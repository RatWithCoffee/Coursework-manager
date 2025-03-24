package coursework_manager.repos;

import coursework_manager.models.Mark;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkRepo {


    // Метод для получения оценок по ID курсовой записи
    public static List<Mark> getMarksByCourseworkRecordId(int cwId, int groupId) {
        List<Mark> marks = new ArrayList<>();
        String query = "SELECT mark.id, mark.mark, student.name FROM mark " +
                "JOIN student ON student.id = mark.student_id WHERE mark.cw_id = ? AND student.group_id = ?";

        try (Connection connection = DbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, cwId);
            statement.setInt(2, groupId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int markVal = resultSet.getInt("mark");
                String stName = resultSet.getString("name");

                Mark mark = new Mark(id, stName, markVal);
                marks.add(mark);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marks;
    }


    // Метод для обновления оценки
    public static void updateMark(Mark mark) {
        String query = "UPDATE mark SET mark = ? WHERE id = ?";

        try (Connection connection = DbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (mark.getMark() == null) {
                statement.setNull(1, java.sql.Types.INTEGER);

            } else {
                statement.setInt(1, mark.getMark());

            }
            statement.setInt(2, mark.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для удаления оценки по ID
    public static void deleteMark(int id) {
        String query = "DELETE FROM mark WHERE id = ?";

        try (Connection connection = DbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}