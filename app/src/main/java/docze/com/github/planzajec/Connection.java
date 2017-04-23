package docze.com.github.planzajec;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Connection extends AsyncTask<String, Void, Object> {

    private final String FORMNAME = "login";
    private final int DEFAULT_FUN = 1;
    private static SSLContext sc = null;
    private final String USER_AGENT = "Mozilla/5.0";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String CONNECTION = "akeep-alive";
    private static String sid ="";

    private List<String> cookies = new ArrayList<String>();

    @Override
    protected Object doInBackground(String... strings) {
        Log.d("Background", "working in background");
        CookieHandler.setDefault(new CookieManager());
        String result = "";

        try {
            result = getPageContent(strings[0]);
//            Log.d("wynik", result);
            getsid(result);
            Log.d("sid", sid);
            String  urlParameters = "formname="+ URLEncoder.encode(FORMNAME, "UTF-8")+"&default_fun="+
                    URLEncoder.encode(DEFAULT_FUN+"", "UTF-8")+"&userid="+URLEncoder.encode(strings[1], "UTF-8")+
                    "&password="+URLEncoder.encode(strings[2], "UTF-8");
        //    Log.d("urlParameters", urlParameters);
            sendPost(strings[0]+"index.php?"+sid, urlParameters);
            result = getPageContent(strings[0]+"logged_inc.php?"+sid+"&t=6799847");
            Log.d("After post", result);
        }catch(Exception e) {
        }

        return null;
    }

    private String getPageContent(String page) throws Exception{
        String result = "";
        Log.d("getPageContent ", page);
        URL url = new URL(page);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        trustEveryone();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        Log.d("result", result);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", CONNECTION);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("User-Agent",USER_AGENT);
        conn.connect();
        result = readContent(conn.getInputStream());


        return result;
    }
    private static void sendPost(String page, String postData) throws Exception {

        Log.d("postPageContent ", page);
        URL url;
        HttpsURLConnection urlConnection = null;
        url = new URL(page);
        urlConnection = (HttpsURLConnection) url.openConnection();
        trustEveryone();

        urlConnection.setSSLSocketFactory(sc.getSocketFactory());
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setFixedLengthStreamingMode(postData.getBytes().length);
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();

        PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
        out.print(postData);
        out.close();
        String resposne = "";
        //Scanner in = new Scanner(urlConnection.getInputStream());

        resposne = readContent(urlConnection.getInputStream());
        Log.d("post code", urlConnection.getResponseCode()+"");
    }

    private void getsid(String html) {
        String result;
        Document doc = Jsoup.parse(html);
        Element element = doc.select("form").first();
        sid = element.attr("action").substring(10);
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
    private  static String readContent(InputStream is) throws  Exception{
        BufferedReader in =
                new BufferedReader(new InputStreamReader(is, "ISO-8859-2"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        String all = "";
        int flaga = 0;
        FileOutputStream fOut;
        while ((inputLine = in.readLine()) != null) {
            if(inputLine.contains("<body")) flaga = 1;
            if(flaga > 0) all += inputLine;

        }
        in.close();
        return all;
    }

}