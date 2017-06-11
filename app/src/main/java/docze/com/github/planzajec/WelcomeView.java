package docze.com.github.planzajec;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
     * Metoda obsługująca zdarzenie onclick, na rzecz przycisku @id/show_schedule
     *  w widoku activity_main, pozwala na przejście do ekranu wyboru planu do wyświetlenia
     * @param w - widok activity
     */
    public void chooseScheduleToDisplay(View w){this.startActivity(new Intent(this, ScheduleChooser.class));}

    /**
     * Metoda obsługująca zdarzenie onclick, na rzecz przycisku @id/updateSchedule
     * w widoku activity_main, pozwala na przejście do ekranu logowania
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
