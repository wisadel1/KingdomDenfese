package util;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FXAnchorEditor extends Application{
    private static final int EDITOR_WIDTH = 1200;
    private static final int EDITOR_HEIGHT = 800;
    private static final int PREVIEW_CANVAS_SIZE = 400;
    private static final int FRAME_THUMB_SIZE = 80;

    private AnimationUnit currentUnit = new AnimationUnit();
    private Frame selecetedFrame;
    private int selectedFrameIndex = -1;

    private Canvas previewCanvas;
    private GraphicsContext gc;
    private ListView<Frame> frameListView;
    private TextField tfUnitClassName;
    private TextField tfAnchorX;
    private TextField tfAnchorY;
    private TextArea taCodeGenerator;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("帧数编辑器");
        primaryStage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f0f0f0;");

        initTopBar(root);
        initLeftPanel(root);
        initCenterCanvas(root);
        initRightPanel(root);
        initBottomCodeArea(root);

        Scene scene = new Scene(root, EDITOR_WIDTH, EDITOR_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initTopBar(BorderPane root){
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(5));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-border-width: 0 0 1 0; -fx-border-color: #ccc;");

        Label lbUnitName = new Label("单位类名:");
        tfUnitClassName = new TextField();
        tfUnitClassName.setPrefWidth(250);
        tfUnitClassName.textProperty().addListener((obs, old, newVal)->{
            currentUnit.setUnitClassName(newVal);
            generateGameCode();
        });

        Button btnLoadFrames = new Button("加载帧序列");
        btnLoadFrames.setOnAction(e->loadFrameSequence());

        Button btGenCode = new Button("生成代码");
        btGenCode.setOnAction(e->generateGameCode());

        Button btnCopyCode = new Button("复制代码");
        btnCopyCode.setOnAction(e -> {
            taCodeGenerator.selectAll();
            taCodeGenerator.copy();
            showAlert(Alert.AlertType.INFORMATION, "代码已复制到剪贴板！")
        });

        topBar.getChildren().addAll(lbUnitName, tfUnitClassName,
                btnLoadFrames, btGenCode, btnCopyCode);
        root.setTop(topBar);
    }

    private void initLeftPanel(BorderPane root){
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(5));
        leftPanel.setPrefWidth(100);
        leftPanel.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-width: 0 1 0 0; -fx-border-color: #ccc;");

        Label lbFrames = new Label("帧列表:");
        lbFrames.setFont(Font.font(12));

        frameListView = new ListView<>();
        frameListView.setCellFactory(lv -> new ListCell<Frame>(){
            @Override
            protected void updateItem(Frame frame, boolean empty){
                super.updateItem(frame, empty);
                if(empty || frame == null){
                    setText(null);
                    setGraphic(null);
                }else{
                    ImageView thumb = new javafx.scene.image.ImageView(frame.getImage());
                    thumb.setFitWidth(FRAME_THUMB_SIZE);
                    thumb.setFitHeight(FRAME_THUMB_SIZE);
                    thumb.setPreserveRatio(true);
                    setGraphic(thumb);
                    setText("帧" + frame.getIndex());
                }
            }
        });

        leftPanel.getChildren().addAll(lbFrames, frameListView);
        root.setLeft(leftPanel);
    }

    private void initCenterCanvas(BorderPane root){
        VBox centerPanel = new VBox(10);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(5));

        Label lbPreview = new Label("预览:");
        lbPreview.setFont(Font.font(12));

        previewCanvas = new Canvas(PREVIEW_CANVAS_SIZE, PREVIEW_CANVAS_SIZE);
        gc = previewCanvas.getGraphicsContext2D();

        drawGrid();

        previewCanvas.setOnMouseDragged(this::handleAnchorDrag);
        previewCanvas.setOnMouseReleased(e -> generateGameCode());

        centerPanel.getChildren().addAll(lbPreview, previewCanvas);
        root.setCenter(centerPanel);
    }

    private void initRightPanel(BorderPane root){
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(200);
        rightPanel.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-width: 1 0 0 0; -fx-border-color: #ccc;");

        Label lbAnchorEdit = new Label("锚点编辑:");
        lbAnchorEdit.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 12));

        HBox hbX = new HBox(5);
        hbX.setAlignment(Pos.CENTER);
        Label lbX = new Label("锚点X：");
        tfAnchorX = new TextField();
        tfAnchorX.setPrefWidth(80);
        tfAnchorX.setPromptText("0.0");
        tfAnchorX.setOnAction(e -> upadateAnchorFromInput());

        HBox hbY = new HBox(5);
        hbY.setAlignment(Pos.CENTER);
        Label lbY = new Label("锚点Y：");
        tfAnchorY = new TextField();
        tfAnchorY.setPrefWidth(80);
        tfAnchorY.setPromptText("0.0");
        tfAnchorY.setOnAction(e -> updateAnchorFromInput());

        Button btnRestAnchor = new Button("重置");
        btnRestAnchor.setOnAction(e-> {
            if(selecetedFrame != null){
                selecetedFrame.setAnchorX(selecetedFrame.getWidth() / 2);
                selecetedFrame.setAnchorY(selecetedFrame.getHeight() / 2);
                updatePreviewCanvas();
                updateAnchorInput();
                generateGameCode();
            }
        });

        rightPanel.getChildren().addAll(lbAnchorEdit, hbX, hbY, btnRestAnchor);
        hbX.getChildren().addAll(lbX, tfAnchorX);
        hbY.getChildren().addAll(lbY, tfAnchorY);
        root.setRight(rightPanel);
    }

    private void initBottomCodeArea(BorderPane root){
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(5));
        bottomPanel.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-width: 1 0 0 0; -fx-border-color: #ccc;");

        Label lbCode = new Label("生成的代码:");
        lbCode.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 12));

        taCodeGenerator = new TextArea();
        taCodeGenerator.setPrefHeight(200);
        taCodeGenerator.setEditable(false);
        taCodeGenerator.setWrapText(true);
        taCodeGenerator.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 11px;");

        bottomPanel.getChildren().addAll(lbCode, taCodeGenerator);
        root.setBottom(bottomPanel);
    }

    /**
     * 核心功能模块
     */
    private void loadFrameSequence(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("选择帧序列文件夹");
        File frameDir = dc.showDialog(null);
        if(frameDir == null || !frameDir.isDirectory()) return;

        List<File> frameFiles = java.util.Arrays.stream(frameDir.listFiles())
                .filter(f -> f.isFile() && f.getName().endsWith(".png"))
                .sorted(Comparator.comparing(this::getFrameNumber))
                .collect(Collectors.toList());

        if(frameFiles.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "所选文件夹不包含有效的PNG帧文件！");
            return;
        }

        String firstFileName = frameFiles.get(0).getName();
        String framePrefix = firstFileName.substring(0, firstFileName.lastIndexOf("_")) + "_";
        currentUnit.setFramePrefix(framePrefix);

        currentUnit.getFrames().clear();
        for(int i = 0; i < frameFiles.size(); i++){
            try{
                Image frameImage = new Image(frameFiles.get(i).toURI().toString());
                Frame frame = new Frame(frameImage, i);
                currentUnit.addFrame(frame);
            } catch (Exception e){
                showAlert(Alert.AlertType.ERROR, "加载帧文件失败: " + frameFiles.get(i).getName());
            }
        }

        frameListView.getItems().setAll(currentUnit.getFrames());
        if(!currentUnit.getFrames().isEmpty())
            frameListView.getSelectionModel().select(0);
        showAlert(Alert.AlertType.INFORMATION, "成功加载 " + currentUnit.getFrameCount() + " 帧！");
        generateGameCode();
    }

}
