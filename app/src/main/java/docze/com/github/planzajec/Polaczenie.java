package docze.com.github.planzajec;

/**
 * Created by Marek
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.String;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.util.Scanner;


public class Polaczenie extends AsyncTask<String, Void, String> {
    String server_response;

    @Override
    protected String doInBackground(String... strings) {
        Log.d("Background", "praca w tle");

        // Create a trust manager that does not validate certificate chains
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
            StreamService ss = new StreamService();
            if (is != null) {
                // Converts Stream to String with max length of 500.
                result = new Scanner(is, "UTF-8").useDelimiter("\\s*</body>\\s*").next();
                Log.d("Odczyt:", result);
            }


        } catch (MalformedURLException e) {
            Log.d("Polaczenie", Log.getStackTraceString(e));
            e.printStackTrace();
        } catch (IOException e){
            Log.d("Polaczenie", Log.getStackTraceString(e));
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.d("Polaczenie", Log.getStackTraceString(e));
        } catch ( KeyManagementException e) {
            Log.d("Polaczenie", Log.getStackTraceString(e));
        }
        return result;
    }
    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }
}