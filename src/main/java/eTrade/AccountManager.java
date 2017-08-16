package eTrade;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountBalanceResponse;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.account.AccountPositionsRequest;
import com.etrade.etws.account.AccountPositionsResponse;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.common.ETWSException;

/**
 * Created by Porwal Brother on 4/16/2017.
 */
public class AccountManager {
    public static List<Account> getAccounts() throws IOException, ETWSException {
        AccountsClient account_client=getAccountsClient();
            AccountListResponse response = account_client.getAccountList();
            account_client.getAccountList();
            List<Account> alist = response.getResponse();
            return alist;

    }

    private static AccountsClient getAccountsClient(){
        ClientRequest r = Login.getRequest();
        return new AccountsClient(r);
    }

    public static AccountBalanceResponse getAccountBalance(Account a) throws IOException, ETWSException {
        return getAccountBalance(a.getAccountId());
    }

    public static AccountBalanceResponse getAccountBalance(String accountID) throws IOException, ETWSException {

            return getAccountsClient().getAccountBalance(accountID);
       }

    public static AccountPositionsResponse getAcctPosition(String acct){
        AccountsClient client=getAccountsClient();
        AccountPositionsResponse aprs = null;
        AccountPositionsRequest apr = new AccountPositionsRequest();
        apr.setSymbol("GOOG");
        apr.setTypeCode("EQ");
        try {
            aprs = client.getAccountPositions(acct, apr);
        } catch (IOException e) {
        }
        catch (ETWSException e){
        }
        return aprs;
    }
    public static AccountPositionsResponse getAcctPosition(Account a){
        return getAcctPosition(a.getAccountId());
    }

}
