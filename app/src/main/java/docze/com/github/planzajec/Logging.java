package docze.com.github.planzajec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.String;

import static android.R.id.edit;


public class Logging extends AppCompatActivity {

    private static final String URL = "https://s1.wcy.wat.edu.pl/ed/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void log(View w){
        EditText editPesel = (EditText) findViewById(R.id.peselInput);
        String pesel = editPesel.getText().toString();
        Log.d("pesel", pesel);
        EditText editPassword = (EditText) findViewById(R.id.passwordInput);
        String password = editPassword.getText().toString();
        Log.d("klik", "nacisnales log");
        Connection connection = new Connection();
        connection.execute(URL, pesel, password );
        cleanInputs(editPassword, editPesel);
    }

    private void cleanInputs(EditText editPassword, EditText editPesel) {
        editPassword.setText("");
        editPesel.setText("");
    }

}
