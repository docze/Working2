package docze.com.github.planzajec;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class WelcomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);
    }

    public void showSchedule(View w){
        this.startActivity(new Intent(this, loggedUser.class));
    }

    public void updateSchedule(View w){
        this.startActivity(new Intent(this, Logging.class));
    }
}
