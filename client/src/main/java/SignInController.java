import Animation.Shake;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInController {
    public Button undoButton;
    public TextField loginField;
    public TextField passwordField;
    private Controller controller;
    private final User user = new User();

    public void undo(ActionEvent actionEvent) {
        undoButton.getScene().getWindow().hide();
    }

    public void signIn(ActionEvent actionEvent) {
        String loginText = loginField.getText().trim();
        String passwordText = passwordField.getText().trim();
        if (!loginText.equals("") && !passwordText.equals("")) {
            connectUser(loginText, passwordText);
        } else {
            shake();
        }
    }

    private void connectUser(String loginText, String passwordText){
        DatabaseHandler dbHandler = new DatabaseHandler();
        user.setLogin(loginText);
        user.setPassword(passwordText);
        ResultSet resultSet = dbHandler.getUser(user);

        int counter = 0;
        try {
            while (resultSet.next()) {
                counter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (counter >= 1) {
            System.out.println("Success");
            controller.init(user);
            loginField.getScene().getWindow().hide();
        } else {
            shake();
        }
    }

    public void shake() {
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
    }

    public void setParent(Controller controller) {
        this.controller = controller;
    }
}
