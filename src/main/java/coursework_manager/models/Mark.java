package coursework_manager.models;

public class Mark {
    private int id;
    private String studentName;
    private Integer mark;

    public Mark(int id, String studentName, int mark) {
        this.id = id;
        this.studentName = studentName;
        this.mark = mark;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public int getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getMark() {
        return mark;
    }
}