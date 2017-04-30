package docze.com.github.planzajec;

public class Lesson {
    private String subject = ""; // Bazy Danych
    private String type = ""; // ć
    private String start; // 8:00
    private String end; // 9:35
    private String number = ""; // [2]
    private String classRoom = ""; // 103 K

    public Lesson(String start, String end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return start + " - " + end + "  " + subject + "  " + type + number + "  " + classRoom;
    }

    public void insertSubjectData(String subject, String type, String number, String classRoom){
        this.subject = subject;
        this.type = type;
        this.number = number;
        this.classRoom = classRoom;
    }
}
