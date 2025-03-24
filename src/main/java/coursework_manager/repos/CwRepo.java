package coursework_manager.repos;


import coursework_manager.models.Coursework;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CwRepo {

    // Метод для получения всех курсовых работ
    public static List<Coursework> getAllCourseworks() {
        List<Coursework> courseworks = new ArrayList<>();
        String query = "SELECT * FROM coursework";

        try (Connection connection = DbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Coursework coursework = new Coursework(id, name);
                courseworks.add(coursework);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseworks;
    }
}