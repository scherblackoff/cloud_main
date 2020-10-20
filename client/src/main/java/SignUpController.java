import Animation.Shake;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;


public class SignUpController {

    public TextField loginField;
    public TextField passwordField;
    public TextField repeatPasswordField;
    private Controller controller;


    public Button undoButton;



    public void undo(ActionEvent actionEvent) {
        undoButton.getScene().getWindow().hide();
    }

    public void signUpButton(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if(isCorrectRepeatPassword()) {
            User user = new User(login, password);
            user.setStatus(User.Status.SIGN_UP);
            controller.getNetwork().sendMessage(user);
            try {
                user = (User) controller.getNetwork().readMessage();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(!user.getStatus().equals(User.Status.USER_IS_REGISTERED)) {


            /*Создаю директорию для каждого нового зарегистрированного клиента в то время как файлы закруженные
                    в облако храняться в одной директории*/


                user.createDir();
                controller.init(user);
                loginField.getScene().getWindow().hide();
            }else{
                playAnimation();
            }
        }else{
            playAnimation();
        }
    }

    private void playAnimation(){
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        Shake userRepeatPassAnim = new Shake(repeatPasswordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
        userRepeatPassAnim.playAnim();
    }

    private boolean isCorrectRepeatPassword(){
        return passwordField.getText().trim().equals(repeatPasswordField.getText().trim());
    }

    /* С помощью обозначения родителя и приемника оперирую информацией между ними */

    public void setParent(Controller controller) {
        this.controller = controller;
    }

}


