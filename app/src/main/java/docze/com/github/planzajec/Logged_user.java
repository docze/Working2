package docze.com.github.planzajec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.os.Build.VERSION_CODES.M;

public class Logged_user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra(Logging.EXTRA_MESSAGE);

        TextView textView = (TextView) findViewById(R.id.welcome);
        textView.setText(textView.getText().toString()+" "+groupName);
    }
}
