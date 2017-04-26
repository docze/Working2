package docze.com.github.planzajec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Connection extends AsyncTask<String, Void, Object> {
    private final String FORMNAME = "login";
    private final int DEFAULT_FUN = 1;
    private final String USER_AGENT = "Mozilla/5.0";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String CONNECTION = "akeep-alive";
    public final static String EXTRA_MESSAGE = "docze.com.github.planzajec.MESSAGE";
    private static SSLContext sc;
    private Activity act;
    ProgressDialog progDailog;

    public Connection (Activity act){
        this.act = act;
        this.progDailog = new ProgressDialog(act);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog.setMessage("Loading...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }

    @Override
    protected Object doInBackground(String... strings) {
        CookieHandler.setDefault(new CookieManager());

        try {
            String homePageContent = getPageContent(strings[0]);
            String sid = getsid(homePageContent);
            String urlParameters = "formname="+ URLEncoder.encode(FORMNAME, "UTF-8")+"&default_fun="+
                    URLEncoder.encode(DEFAULT_FUN+"", "UTF-8")+"&userid="+URLEncoder.encode(strings[1], "UTF-8")+
                    "&password="+URLEncoder.encode(strings[2], "UTF-8");
            String returnedPage = sendPost(strings[0]+"index.php?"+sid, urlParameters);
            if(returnedPage.contains("logged_inc.php")) {
                String afterLogPageContent = getPageContent(strings[0] + "logged_inc.php?" + sid + "&t=6799847");
                String groupName = getGroupName(afterLogPageContent).first().text().substring(0,6);
                System.out.println(groupName);
                Intent intent = new Intent(act, Logged_user.class);
                intent.putExtra(EXTRA_MESSAGE, groupName);
                act.startActivity(intent);

            } else {
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(act, "Nie udało się zalogować", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progDailog.dismiss();
    }

    private String getPageContent(String page) throws IOException {
        URL url = new URL(page);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        trustEveryone();

        urlConnection.setSSLSocketFactory(sc.getSocketFactory());
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Connection", CONNECTION);
        urlConnection.setRequestProperty("Content-Type", CONTENT_TYPE);
        urlConnection.setRequestProperty("User-Agent",USER_AGENT);

        String content = "";
        try {
            urlConnection.connect();
            content = getBodyContent(urlConnection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return content;
    }

    private String sendPost(String page, String postData) throws Exception {
        URL url = new URL(page);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        trustEveryone();

        urlConnection.setSSLSocketFactory(sc.getSocketFactory());
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setFixedLengthStreamingMode(postData.getBytes().length);
        urlConnection.setRequestProperty("Content-Type", CONTENT_TYPE);

        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        String returnedPage = "";
        try {
            urlConnection.connect();
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postData);
            out.close();
            urlConnection.getInputStream();
            returnedPage = urlConnection.getURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return returnedPage;
    }

    private String getsid(String html) {
        Document doc = Jsoup.parse(html);
        Element element = doc.select("form").first();
        return element.attr("action").substring(10);
    }

    private Elements getGroupName(String html){
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsMatchingOwnText("[A-Z][0-9][A-Z][0-9][A-Z][0-9]");
        return elements;
        /*Element element = doc.select("td").get(2);
        return element.text().substring(39,45);*/
    }

    private String getBodyContent(InputStream is) throws  Exception{
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "ISO-8859-2"));
        String inputLine;
        String body = "";

        // int flag = 0;
        while ((inputLine = in.readLine()) != null) {
            if(inputLine.contains("<body")){
                body += inputLine;
            }
            /*
            if(inputLine.contains("<body")){
                flag = 1;
            }
            if(flag == 1){
                body += inputLine;
            }
            */
        }

        in.close();
        return body;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}