package docze.com.github.planzajec;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

/**
 * Klasa odpowiedzialna za stworzenie i obsluge welcome_view.xml
 */
public class WelcomeView extends AppCompatActivity {

    /**
     * Metoda odpowiedzialna za stworzenie widoku
     * @param savedInstanceState - informacja na temat stanu activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);
    }
    /**
     * Metoda obsługująca zdarzenie onclick, na rzecz przycisku @id/showSchedule
     * w widoku activity_main, pozwala na przejście do widoku kalendarza
     * @param w - widok activity
     */
    public void showSchedule(View w){
        String groupName = getIntent().getStringExtra(Connection.EXTRA_MESSAGE);
        if((new File(this.getDir("Grupa_", Context.MODE_PRIVATE)+"I5Y2S1.txt")).exists()){
            this.startActivity(new Intent(this, DisplayCalendar.class));
        } else {
            final Activity act = this;
            act.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(act, "Brak planu. Zaktualizuj.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    /**
     * Metoda obsługująca zdarzenie onclick, na rzecz przycisku @id/updateSchedule
     * w widoku activity_main, pozwala na przejście do erkanu logowania
     * @param w - widok activity
     */
    public void updateSchedule(View w){
        this.startActivity(new Intent(this, Logging.class));
    }
    /**
     * Metoda obsługująca zdarzenie onclick, na rzecz przycisku @id/fileList
     * w widoku activity_main, pozwala na przejście do listy plików do pobrania
     * @param w - widok activity
     */
    public void showFileList(View w){
        this.startActivity(new Intent(this, FileList.class));
    }
}
