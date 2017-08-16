package model;

/**
 * Created by Porwal Brother on 4/16/2017.
 */
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.etrade.etws.account.Account;
import com.etrade.etws.order.PlaceEquityOrderResponse;

import com.etrade.etws.sdk.common.ETWSException;
import eTrade.AccountManager;
import eTrade.OrderManager;

public class eTrader {
    private SQLDBConnection conn = new SQLDBConnection();
    private double tradeFee = 9.99;//hard coded trading fee
    private Account acct;

    public eTrader() throws IOException, ETWSException {
        this.acct = getAccount();
    }

    public void buy(ArrayList<Stock> stocksToBuy) {
        for (Stock stock : stocksToBuy) {
            if (allowTrade(stock)) {
                updateDBAndLog(stock, "buy");
                addStockToTable(stock);
                PlaceEquityOrderResponse response = OrderManager.buy(stock, this.acct);
                return;
            }
        }
    }


    private void updateDBAndLog(Stock stock, String s) {
        logTransaction(stock, s);
        updateCash(stock, s);
    }


    public void sell(ArrayList<Stock> stocksToSell) {
        for (Stock stock : stocksToSell) {
            if (allowTrade(stock)) {
                stock.setSellPrice(stock.getCurrentPrice());
                updateMostRecentSellPrice(stock);
                removeStockFromTable(stock);
                updateDBAndLog(stock, "sell");
                PlaceEquityOrderResponse response = OrderManager.sell(stock, this.acct);

            }
        }
    }

    private void updateMostRecentSellPrice(Stock stock) {
        String sql = "Update Followed_Stocks Set mostRecentSellPrice = " + stock.getCurrentPrice() + "where tickerName = '" + stock.getName() + "';";
        this.conn.executeUpdate(sql);
    }


    private void updateCash(Stock stock, String buy_sell) {
        if (buy_sell.equals("sell")) {
            String sql = "Update Current_Cash Set currentCash=currentCash+" + (stock.getSellPrice() * stock.getShares() - tradeFee) + ";";
            this.conn.executeUpdate(sql);
        } else if (buy_sell.equals("buy")) {
            String sql = "Update Current_Cash Set currentCash=currentCash-" + (stock.getBuyPrice() * stock.getShares() + tradeFee) + ";";
            this.conn.executeUpdate(sql);
        }
    }

    private boolean allowTrade(Stock s) {
        return true;
    }

    private boolean maxNumDayTradesReached() {
        cleanUpDayTradesTable();
        String sql = "Select buyTime from Day_Trades;";
        ResultSet results = this.conn.executeQuery(sql);
        int resultCount = 0;
        try {
            while (results.next())
                resultCount++;
        } catch (SQLException exception) {
            System.err.println("SQLException: sql = " + sql);
            exception.printStackTrace();
            System.exit(-1);
        }
        return resultCount > 3;
    }

    private void incrementDayTradesTable(Stock s) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String sql = "Insert into Day_Trades(tickerName, buyTime, sellTime) values ('"
                + s.getName()
                + "', '"
                + dateFormat.format(s.getBuyTime())
                + "', '"
                + dateFormat.format(date) + "');";
        this.conn.executeUpdate(sql);
    }

    private void cleanUpDayTradesTable() {// ideally, we only need to run this
        // 1x a day.
        boolean oldTradesInTable = true;
        String sql = null;
        ResultSet oldest;
        while (oldTradesInTable) {
            try {
                sql = "Select buyTime from Day_Trades order by buyTime Limit 1";// get
                // the
                // oldest
                oldest = this.conn.executeQuery(sql);
                if (!oldest.isBeforeFirst() && !oldest.isAfterLast())//empty result set
                    break;
                oldest.next();
                int businessDays = countBusinessDays(
                        getDateFromString(oldest.getString(1)), new Date());
                if (businessDays > 5) {
                    sql = "Delete from Day_Trades where buyTime='"
                            + oldest.getString(1) + "';";
                    this.conn.executeUpdate(sql);
                } else
                    oldTradesInTable = false;
            } catch (SQLException exception) {
                System.err.println("Exception caused by next()");
                exception.printStackTrace();
                System.exit(-1);
            }

        }
    }

    public void logTransaction(Stock stock, String buyOrSell) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        int saleFlag = (buyOrSell.equals("buy") ? -1 : 1);
        int shares = stock.getShares();
        double currentPrice = stock.getCurrentPrice();
        String sql = "Insert into Transaction_History (tickerName, price, shares, BUY_SELL, gross, net, time) Values ('"
                + stock.getName()
                + "', "
                + currentPrice
                + ", "
                + stock.getShares()
                + ", '"
                + buyOrSell
                + "', "
                + currentPrice * shares * saleFlag
                + ", "
                + (((currentPrice - stock.getBuyPrice()) * shares) - tradeFee)
                + ", '"
                + dateFormat.format(date) + "');";

        this.conn.executeUpdate(sql);
    }

    private void removeStockFromTable(Stock stock) {
        String sql = "Delete from Owned_Stocks where tickerName='" + stock.getName()
                + "';";
        this.conn.executeUpdate(sql);
    }

    private void addStockToTable(Stock stock) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String sql = "Insert into Owned_Stocks VALUES ('" + stock.getName() + "', " + stock.getBuyPrice() + ", " + stock.getShares() + ", " + 10 + ", '" + dateFormat.format(stock.getBuyTime()) + "');";
        this.conn.executeUpdate(sql);

    }

    private Date getDateFromString(String s) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(s.substring(0, 19));
        } catch (ParseException exception) {
            System.err.println("Error parsing date from DB");
            exception.printStackTrace();
            System.exit(-1);
        }
        return date;
    }


    public static int countBusinessDays(Date start, Date end) {
        // Ignore argument check

        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        int w1 = c1.get(Calendar.DAY_OF_WEEK);
        c1.add(Calendar.DAY_OF_WEEK, -w1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        int w2 = c2.get(Calendar.DAY_OF_WEEK);
        c2.add(Calendar.DAY_OF_WEEK, -w2);

        // end Saturday to start Saturday
        long days = (c2.getTimeInMillis() - c1.getTimeInMillis())
                / (1000 * 60 * 60 * 24);
        long daysWithoutSunday = days - (days * 2 / 7);

        return (int) (daysWithoutSunday - w1 + w2);
    }

    /**
     * TODO Put here a description of what this method does.
     *
     * @return
     */
    private Account getAccount() throws IOException, ETWSException {
        List<Account> accounts = AccountManager.getAccounts();
        Iterator<Account> it = accounts.iterator();
        while (it.hasNext()) {
            //insert some logic in here to pick out the correct account
            return it.next();
        }
        return  null;
    }
}



