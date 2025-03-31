package coursework_manager.models;

import lombok.Data;

@Data
public class Coursework {
    private int id;
    private String name;

    public Coursework(int id, String name) {
        this.id = id;
        this.name = name;
    }

}