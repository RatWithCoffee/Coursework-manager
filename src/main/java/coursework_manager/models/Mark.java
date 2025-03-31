package coursework_manager.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Mark {
    private int id;
    private String studentName;
    private Integer mark;

    public Mark(int id, String studentName, Integer mark) {
        this.id = id;
        this.studentName = studentName;
        this.mark = mark;
    }

}