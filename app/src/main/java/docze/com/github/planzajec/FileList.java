package docze.com.github.planzajec;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Klasa odpowiedzialna za stworzenie i obsluge acitivity_file_list.xml
 */
public class FileList extends AppCompatActivity {
    /** lista zaznaczonych CheckBox*/
    List<String> checkBoxes = null;
    /** lista grup, na podstawie ktorej beda stworzone obiekty typu CheckBox*/
    List<String> groupList = null;
    /** Adres FTP, z ktorej bedzie pobierana lista grup */
    URL url = null;
    /**
     * Metoda odpowiedzialna za stworzenie widoku
     * @param savedInstanceState informacja na temat stanu activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        FTPConnection ftpConnection = new FTPConnection();
        try {
            groupList = ftpConnection.execute("az-serwer1701230.online.pro").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(groupList != null) {
            fillList();
        }
    }

    /**
     * Metoda odpowiedzialna za stworzenie CheckBox w acitvity_file_list.xml
     * zgodnie z lista
     */
    protected void fillList(){
        checkBoxes = new ArrayList<>();
        LinearLayout ll = (LinearLayout)  findViewById(R.id.listGroup);
        for(String box: groupList){
            CheckBox checkBox = new CheckBox(this.getApplicationContext());
            checkBox.setTextColor(Color.BLACK);
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            checkBox.setText(box);
            checkBox.setOnClickListener(isChecked);
            ll.addView(checkBox);
        }
    }

    /**
     *  Metoda obslugująca zdarzenie onclick, na rzecz przycisku @id/Download
     * w widoku acitvity_file_list.xml, pozwala na przejscie do widoku kalendarza
     * @param w widok activity
     */
    public void downloadFile(View w){
        if(checkBoxes != null){
            try{
                url = new URL("http://woonkievitch.pl/wat/");
                for(String box: checkBoxes){
                    url = new URL("http://woonkievitch.pl/wat/"+box);
                    ScheduleDownloader scheduleDownloader = new ScheduleDownloader(this, url, box, "23123" );
                    Object obj = scheduleDownloader.execute().get();
                }
            }catch (Exception e){
                Log.d("check", e.toString());
            }
        }
    }
    private View.OnClickListener isChecked = new View.OnClickListener() {
        public void onClick(View v) {
            CheckBox checkbox = (CheckBox) v;
            if (checkbox.isChecked()) {
                checkBoxes.add(checkbox.getText().toString());
                Toast.makeText(getApplicationContext(), "Dodalem do listy pobierania: " + checkbox.getText(), Toast.LENGTH_SHORT).show();
            } else {
                for (String box : checkBoxes) {
                    if (box.equals(checkbox.getText().toString())) {
                        checkBoxes.remove(box);
                        Toast.makeText(getApplicationContext(), "Usunalem z listy pobierania: " + checkbox.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}
