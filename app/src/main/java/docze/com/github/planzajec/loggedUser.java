package docze.com.github.planzajec;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

public class loggedUser extends AppCompatActivity implements View.OnClickListener{

    private static Calendar calendar = Calendar.getInstance();
    private static int year = calendar.get(Calendar.YEAR);
    private static int month = calendar.get(Calendar.MONTH);
    private static int day = calendar.get(Calendar.DAY_OF_MONTH);
    private static Map<Date, Lesson[]> lessons = new HashMap<Date, Lesson[]>();
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale("pl_PL");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        this.getApplicationContext().getResources().updateConfiguration(config, null);

        setContentView(R.layout.activity_logged_user);

        setCalendarMonth();

        ImageButton buttonBack = (ImageButton)findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);
        ImageButton buttonFront = (ImageButton)findViewById(R.id.buttonFront);
        buttonFront.setOnClickListener(this);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        final TextView tv = (TextView) findViewById(R.id.textView);

        try {
            setLessonsCalendar();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                try {
                    String text = "";
                    String m;
                    int mm = month+1;
                    if(mm < 10){
                        m = "0" + mm;
                    } else {
                        m = mm + "";
                    }

                    Lesson[] l = lessons.get(new Date(dateFormat.parse(year + "-" + m + "-" + dayOfMonth).getTime()));

                    if(l != null){
                        for(Lesson lesson : l){
                            text += lesson + "\n\n";
                        }
                        tv.setText(text);
                    } else {
                        tv.setText("");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.buttonBack: {
                month++;
                if(month==12){
                    month=0;
                    year++;
                }
                break;
            }

            case R.id.buttonFront: {
                month--;
                if(month==-1){
                    month=11;
                    year--;
                }
                break;
            }
        }
        setCalendarMonth();
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("");
    }

    private void setCalendarMonth(){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        long endOfMonth = calendar.getTimeInMillis();

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setMaxDate(endOfMonth);
        calendarView.setMinDate(startOfMonth);
        calendarView.invalidate();
    }

    private void setLessonsCalendar() throws ParseException {
        /*
        Otwieramy plik i w pętli po każdej linii

        odczytaliśmy:
        Sieci komputerowe (w) [13],307 S,2017-04-13,13:30,2017-04-13,15:05,Fałsz,2017-04-13,13:30

        subject = sieci
        type = w
        start = 13:30
        number = [13]
        classroom = 307 S
        data = 2017-04-13

         */

        insertRowToMap("2017-04-30", "Sieci komputerowe", "w", "13:30", "[13]", "307 S");
    }

    private void insertRowToMap(String dateString, String subject, String type, String start, String number, String classRoom) {
        try {
            Date date = new Date(dateFormat.parse(dateString).getTime());

            if(!lessons.containsKey(date)){
                lessons.put(date, new Lesson[7]);
                Lesson[] l = lessons.get(date);
                l[0] = new Lesson("08:00", "09:35");
                l[1] = new Lesson("09:50", "11:25");
                l[2] = new Lesson("11:40", "13:15");
                l[3] = new Lesson("13:30", "15:05");
                l[4] = new Lesson("15:45", "17:20");
                l[5] = new Lesson("17:35", "19:10");
                l[6] = new Lesson("19:25", "21:00");
            }

            int index = countIndex(start);
            if (index > 0 && index <7){
                lessons.get(date)[index].insertSubjectData(subject, type, number, classRoom);
            }
            lessons.get(date)[index].insertSubjectData(subject, type, number, classRoom);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private int countIndex(String start) {
        int index = -1;
        switch(start){
            case "08:00": index = 0;
                break;
            case "09:50": index = 1;
                break;
            case "11:40": index = 2;
                break;
            case "13:30": index = 3;
                break;
            case "15:45": index = 4;
                break;
            case "17:35": index = 5;
                break;
            case "19:25": index = 6;
                break;
        }
        return index;
    }
}
