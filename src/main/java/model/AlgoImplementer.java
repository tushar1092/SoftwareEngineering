package model;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Porwal Brother on 4/26/2017.
 */
public class AlgoImplementer implements TradeInterface {
    public ArrayList<Stock> stocksToBuy(ArrayList<Stock> stocksToConsider) {
        try {
            ArrayList<Stock> stocksToBuy = new ArrayList<Stock>();
            for (Stock stock : stocksToConsider) {
                double vals[] = getStats(stock.getName());
                double avgPrice = vals[0];
                double stdDev = vals[1];
                double currentPrice= stock.getBuyPrice();
                double normalizedStandardDev= stdDev/avgPrice;
                if (normalizedStandardDev>.02&&avgPrice-currentPrice>stdDev){
                    stocksToBuy.add(stock);
                }
            }
            return stocksToBuy;
        } catch (SQLException e) {
        }
        return null;
    }

    private double[] getStats(String name) throws SQLException {
        double vals[]={0,0};
        String sql = "Select avgPrice, stddev from " + name + "_Stats where tickerName='"
                + name + "'l";
        ResultSet StdDev = new SQLDBConnection().executeQuery(sql);
        if (StdDev.next()){
            vals[0]=StdDev.getDouble(1);
            vals[1]=StdDev.getDouble(2);
            return vals;
        }
        StdDev.close();
        throw new SQLException("Could not get StdDev from view");
    }


    public ArrayList<Stock> stocksToSell() {
        ArrayList<Stock> stocksToSell = new ArrayList<Stock>();
        ArrayList<Stock> owned= new SQLDBConnection().getOwnedStocks();
        for (Stock stock: owned){
            double currentPrice = stock.getCurrentPrice();
            double tradeFee=9.99;
            double minimumProfitPerSale= 5;
            if ((currentPrice - stock.getBuyPrice()) * stock.getShares() > 2*tradeFee+minimumProfitPerSale) {
                stocksToSell.add(stock);
            }
            else if ((currentPrice-stock.getBuyPrice())*stock.getShares()*-1 > stock.getMaxLoss()){
                stocksToSell.add(stock);
            }

        }
        return null;
    }

    public void updateOwnedStocks() {

    }

}