package docze.com.github.planzajec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.String;


public class Logging extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logowanie(View w){
        String result = "";
        EditText editPesel = (EditText) findViewById(R.id.peselInput);
        String pesel = editPesel.toString();
        EditText editPassword = (EditText) findViewById(R.id.passwordInput);
        String password = editPesel.toString();
        Log.d("klik", "nacisnales logowanie");
        Connection connection = new Connection();
        String url = "https://s1.wcy.wat.edu.pl/ed/";
        connection.execute(url, pesel, password );
    }

}
