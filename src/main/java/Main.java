import GameScene.GameScene;
import javafx.application.Application;
import javafx.stage.Stage;
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameScene gameScene = new GameScene();
        new GameController(gameScene);

        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(gameScene.getJavafxScene());
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
