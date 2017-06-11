package docze.com.github.planzajec;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Klasa obslugujaca kalendarz - jego wyswietlanie i przetwarzanie danych
 */
public class DisplayCalendar extends AppCompatActivity implements View.OnClickListener{

    /** Pole kalendarza do ustalania dat */
    private static java.util.Calendar calendar = java.util.Calendar.getInstance();
    /** Pole przechowujace rok */
    private static int year = calendar.get(java.util.Calendar.YEAR);
    /** Pole przechowujace miesiac */
    private static int month = calendar.get(java.util.Calendar.MONTH);
    /** Pole przechowujace dzien miesiaca */
    private static int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
    /** Mapa przechowujaca lekcje - kluczami sa daty, a wartosciami tablice zajeć w danym dniu */
    private static Map<Date, Lesson[]> lessons = new HashMap<Date, Lesson[]>();
    /** Pole przechowujace format daty "yyyy-MM-dd" */
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /** Metoda wywolywana automatycznie po utworzeniu Activity obslugującego kalendarz. Ustawiany jest
     *  jezyk, w jakim maja zostac wyswietlone miesiace. Metoda dodaje rowniez odpowiedniego sluchacza
     *  do przyciskow zmiany miesiecy w tyl i w przod, a takze do akcji zmiany daty w danym miesiacu.
     *  @param savedInstanceState   zachowany stan instancji
     */
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
            putLessonsToCalendar(calendarView, year, month, day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                putLessonsToCalendar(view, year, month, dayOfMonth);
            }
        });

    }

    /** Metoda dodajaca lekcje do aktualnie zaznaczonego dnia w kalendarzu
     *  @param view         widok kalendarza
     *  @param year         wybrany i zaznaczony przez uzytkownika rok
     *  @param month        wybrany i zaznaczony przez uzytkownika miesiac
     *  @param dayOfMonth   wybrany i zaznaczony przez uzytkownika dzien miesiaca
     */
    void putLessonsToCalendar(CalendarView view, int year, int month, int dayOfMonth) {
        final TextView tv = (TextView) findViewById(R.id.textView);
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

    /** Metoda wywolywana po kliknieciu przycisku zmiany miesiaca wstecz lub w przod - oblicza nowy miesiac,
     * ktory powinien zostac wyswietlony. Nastepnie konczy obecnie wyswietlane Activity i rozpoczyna nowe,
     * z nowym wybranym przez uzytkownika miesiacem.
     * @param view      widok, do ktorego sa dolaczone przyciski
     */
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
        view.refreshDrawableState();
        view.invalidate();
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("");
        finish();
        startActivity(getIntent());
    }

    /** Metoda wykorzystywana do obliczenia pierwszego i ostatniego dnia miesiaca, aby
     * wyswietlic tylko jeden, wybrany przez uzytkownika miesiać.
     */
    private void setCalendarMonth(){
        calendar.set(java.util.Calendar.YEAR, year);
        calendar.set(java.util.Calendar.MONTH, month);
        calendar.set(java.util.Calendar.DATE, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.set(java.util.Calendar.DATE, calendar.getActualMaximum(java.util.Calendar.DATE));
        calendar.set(java.util.Calendar.MONTH, month);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        long endOfMonth = calendar.getTimeInMillis();

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setMaxDate(endOfMonth);
        calendarView.setMinDate(startOfMonth);
        calendarView.invalidate();
        calendarView.refreshDrawableState();
    }

    /** Metoda przetwarzajaca zapisany w pamięci urzadzenia plik tekstowy planu zajęc w formacie CSV.
     *  Plik ten jest wczytywany linia po linii, dzielony na czesci wzgledem przecinka, a nastepnie
     *  zapisane w danej linii zajecie ze wszystkimi istotnymi szczegolami jest dodawane do mapy zajec.
     * */
    private void setLessonsCalendar() throws ParseException {
        String groupName = getIntent().getStringExtra(ScheduleChooser.EXTRA_MESSAGE);
        File file = new File(this.getDir("Grupa_", Context.MODE_PRIVATE)+ groupName + ".txt");
        if(file.exists()){
            try {
                String line;
                String[] parts;
                Scanner input = new Scanner(file, "ISO-8859-2");
                input.nextLine();
                while(input.hasNext()){
                    line = input.nextLine();
                    parts = line.split(",");
                    if(parts.length>8){
                        insertRowToMap(
                                parts[2],
                                parts[0].substring(0, parts[0].indexOf("(")-1),
                                parts[0].substring(parts[0].indexOf("(")+1, parts[0].indexOf(")")),
                                parts[3],
                                "[" + parts[0].substring(parts[0].indexOf("[")+1, parts[0].indexOf("]")) + "]",
                                parts[1]
                        );
                    }
                }
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Plik nie istnieje.");
        }
    }

    /** Metoda pozwalajaca wstawic do mapy zajec nowe zajecie.
     *  @param dateString       data zajecia
     *  @param subject          przedmiot
     *  @param type             typ zajecia
     *  @param start            godzina rozpoczecia
     *  @param number           kolejny numer zajecia z danego przedmiotu i typu
     *  @param classRoom        sala
     */
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

    /** Metoda na podstawie przekazanej jako parametr godziny rozpoczecia zajec oblicza,
     *  ktory jest to blok zajec w ciagu dnia.
     *  @param start godzina rozpoczecia zajec
     */
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
