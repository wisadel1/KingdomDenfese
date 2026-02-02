import GameScene.GameScene;
public class GameController {
    private final GameScene gameScene;
    private int currentGold;
    private int currentWave;

    public GameController(GameScene gameScene){
        this.gameScene = gameScene;
        initGameData();
        initGameElements();
        System.out.println("游戏初始化完成！场景尺寸："
                + GameScene.SCENE_WIDTH + "x" + GameScene.SCENE_HEIGHT);
    }

    private void initGameData(){
        this.currentGold = 1000;
        this.currentWave = 1;
    }

    private void initGameElements(){
        // 初始化游戏元素，如敌人、塔、防御工事等
    }

    //Getter和Setter
    public int getCurrentGold(){
        return currentGold;
    }
    public void setCurrentGold(int currentGold){
        this.currentGold = currentGold;
    }
}
