package coursework_manager.models;


import lombok.Data;

@Data
public class Group {
    // Сеттеры
    // Геттеры для свойств
    private int id;
    private String name;

    // Конструктор
    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
