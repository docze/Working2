package docze.com.github.planzajec;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.os.Build.VERSION_CODES.M;

public class ScheduleDownloader extends AsyncTask<String, Void, Object> {
    private static SSLContext sc;
    private URL url;
    private String nameOfFile = "";
    private Activity act;
    private String sid;
    public ScheduleDownloader(Activity act, URL url, String nameOfFile, String sid) {
        this.act = act;
        this.url = url;
        this.nameOfFile = nameOfFile;
        this.sid = sid.substring(4);
        Log.d("sid", this.sid);
    }

    public void setURL(String url){
        try {
            this.url = new URL(url);
        }catch (MalformedURLException e){
            Log.d("urlError", e.toString());
        }
    };
    public void setNameOfFile(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }

    protected Object doInBackground(String... strings)  {
        Log.d("url", url.toString());
        try {
            downloadFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    private String downloadFile() throws Exception {
            HttpURLConnection urlConnection = (HttpURLConnection)  url.openConnection();

            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if( responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                File file = new File(act.getDir("Grupa_", Context.MODE_PRIVATE)+nameOfFile);
                if(file.exists()){
                    Log.d("usunalem", "plik");
                    file.delete();
                }
                file.createNewFile();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-2"));
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int byteReaders = -1;
                byte[] buffer = new byte[1024];
                byteReaders = inputStream.read(buffer);
                    while ((byteReaders = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteReaders);
                        Log.d("pobieram", byteReaders + "");
                    }
                fileOutputStream.close();
                inputStream.close();
            }else{
                Log.d("Pobranie", "nie udalo sie");
            }
         return null;
    }
    private void trustEveryone() {
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
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
