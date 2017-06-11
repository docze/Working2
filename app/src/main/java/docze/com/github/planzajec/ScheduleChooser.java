package docze.com.github.planzajec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *  Klasa pozwalajacąca wybrać, jaki plan wyświetlić użytkownikowi.
 *  Po wciśnięciu odpowiedniego przycisku odsyła do widoku kalendarza
 */
public class ScheduleChooser extends AppCompatActivity {
    /** Pole pozwalające na przesłanie wiadomości do innego Activity */
    public final static String EXTRA_MESSAGE = "docze.com.github.planzajec.MESSAGE";
    /** Lista grup, których plan jest dostępny do wyświetlenia */
    List<String> groupList;

    /** Metoda uruchamiana przy tworzeniu Activity. Znajduje dostępne w telefonie
     *  zajęcia, które można wyświetlić oraz tworzy dla nich przyciski.
     *
     *  @param savedInstanceState zachowany stan instancji
     *  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_schedule);

        findSchedules();
        fillList();
    }

    /** Metoda odpowiedzialna za utworzenie i wypełnienie listy dostępnych w telefonie planów zajęć
     *  poprzez przeszukanie katalogu plików.
     */
    private void findSchedules(){
        groupList = new ArrayList<>();

        File dir = new File(this.getDir("", Context.MODE_PRIVATE)+""); // /data/data/docze.com.github.planzajec/app_
        File parentDir = dir.getParentFile(); // /data/data/docze.com.github.planzajec
        final Pattern p = Pattern.compile("app_Grupa_[A-Z][0-9][A-Z][0-9][A-Z][0-9].txt");

        File [] files = parentDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return p.matcher(name).matches();
            }
        });

        for (File schedule : files) {
            String fileName = schedule.getName();
            groupList.add(fileName.substring(fileName.indexOf("Grupa_")+6, fileName.indexOf(".txt")));
        }
    }

    /** Metoda odpowiedzialna za utworzenie przycisków dla wszystkich grup,
     *  które znajdują się na liście groupList
     */
    private void fillList(){
        LinearLayout ll = (LinearLayout)  findViewById(R.id.listGroup);
        for(String g: groupList){
            Button button = new Button(this.getApplicationContext());
            button.setTextColor(Color.BLACK);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            button.setText(g);
            ll.addView(button);
            button.setOnClickListener(isClicked);
        }
    }

    /** Słuchacz wywoływany po kliknięciu w przycisk grupy. Zczytuje
     *  z przycisku nazwę grupy i wywołuje metodę showSchedule()
     */
    private View.OnClickListener isClicked = new View.OnClickListener() {
        public void onClick(View v) {
            Button b = (Button) v;
            showSchedule(b.getText().toString());
        }
    };

    /**
     * Metoda obsługująca zdarzenie onclick, na rzecz przycisku @id/showSchedule
     * w widoku activity_main, pozwala na przejście do widoku kalendarza
     * @param groupName - nazwa grupy
     */
    public void showSchedule(String groupName){
        if((new File(this.getDir("Grupa_", Context.MODE_PRIVATE)+ groupName + ".txt")).exists()){
            Intent intent = new Intent(this, DisplayCalendar.class);
            intent.putExtra(EXTRA_MESSAGE, groupName);
            this.startActivity(intent);
        } else {
            final Activity act = this;
            act.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(act, "Brak planu. Zaktualizuj.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
