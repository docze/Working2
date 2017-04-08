package docze.com.github.planzajec;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Connection extends AsyncTask<String, Void, Object> {

    private final String FORMNAME = "login";
    private final int DEFAULT_FUN = 1;
    private static SSLContext sc = null;

    @Override
    protected Object doInBackground(String... strings) {
        Log.d("Background", "praca w tle");

        URL url;
        HttpsURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpsURLConnection) url.openConnection();

            trustEveryone();

            urlConnection.setSSLSocketFactory(sc.getSocketFactory());
            urlConnection.setReadTimeout(7000);
            urlConnection.setConnectTimeout(7000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
        } catch (MalformedURLException e) {
            Log.d("Connection", Log.getStackTraceString(e));
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Connection", Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return null;
    }

    private static void trustEveryone() {

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {};
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {};
                }
        };

        try {
            sc  = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}