package docze.com.github.planzajec;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

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
        EditText editPassword = (EditText) findViewById(R.id.passwordInput);
        String password = editPassword.getText().toString();
        Connection connection = new Connection();
        connection.execute(URL, pesel, password );
        cleanInputs(editPassword, editPesel);
    }

    private void cleanInputs(EditText editPassword, EditText editPesel) {
        editPassword.setText("");
        editPesel.setText("");
    }
}
