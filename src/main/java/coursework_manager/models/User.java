package coursework_manager.models;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class User  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private String login;
    private String password;
    private Role role;

    public User(String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
}
