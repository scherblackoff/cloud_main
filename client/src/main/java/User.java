import java.io.File;

public class User {

    private String login;
    private String password;


    private String path;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        path = "client/src/main/resources/" + login + "_dir";
    }

    public User() {

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
