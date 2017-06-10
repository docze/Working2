package docze.com.github.planzajec;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Klasa odpowiedzialna za stworzenie i obsluge activity_main.xml
 */
public class Logging extends AppCompatActivity {
    /** Adres strony logowania */
    private static final String URL = "https://s1.wcy.wat.edu.pl/ed/";
    /** Klucz do wykonania intentu */
    public final static String EXTRA_MESSAGE = "docze.com.github.planzajec.MESSAGE";

    /**
     * Metoda odpowiedzialna za stworzenie widoku
     * @param savedInstanceState - informacja na temat stanu activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Metoda obsługująca zdarzenie onclick w widoku activity_main,
     * odpowiedzialna wykonanie procedury logowania
     * @param w - widok activity
     */
    public void log(View w){
        EditText editPesel = (EditText) findViewById(R.id.peselInput);
        String pesel = editPesel.getText().toString();
        EditText editPassword = (EditText) findViewById(R.id.passwordInput);
        String password = editPassword.getText().toString();
        Connection connection = new Connection(this);
        connection.execute(URL, pesel, password );
        cleanInputs(editPassword, editPesel);
    }

    /**
     * Metoda czyści pola wprowadzania danych
     * @param editPassword - kontrolka wprowadzania  hasła
     * @param editPesel - kontrolka wprowadzania numeru PESEL
     */
    private void cleanInputs(EditText editPassword, EditText editPesel) {
        editPassword.setText("");
        editPesel.setText("");
    }
}
