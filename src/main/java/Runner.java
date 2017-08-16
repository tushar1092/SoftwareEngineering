import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.etrade.etws.sdk.common.ETWSException;
import eTrade.Login;
import model.*;
import eTrade.MarketManager;

/**
 * Created by Porwal Brother on 4/16/2017.
 */
public class Runner {

    public static void main(String[] args) throws IOException, ETWSException {
        Login.login();
        SQLDBConnection conn = new SQLDBConnection();

        TradeInterface algorithm = new AlgorithmImplementer();
        TradeInterface algo= new AlgoImplementer();
        ArrayList<String> tickerNames = conn.getFollowedStocks();
        eTrader trader = new eTrader();

        try {
            ArrayList<Stock> stocks = MarketManager.getStockData(tickerNames);
            conn.createViews(stocks);
            while (true) {
                Stock.pushPricesToDB(stocks);
                trader.buy(algorithm.stocksToBuy(stocks));
                trader.sell(algorithm.stocksToSell());
                Thread.sleep(1000);
                stocks=MarketManager.getStockData(tickerNames);
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.err.println(e.getCause());
            e.printStackTrace();

        }
    }



}
