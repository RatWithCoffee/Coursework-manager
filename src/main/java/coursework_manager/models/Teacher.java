package coursework_manager.models;

import lombok.Data;

@Data
public class Teacher {
    private int id;
    private String name;
    private String jobTitle;

    public Teacher(int id, String name, String jobTitle) {
        this.id = id;
        this.name = name;
        this.jobTitle = jobTitle;
    }

    public Teacher(String name, String jobTitle) {
        this.name = name;
        this.jobTitle = jobTitle;
    }

}