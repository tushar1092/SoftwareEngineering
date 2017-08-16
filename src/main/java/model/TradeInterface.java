package model;

import java.util.ArrayList;

/**
 * Created by Porwal Brother on 4/26/2017.
 */
public interface TradeInterface {
    abstract ArrayList<Stock> stocksToBuy(ArrayList<Stock> stocksToConsider) ;

    ArrayList<Stock> stocksToSell();

    void updateOwnedStocks();

}
