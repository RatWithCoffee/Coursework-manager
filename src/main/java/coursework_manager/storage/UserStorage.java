package coursework_manager.storage;


import coursework_manager.models.users.User;
import lombok.Getter;

public class UserStorage {

    @Getter
    private static User user;

    public static void setUser(User user) {
        UserStorage.user = user;
    }

    public static void deleteUser() {
        user = null;
    }
}
