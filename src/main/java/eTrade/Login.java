package eTrade;

import java.awt.Desktop;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Scanner;
import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.common.ETWSException;

/**
 * Created by Porwal Brother on 4/16/2017.
 */
public class Login {
    // Variables

    public static IOAuthClient client = null;
    public static ClientRequest request = null;
    public static Token token = null;
    public static String oauth_consumer_key = "5ea36168462d9f5db6310f0f562aea58";
    public static String oauth_consumer_secret = "3fb77030b0105186336a908c6554f6e0";
    public static String oauth_request_token = null;
    public static String oauth_request_token_secret = null;
    public static String oauth_access_token = null;
    public static String oauth_access_token_secret = null;
    public static String oauth_verify_code = null;

    public static void login() {
        client = OAuthClientImpl.getInstance();
        request = new ClientRequest();
        request.setEnv(Environment.SANDBOX);
        request.setConsumerKey(oauth_consumer_key);
        request.setConsumerSecret(oauth_consumer_secret);
        try {
            token = client.getRequestToken(request);
            oauth_request_token = token.getToken();
            oauth_request_token_secret = token.getSecret();
        } catch (IOException e) {}
        catch (ETWSException e){}
        request.setToken(token.getToken());
        request.setTokenSecret(token.getSecret());
        oauth_verify_code = getVerficationCode();
        getAccessToken();
    }

    private static String getVerficationCode() {
        String authorizeURL = null;
        try {
            authorizeURL = client.getAuthorizeUrl(request);
            URI uri = new java.net.URI(authorizeURL);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        } catch (IOException e ) {
        }catch ( ETWSException e ) {
        }catch (URISyntaxException e) {
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("please input the verification code");
        String authCode = scanner.next();
        scanner.close();
        return authCode;
    }

    private static void getAccessToken() {
        request = new ClientRequest();
        request.setEnv(Environment.SANDBOX);
        request.setConsumerKey(oauth_consumer_key);
        request.setConsumerSecret(oauth_consumer_secret);
        request.setToken(oauth_request_token);
        request.setTokenSecret(oauth_request_token_secret);
        request.setVerifierCode(oauth_verify_code);
        try {
            token = client.getAccessToken(request);
        } catch (IOException e) {
        }
        catch ( ETWSException e) {
        }
        oauth_access_token = token.getToken();
        oauth_access_token_secret = token.getSecret();
    }

    public static ClientRequest getRequest(){
        if (request==null){
            System.out.println("Logging in.  You will have to enter the verification code");
            login();
            request = new ClientRequest(); 
            request.setEnv(Environment.SANDBOX);
            request.setConsumerKey(oauth_consumer_key);
            request.setConsumerSecret(oauth_consumer_secret);
            request.setToken(oauth_access_token);
            request.setTokenSecret(oauth_access_token_secret);
            return request;
        }
        return request;

    }
}
