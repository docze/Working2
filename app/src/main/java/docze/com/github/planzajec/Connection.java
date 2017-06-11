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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Klasa rozszerzająca klasę AsyncTask. AsyncTask to asnychroniczne zadanie
 * wykonywane w wątku tła. Zadanie wykonywane jest poprzez wywołanie metody execute.
 * Po zakończeniu wykonywania zadania, nie można wykonać go ponownie.
 * Pozwala na polaczenie sie ze strona logowania www.s1.wcy.wat.edu.pl/en/
 * Nastepnie wykonuje logowanie do wyzej wymienionej strony.
 * Po zakoczeniu pracy przechodzi do activity DisplayCalendar
 */

public class Connection extends AsyncTask<String, Void, Object> {
    /** Klucz zapytania podczas logowania */
    private final String FORMNAME = "login";
    /** Klucz zapytania podczas logowania */
    private final int DEFAULT_FUN = 1;
    /** Klucz zapytania podczas logowania */
    private final String USER_AGENT = "Mozilla/5.0";
    /** Klucz zapytania podczas logowania */
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    /** Klucz zapytania podczas logowania */
    private final String CONNECTION = "akeep-alive";
    /** Obiekt przechowujacy parametry protokołu SSL*/
    private static SSLContext sc;
    private Activity act;
    ProgressDialog progDailog;

    /** Konstruktor klasy
     *
     * @param act   pozwala na wyświetlanie powiadomień i paska postępu,
     *              na activity, które wywołało metodę execute na rzecz
     *              obiektu klasy Connection
     */
    public Connection (Activity act){
        this.act = act;
        this.progDailog = new ProgressDialog(act);
    }

    /**
     * Metoda odpowiedzialna za wyświetlanie okna dialogowego ładowania
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog.setMessage("Loading...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }

    /**
     *  Metoda obsługująca pracę w tle. Odpowiedzialna za sekwencyjne wykonywanie zadania
     *
     * @param strings - Tablica stringów przechowują adres strony logowania, logino oraz hasło
     * @return
     */
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

                URL url = null;

                try{
                    url = new URL("http://az-serwer1701230.online.pro/wat/"+groupName+".txt");
                }catch (MalformedURLException e){

                }

                final ScheduleDownloader scheduleDownloader = new ScheduleDownloader(act, url, groupName+".txt", sid);
                act.runOnUiThread(new Runnable() {
                    public void run(){
                        scheduleDownloader.execute();
                    }
                });

                act.startActivity(new Intent(act, ScheduleChooser.class));

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

    /**
     * Metoda odpowiedzialna za zamknięcia okna dialogowego ładowania, po wykonaniu zadania
     * @param o -  obiekt typu zwracanego przez zadanie asynchroniczne
     */
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progDailog.dismiss();
    }

    /**
     * Wykonuje zapytanie GET na adres wskazany w parametrze page w celu pobrania klucza sesji
     * oraz niezbędnych parametrów do zalogwania się
     * @param page - adres strony, na który ma zostać wykonane zapytanie GET
     * @return - odpowiedź od strony, kod źródłowy HTML
     * @throws IOException
     */
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

    /**
     * Metoda wykonuje zapytanie post w celu zalogowania się do edziekanatu
     * @param page - adres strony na który jest wykonywane zapytanie POST
     * @param postData - przetworzone dane formularza
     * @return - zwraca odpowiedź strony, kod źródłowy HTML
     * @throws Exception
     */
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

    /**
     * Metoda wynajduje w kodzie źródłowym id sesji
     * @param html - kod źródłowy strony
     * @return - zwraca id sesji
     */
    private String getsid(String html) {
        Document doc = Jsoup.parse(html);
        Element element = doc.select("form").first();
        return element.attr("action").substring(10);
    }

    /**
     * Metoda wyszukuje numer grupy zalogowowanego studenta
     * @param html - kod źródłowy strony
     * @return - numer grupy zalogowanego studenta
     */
    private Elements getGroupName(String html){
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsMatchingOwnText("[A-Z][0-9][A-Z][0-9][A-Z][0-9]");
        return elements;
    }

    /**
     * Metoda zapisuje jedynie <body></body> kodu źródłowego ze strumienia wejściowego
     * @param is - strumień wejściowy, odbior odpowiedzi strony
     * @return - zwraca <body></body> kodu źródłowego
     * @throws Exception
     */
    private String getBodyContent(InputStream is) throws  Exception{
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "ISO-8859-2"));
        String inputLine;
        String body = "";
        while ((inputLine = in.readLine()) != null) {
            if(inputLine.contains("<body")){
                body += inputLine;
            }
        }

        in.close();
        return body;
    }
    /**
     * Metoda powoduje wyłączenie sprawdzania certyfikatów strony https
     */
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