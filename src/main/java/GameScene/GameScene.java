package GameScene;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
public class GameScene {
    //窗口大小
    public static final int SCENE_WIDTH = 1280;
    public static final int SCENE_HEIGHT = 720;
    private final Pane rootPane;
    private final Scene javafxScene;

    public GameScene(){
        rootPane = new Pane();
        rootPane.setPrefSize(SCENE_WIDTH,SCENE_HEIGHT);
        rootPane.setStyle("-fx-background-color: #2c3e50;");
        initBackground();
        javafxScene = new Scene(rootPane, SCENE_WIDTH, SCENE_HEIGHT);
    }

    public void initBackground(){
        ImageView background = new ImageView();
        try{
            Image bgImage = new Image(getClass().getResourceAsStream("/images/Stage2 Material.png"));
            background.setImage(bgImage);
            background.setFitHeight(SCENE_HEIGHT);
            background.setFitWidth(SCENE_WIDTH);
            background.setPreserveRatio(false);
            background.setSmooth(true);
        }catch(NullPointerException e){
            System.err.println("背景图片加载失败");
            System.err.println("/images/Stage2 Material.png");
        }
        rootPane.getChildren().add(background);
    }

    //Getter
    public Pane getRootPane(){
        return rootPane;
    }
    public Scene getJavafxScene(){
        return javafxScene;
    }
}
