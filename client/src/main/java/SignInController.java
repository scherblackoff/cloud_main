import Animation.Shake;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignInController {
    public Button undoButton;
    public TextField loginField;
    public TextField passwordField;
    private Controller controller;
    private User user = new User();

    public void undo(ActionEvent actionEvent) {
        undoButton.getScene().getWindow().hide();
    }

    public void signIn(ActionEvent actionEvent) {
        String loginText = loginField.getText().trim();
        String passwordText = passwordField.getText().trim();
        if (!loginText.equals("") && !passwordText.equals("")) {
            connectUser(loginText, passwordText);
        } else {
            playAnimation();
        }
    }

    private void connectUser(String loginText, String passwordText) {
        user.setLogin(loginText);
        user.setPassword(passwordText);
        user.setStatus(User.Status.SIGN_IN);
        controller.getNetwork().sendMessage(user);
        try {
            user = (User) controller.getNetwork().readMessage();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (user.getStatus().equals(User.Status.USER_IS_REGISTERED)) {
            controller.init(user);
            loginField.getScene().getWindow().hide();
        } else {
            playAnimation();
        }
    }

    public void playAnimation() {
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
    }

    public void setParent(Controller controller) {
        this.controller = controller;
    }
}
