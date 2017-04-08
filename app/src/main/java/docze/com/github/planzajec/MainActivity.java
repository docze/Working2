package docze.com.github.planzajec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.String;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    public void logowanie(View w){
        String result = "";
        EditText editPesel = (EditText) findViewById(R.id.peselInput);
        String pesel = editPesel.toString();
        EditText editPassword = (EditText) findViewById(R.id.passwordInput);
        String password = editPesel.toString();
        Log.d("klik", "nacisnales logowanie");
        Polaczenie polaczenie = new Polaczenie();
        String url = "https://s1.wcy.wat.edu.pl/ed/";
        polaczenie.execute(url, pesel, password );
    }

}
