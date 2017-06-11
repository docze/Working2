package docze.com.github.planzajec;

/**
 * Klasa slużaca do przechowywania informacji na temat zajęcia
 */
public class Lesson {
    /** Nazwa przedmiotu zajecia */
    private String subject = "";
    /** Typ zajecia */
    private String type = "";
    /** Godzina rozpoczecia zajecia */
    private String start;
    /** Godzina zakonczenia zajecia */
    private String end;
    /** Kolejny numer zajecia z danego przedmiotu i typu */
    private String number = "";
    /** Sala */
    private String classRoom = "";

    /** Konstruktor klasy Lesson
     *  @param start    godzina rozpoczecia zajecia
     *  @param end      godzina zakonczenia zajecia
     */
    public Lesson(String start, String end) {
        this.start = start;
        this.end = end;
    }

    /** Metoda odpowiadajaca za zwrocenie informacji o zajeciu */
    @Override
    public String toString() {
        return start + " - " + end + "  " + subject + "  " + type + number + "  " + classRoom;
    }

    /** Metoda odpowiadajaca za ustawienie informacji o zajeciu
     *  @param subject      nazwa przedmiotu
     *  @param type         typ zajecia
     *  @param number       kolejny numer zajecia z danego przedmiotu i typu
     *  @param classRoom    sala
     */
    public void insertSubjectData(String subject, String type, String number, String classRoom){
        this.subject = subject;
        this.type = type;
        this.number = number;
        this.classRoom = classRoom;
    }
}
