import java.io.File;

public class User extends AbstractMessage{

    public enum  Status {
        SIGN_UP,
        SIGN_IN,
        USER_IS_REGISTERED
    }

    private String login;
    private String password;


    private String path;
    private Status status;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        path = "client/src/main/resources/" + login + "_dir";
    }

    public User() {

    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //С помощью данного метода создаю для каждого пользователя отдеельную директорию
    public void createDir() {
        new File(path).mkdirs();
    }
}
