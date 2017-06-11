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
 * Klasa rozszerzajaca klase AsyncTask. AsyncTask to asnychroniczne zadanie
 * wykonywane w watku tla. Zadanie wykonywane jest poprzez wywolanie metody execute.
 * Po zakonczeniu wykonywania zadania, nie mozna wykonac go ponownie.
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
    /** Obiekt przechowujacy parametry protokolu SSL*/
    private static SSLContext sc;
    /** Activity, w ktorym jest wykonywane polaczenie*/
    private Activity act;
    /** Obiekt okna dialogowego */
    ProgressDialog progDailog;

    /**
     * @param act   pozwala na wyswietlanie powiadomien i paska postępu,
     *              na activity, ktore wywolalo metode execute na rzecz
     *              obiektu klasy Connection
     */
    public Connection (Activity act){
        this.act = act;
        this.progDailog = new ProgressDialog(act);
    }

    /**
     * Metoda odpowiedzialna za wyswietlanie okna dialogowego ladowania
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
     *  Metoda obslugujaca prace w tle. Odpowiedzialna za sekwencyjne wykonywanie zadania
     *
     * @param strings Tablica napisow przechowujaca adres strony logowania, login oraz haslo
     * @return null
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
     * Metoda odpowiedzialna za zamkniecie okna dialogowego ladowania, po wykonaniu zadania
     * @param o obiekt typu zwracanego przez zadanie asynchroniczne
     */
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progDailog.dismiss();
    }

    /**
     * Wykonuje zapytanie GET na adres wskazany w parametrze page w celu pobrania klucza sesji
     * oraz niezbednych parametrow do zalogowania sie
     * @param page adres strony, na ktory ma zostac wykonane zapytanie GET
     * @return odpowiedz od strony, kod zrodłowy HTML
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
     * Metoda wykonuje zapytanie post w celu zalogowania sie do edziekanatu
     * @param page adres strony na ktory jest wykonywane zapytanie POST
     * @param postData przetworzone dane formularza
     * @return zwraca odpowiedz strony, kod zrodłowy HTML
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
     * Metoda wynajduje w kodzie zrodłowym id sesji
     * @param html kod zrodłowy strony
     * @return id sesji
     */
    private String getsid(String html) {
        Document doc = Jsoup.parse(html);
        Element element = doc.select("form").first();
        return element.attr("action").substring(10);
    }

    /**
     * Metoda wyszukuje numer grupy zalogowowanego studenta
     * @param html kod zrodlowy strony
     * @return numer grupy zalogowanego studenta
     */
    private Elements getGroupName(String html){
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsMatchingOwnText("[A-Z][0-9][A-Z][0-9][A-Z][0-9]");
        return elements;
    }

    /**
     * Metoda zapisuje <body></body> kodu zrodłowego ze strumienia wejsciowego
     * @param is strumien wejsciowy, odbior odpowiedzi strony
     * @return <body></body> kodu zrodlowego
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
     * Metoda powoduje wylaczenie sprawdzania certyfikatow strony https
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