package model;

/**
 * Created by Porwal Brother on 4/16/2017.
 */
        import java.sql.*;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.text.DateFormat;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Properties;


public class SQLDBConnection {

    private Connection conn;
    private Statement stmt;

    public SQLDBConnection() {
        Properties prop = new Properties();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:test.db" );
            System.out.println("Opened database successfully");
            this.stmt = this.conn.createStatement();
        } catch (ClassNotFoundException e){
            System.err.println("Exception in constructor: " + e.getMessage());
            e.printStackTrace();
        }
        catch(SQLException e){
            System.err.println("Exception in constructor: " + e.getMessage());
        }

        }


    protected double getCurrentCash() {
        ResultSet moneySet = executeQuery("select * from Current_Cash");
        try {
            moneySet.next();
            return moneySet.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Failed to get cash from DB");
            System.err.println(e.getStackTrace());
            System.err.println(e.getMessage());

        }
        return -1;
    }

    public int executeUpdate(String sql) {
        int toRet = -1;
        try {
            if (!hasOpenStatementAndConnection())
                reopenConnectionAndStatement();
            return this.stmt.executeUpdate(sql);
        } catch (SQLException exception) {
            System.err.println("SQLException: sql = " + sql);
            exception.printStackTrace();
            System.exit(-1);
        }
        return toRet;
    }

    public ResultSet executeQuery(String sql) {
        ResultSet result = null;
        try {
            if (!hasOpenStatementAndConnection())
                reopenConnectionAndStatement();
            result = this.stmt.executeQuery(sql);
        } catch (SQLException exception) {
            System.err.println("SQLException: sql = " + sql);
            exception.printStackTrace();
        }
        return result;
    }

    private boolean hasOpenStatementAndConnection() throws SQLException {
        return !this.conn.isClosed();
    }

    private void reopenConnectionAndStatement() {
        try {
            if (this.conn == null || this.conn.isClosed())
                this.conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            if (this.stmt == null || this.stmt.isClosed())
                this.stmt = this.conn.createStatement();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected ArrayList<Stock> getOwnedStocks() {
        ArrayList<Stock> stocks = new ArrayList<Stock>();
        ResultSet rows = executeQuery("select * from Owned_Stocks;");
        try {
            while (rows.next()) {
                String name = rows.getString("tickerName");
                double buyPrice = Double.valueOf(rows.getString("buyPrice"));
                int shares = Integer.valueOf(rows.getString("shares"));
                DateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                Date buyDate = null;
                String stringBuyDate = rows.getString("buyDate").substring(0,
                        19);
                buyDate = dateFormat.parse(stringBuyDate);
                stocks.add(new Stock(name, buyPrice, shares, buyDate));
            }
            rows.close();
        } catch (ParseException exception) {
            System.err.println("Error parsing date from DB");
            exception.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL exception caused by getString()");
            e.printStackTrace();
        }
        return stocks;
    }
    public ArrayList<String> getFollowedStocks() {
        ArrayList<String> stocks = new ArrayList<String>();
        ResultSet rows = executeQuery("Select * from Followed_Stocks");
        try {
            while (rows.next()) {

                String name = rows.getString("tickerName");
                stocks.add(name);

            }
            rows.close();
        } catch (SQLException e) {
            System.err.println("SQL exception caused by getString()");
            e.printStackTrace();
            System.exit(-1);
        }
        return stocks;
    }

    protected static ArrayList<String> convertOneDimensionResultSetToArrayList(
            ResultSet names) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            while (names.next()) {
                result.add(names.getString(1));
            }
            names.close();
        } catch (SQLException e) {
            System.err
                    .println("exception in convertOneDimensionResultSetToArrayList");
            e.printStackTrace();
        }
        return result;
    }

    public void createViews(ArrayList<Stock> tickerNames) {
        String sql, sql2;
        for (Stock viewName : tickerNames) {
            sql = "create or replace view " + viewName.getName()
                    + "_Prices as " + "select * from Ticker_Prices "
                    + "where Ticker_Prices.tickerName='" + viewName.getName()
                    + "' " + "order by Ticker_Prices.time desc;";
            executeUpdate(sql);
            sql2 ="create or replace view " + viewName.getName()
                    + "_Stats as " + "select DISTINCT tickerName, AVG(price) as avgPrice, STDDEV(price) as stddev from "+ viewName.getName()
                    + "_Prices where tickerName='" + viewName.getName()
                    + "';";
            executeUpdate(sql2);
        }
    }

}

