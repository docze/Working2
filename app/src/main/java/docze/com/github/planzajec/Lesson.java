package docze.com.github.planzajec;

/**
 * Klasa służąca do przechowywania informacji na temat zajęć
 */
public class Lesson {
    /** Nazwa przedmiot zajęcia */
    private String subject = "";
    /** Typ zajęcia */
    private String type = "";
    /** Godzina rozpoczęcia zajęcia */
    private String start;
    /** Godzina zakończenia zajęcia */
    private String end;
    /** Kolejny numer zajęcia z danego przedmiotu i typu */
    private String number = "";
    /** Sala */
    private String classRoom = "";

    /** Konstruktor klasy Lesson
     *  @param start    godzina rozpoczęcia zajęcia
     *  @param end      godzina zakończenia zajęcia
     */
    public Lesson(String start, String end) {
        this.start = start;
        this.end = end;
    }

    /** Metoda odpowiadająca za zwrócenie informacji o zajęciu */
    @Override
    public String toString() {
        return start + " - " + end + "  " + subject + "  " + type + number + "  " + classRoom;
    }

    /** Metoda odpowiadajaca za ustawienie informacji o zajęciu
     *  @param subject      nazwa przedmiotu
     *  @param type         typ zajęcia
     *  @param number       kolejny numer zajęcia z danego przedmiotu i typu
     *  @param classRoom    sala
     */
    public void insertSubjectData(String subject, String type, String number, String classRoom){
        this.subject = subject;
        this.type = type;
        this.number = number;
        this.classRoom = classRoom;
    }
}
