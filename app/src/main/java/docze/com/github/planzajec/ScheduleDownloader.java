package docze.com.github.planzajec;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Klasa rozszerzająca klasę AsyncTask. AsyncTask to asnychroniczne zadanie
 * wykonywane w wątku tła. Zadanie wykonywane jest poprzez wywołanie metody execute.
 * Po zakończeniu wykonywania zadania, nie można wykonać go ponownie.
 * Pozwala na polaczenie sie ze strona http oraz pobranie plików
 **/

public class ScheduleDownloader extends AsyncTask<String, Void, Object> {
    /** Adres strony */
    private URL url;
    /** Nazwa pliku do pobrania */
    private String nameOfFile = "";
    /** Obiekt activity, ktore wywołuje zadanie */
    private Activity act;
    /** Id sesji */
    private String sid;

    /**
     * Konstruktor zadania, pozwalana na zainicjalizowanie atrybutów klasy
     * @param act activity, ktore wywoluje zadanie
     * @param url adres strony
     * @param nameOfFile nazwa pliku do pobrania
     * @param sid id sesji
     */
    public ScheduleDownloader(Activity act, URL url, String nameOfFile, String sid) {
        this.act = act;
        this.url = url;
        this.nameOfFile = nameOfFile;
        this.sid = sid.substring(4);
    }

    /**
     *
     *  Metoda obsługująca pracę w tle. Odpowiedzialna za sekwencyjne wykonywanie zadania
     * @param strings - tablica parametrów typu String przesylanych podczas wykonania
     *                 metody execute, w tym przypadku nie jest wykorzystywana, ale klasa AsyncTask
     *                 wymaga takiej konstrukcji.
     * @return
     */
    protected Object doInBackground(String... strings)  {
        try {
            downloadFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Metoda pobiera plik z zadanego adresu http
     * @throws Exception
     */
    private void downloadFile() throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection)  url.openConnection();
        urlConnection.setDoInput(true);
        int responseCode = urlConnection.getResponseCode();
        if( responseCode == HttpsURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            File file = new File(act.getDir("Grupa_", Context.MODE_PRIVATE)+nameOfFile);

            if(file.exists()){
                file.delete();
            }

            file.createNewFile();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-2"));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int byteReaders = -1;
            byte[] buffer = new byte[1024];

            while ((byteReaders = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteReaders);
            }

            fileOutputStream.close();
            inputStream.close();
        }else{
            Log.d("Pobranie", "nie udalo sie");
        }
    }
}
