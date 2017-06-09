package docze.com.github.planzajec;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class WelcomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);
    }

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

    public void updateSchedule(View w){
        this.startActivity(new Intent(this, Logging.class));
    }
}
