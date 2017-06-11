package docze.com.github.planzajec;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa rozszerzająca klasę AsyncTask. AsyncTask to asnychroniczne zadanie
 * wykonywane w wątku tła. Zadanie wykonywane jest poprzez wywołanie metody execute.
 * Po zakończeniu wykonywania zadania, nie można wykonać go ponownie.
 * Pozwala na polaczenie sie z serwerem FTP
 *
 */

public class FTPConnection extends AsyncTask<String, Void, List<String> > {
    /** login użytkownika serwera FTP */
    private String login = "planzajec";
    /** hasło użytkownika serwera FTP */
    private String password = "watjestokej";

    /**
     * Metoda odpowiedzialna za utworzenie polaczenia z serwerem FTP oraz
     * utworzenia listy plików do pobrania
     * @param params - tablica obiektów typu String, przechowuje adres serwera FTP
     * @return
     */
    @Override
    protected List<String> doInBackground(String... params) {
        FTPClient ftpClient = new FTPClient();
        FTPFile[] ftpFiles = null;
        List<String> checkBoxes = null;

        try {
            ftpClient.connect(params[0], 21);
            boolean success = ftpClient.login(login, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.type(FTP.BINARY_FILE_TYPE);
                if(success){
                    success = ftpClient.changeWorkingDirectory("/wat");
                    if(success){
                        checkBoxes = new ArrayList<>();
                        ftpFiles = ftpClient.listFiles();

                        for (FTPFile file : ftpFiles) {
                            String details = file.getName();
                            checkBoxes.add(details);
                        }
                    }
                }else{
                    Log.d("Zmiana folderu", "nie powiodla sie");
                    return null;
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return checkBoxes;
        }
    }
}
