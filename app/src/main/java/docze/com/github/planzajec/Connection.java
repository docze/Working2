package docze.com.github.planzajec;

/**
 * Created by Marek
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class Connection extends AsyncTask<String, Void, String> {
    String server_response;
    private void     post(){};
    String formname = "login";
    int default_fun = 1;



    @Override
    protected String doInBackground(String... strings) {
        Log.d("Background", "praca w tle");
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        String result = "";
        InputStream is = null;
        URL url;
        HttpsURLConnection urlConnection = null;
        SSLContext sc = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpsURLConnection) url.openConnection();

            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

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
            is = urlConnection.getInputStream();
            if (is != null) {
                // Converts Stream to String with max length of 500.
                result = new Scanner(is, "UTF-8").useDelimiter("\\s*</head>\\s*").next();
                Log.d("Odczyt:", result);
            }


        } catch (MalformedURLException e) {
            Log.d("Connection", Log.getStackTraceString(e));
            e.printStackTrace();
        } catch (IOException e){
            Log.d("Connection", Log.getStackTraceString(e));
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.d("Connection", Log.getStackTraceString(e));
        } catch ( KeyManagementException e) {
            Log.d("Connection", Log.getStackTraceString(e));
        }
        return result;
    }

}