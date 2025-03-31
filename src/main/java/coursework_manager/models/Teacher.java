package coursework_manager.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Teacher {
    private  IntegerProperty id;
    private final StringProperty name;
    private final StringProperty jobTitle;

    // Конструктор
    public Teacher(int id, String name, String jobTitle) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.jobTitle = new SimpleStringProperty(jobTitle);
    }

    public Teacher(String name, String jobTitle) {
        this.name = new SimpleStringProperty(name);
        this.jobTitle = new SimpleStringProperty(jobTitle);
    }

    // Геттеры для свойств
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Сеттеры
    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // Переопределение метода toString для удобства отладки
    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id.get() +
                ", name=" + name.get() +
                '}';
    }

    public String getJobTitle() {
        return jobTitle.get();
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle.set(jobTitle);
    }

    public StringProperty jobTitleProperty() {
        return jobTitle;
    }
}