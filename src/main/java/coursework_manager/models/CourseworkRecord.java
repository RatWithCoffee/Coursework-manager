package coursework_manager.models;

import lombok.Data;

@Data
public class CourseworkRecord {
    private final int id;
    private  Teacher teacher;
    private final Coursework coursework;
    private final Group group;

    public CourseworkRecord(int id, Teacher teacher, Coursework coursework, Group group) {
        this.id = id;
        this.teacher = teacher;
        this.coursework = coursework;
        this.group = group;
    }

    public String getCourseworkName() {
        return coursework.getName();
    }

    public String getTeacherName() {
        return teacher.getName();
    }

}