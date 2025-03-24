package coursework_manager.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CourseworkRecord {
    private final IntegerProperty id;
    private  Teacher teacher;
    private final Coursework coursework;
    private final Group group;

    public CourseworkRecord(int id, Teacher teacher, Coursework coursework, Group group) {
        this.id = new SimpleIntegerProperty(id);
        this.teacher = teacher;
        this.coursework = coursework;
        this.group = group;
    }
    
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getTeacherName() {
        return teacher.getName(); // Assuming Teacher class has a getName() method
    }

    public Coursework getCoursework() {
        return coursework;
    }

    public String getCourseworkName() {
        return coursework.getName(); // Assuming Coursework class has a getTitle() method
    }

    public Group getGroup() {
        return group;
    }

    public String getGroupName() {
        return group.getName(); // Assuming Group class has a getName() method
    }
}