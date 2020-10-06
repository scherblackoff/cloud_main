import Animation.Shake;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;




public class SignUpController {

    public TextField loginField;
    public TextField passwordField;
    public TextField repeatPasswordField;
    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private Controller controller;


    public Button undoButton;



    public void undo(ActionEvent actionEvent) {
        undoButton.getScene().getWindow().hide();
    }

    public void signUpButton(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if(isCorrectRepeatPassword() && !dbHandler.clientIsExist(login)) {
            User user = new User(login, password);
            dbHandler.signUpUser(user);


            /*Создаю директорию для каждого нового зарегистрированного клиента в то время как файлы закруженные
                    в облако храняться в одной директории*/


            user.createDir();
            controller.init(user);
            loginField.getScene().getWindow().hide();
        }else{
            Shake userLoginAnim = new Shake(loginField);
            Shake userPassAnim = new Shake(passwordField);
            Shake userRepeatPassAnim = new Shake(repeatPasswordField);
            userLoginAnim.playAnim();
            userPassAnim.playAnim();
            userRepeatPassAnim.playAnim();
        }
    }

    private boolean isCorrectRepeatPassword(){
        return passwordField.getText().trim().equals(repeatPasswordField.getText().trim());
    }

    /* С помощью обозначения родителя и приемника оперирую информацией между ними */

    public void setParent(Controller controller) {
        this.controller = controller;
    }

}


