package coursework_manager.repos;

import coursework_manager.models.Group;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GroupRepo {

    // Метод для получения всех групп
    public static List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM \"group\"";

        try (Connection connection = DbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Group group = new Group(id, name);
                groups.add(group);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }
}