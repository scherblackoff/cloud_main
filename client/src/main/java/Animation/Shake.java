package Animation;

import javafx.animation.TranslateTransition;

import javafx.scene.Node;
import javafx.util.Duration;

//Класс для протряски поля с паролем в случае не правильного его ввода

public class Shake {
    private TranslateTransition tt;

    public Shake(Node node) {
        tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0f);
        tt.setByX(10f);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
    }
    public void playAnim(){
        tt.playFromStart();
    }
}
