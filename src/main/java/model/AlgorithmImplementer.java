package model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Porwal Brother on 4/26/2017.
 */
public class AlgorithmImplementer implements TradeInterface {
    SQLDBConnection conn;
    private double tradeFee = 9.99;
    private int minimumInvestment = 2500;//Play around with this
    private double maxWaste = 100;
    ArrayList<Stock> stocks;
    private double minimumProfitPerSale;

    public AlgorithmImplementer() {
        this.conn = new SQLDBConnection();
        updateOwnedStocks();
    }


    public void updateOwnedStocks() {
        this.stocks = this.conn.getOwnedStocks();
    }

    public ArrayList<Stock> stocksToBuy(ArrayList<Stock> stocksToConsider) {
        ArrayList<Stock> stocksToBuy = new ArrayList<Stock>();
        double currentCash = this.conn.getCurrentCash();
        if (currentCash > minimumInvestment) {
            for (Stock stock : stocksToConsider) {
                double currentPrice = stock.getBuyPrice();//buy price is currently market price
                int shares = (int) Math.floor((currentCash - tradeFee) / currentPrice);
                if (currentCash - shares * currentPrice < maxWaste && currentPrice < stock.getMostRecentSalePrice()) {
                    stock.setShares(shares);
                    stock.setBuyPrice(currentPrice);
                    stock.setBuyTime(new Date());
                    stocksToBuy.add(stock);
                }
            }
        }

        return stocksToBuy;
    }

    public ArrayList<Stock> stocksToSell() {
        ArrayList<Stock> stocksToSell = new ArrayList<Stock>();
        for (Stock stock : this.stocks) {
            double currentPrice = stock.getCurrentPrice();
            if ((currentPrice - stock.getBuyPrice()) * stock.getShares() > 2 * tradeFee + minimumProfitPerSale) {
                stocksToSell.add(stock);
            } else if ((currentPrice - stock.getBuyPrice()) * stock.getShares() * -1 > stock.getMaxLoss()) {
                stocksToSell.add(stock);
            }
        }
        return stocksToSell;
    }
}



