package excelsior;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.io.File;

public class UI {
    Stage primaryStage;
    HBox allComics = new HBox(15);
    ComicPane comic = new ComicPane();

    Character selectedCharacter;

    DropShadow dropShadow = new DropShadow();

    public UI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setStage(){
        VBox root = new VBox();
        root.getChildren().add(createMenu());
        root.getChildren().add(createView());
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1250, 800));
        primaryStage.show();
    }

    public MenuBar createMenu(){
        Menu m = new Menu("File");

        MenuItem m1 = new MenuItem("New");
        MenuItem m2 = new MenuItem("Delete");
        MenuItem m3 = new MenuItem("Save");

        m.getItems().add(m1);
        m.getItems().add(m2);
        m.getItems().add(m3);

        MenuBar mb = new MenuBar();

        mb.getMenus().add(m);

        return mb;
    }
    public GridPane createView(){
        GridPane view = new GridPane();
        view.setPadding(new Insets(15));
        view.setHgap(30);
        view.setVgap(15);
        view.add(comic, 0, 0);
        view.add(createComicsView(), 0, 1, 3, 1);
        view.add(createColourPallet(), 1, 0);
        view.add(createButtons(), 2, 0);

        return view;
    }

    public HBox createColourPallet(){
        HBox box = new HBox(10);
        box.setPadding(new Insets(5));
        box.setPrefSize(450, 500);
        box.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 2;");
        VBox skin = new VBox(10);
        VBox hair = new VBox(10);
        box.getChildren().addAll(skin, hair);
        return box;
    }

    public TilePane createButtons(){
        TilePane buttonBox = new TilePane();
        buttonBox.setPrefSize(375, 500);
        buttonBox.setPrefColumns(2);
        buttonBox.setVgap(11);
        buttonBox.setHgap(14);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setPrefRows(4);
        buttonBox.getChildren().add(0, new Button("Left"));
        buttonBox.getChildren().add(1, new Button("Right"));
        buttonBox.getChildren().add(2, new Button("Flip"));
        buttonBox.getChildren().add(3, new Button("Character Poses"));
        leftCharacterButton(buttonBox.getChildren().get(0));
        rightCharacterButton(buttonBox.getChildren().get(1));
        switchOrientationButton(buttonBox.getChildren().get(2));
        characterPoses(buttonBox.getChildren().get(3));

        return buttonBox;
    }

    public ScrollPane createComicsView(){
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 5;");
        scroll.setPrefHeight(300);
        allComics.setPrefSize(2000, 235);
        allComics.setAlignment(Pos.CENTER_LEFT);
        allComics.setPadding(new Insets(0, 0, 0, 15));
        scroll.setContent(allComics);
        scroll.setPannable(true);
        return scroll;
    }

    private void characterPoses(Node button){

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(selectedCharacter == null)
            {
                displaySelectCharacterWarning();
            }else {

                displayCharacterPoses();
                //button.setEffect(dropShadow);
            }
        });
    }


    private void leftCharacterButton(Node button) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (selectedCharacter == comic.getLeftCharacter()) {
                selectedCharacter = null;
                comic.getLeftCharacter().setEffect(null);
            } else {
                selectedCharacter = comic.getLeftCharacter();
                comic.getLeftCharacter().setEffect(dropShadow);
                comic.getRightCharacter().setEffect(null);
            }
            button.setEffect(dropShadow);
        });
    }

    private void rightCharacterButton(Node button) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (selectedCharacter == comic.getRightCharacter()) {
                selectedCharacter = null;
                comic.getRightCharacter().setEffect(null);
            } else {
                selectedCharacter = comic.getRightCharacter();
                comic.getRightCharacter().setEffect(dropShadow);
                comic.getLeftCharacter().setEffect(null);
            }
            button.setEffect(dropShadow);
        });
    }

    private void switchOrientationButton(Node button) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(selectedCharacter == null || selectedCharacter.isEmpty())
            {
                displaySelectCharacterWarning();
            }else {
                selectedCharacter.flipDefaultOrientation();
                button.setEffect(dropShadow);
            }
        });
    }

    private void displayCharacterPoses()
    {

        ScrollPane selection = new ScrollPane();
        selection.setPrefSize(300 ,750);

        selection.setContent(createPoses());
        selection.setPannable(true);


        selection.setPadding(new Insets(22));
        selection.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");


        Popup charPosesPopup = new Popup();
        charPosesPopup.getContent().add(selection);
        charPosesPopup.setAutoHide(true);
        charPosesPopup.show(primaryStage);


    }

    public TilePane createPoses(){

        TilePane Poses = new TilePane();
        //Poses.setPrefSize(375, 100);
        // Poses.setMaxHeight(100);
        Poses.setMaxSize(500, 10);
        Poses.setPrefColumns(2);
        Poses.setPrefRows(6);
        Poses.setVgap(11);
        Poses.setHgap(14);
        Poses.setAlignment(Pos.CENTER_RIGHT);
        Poses.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");

        File folder = new File("src/Character_Images");
        File[] listOfFiles = folder.listFiles();
        int i = 0;
        for (File file : listOfFiles){
        if(file.isFile()){
        Poses.getChildren().add(i,new CharacterPoseButton(file.getName()));
        Poses.setTileAlignment(Pos.TOP_LEFT);
        changePose(Poses.getChildren().get(i) , file.getName());
        i++;
        }
        }
        return Poses;
    }
//  waits for update for button being pressed and then changes the pose to the selected
    private void changePose(Node button , String pose){
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            selectedCharacter.setCharacterPose(pose);
            //button.setEffect(dropShadow);
        });


    }


    private void displaySelectCharacterWarning()
    {
        VBox container = new VBox();
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);
        final Label warning = new Label("Warning!");
        warning.setStyle("-fx-font-size: 30px; -fx-text-fill: red; -fx-font-weight: bold;");
        final Label selectCharacterWarning = new Label("You must select a character first.");
        final Label closePrompt = new Label("Click anywhere to close.");
        container.getChildren().addAll(warning,selectCharacterWarning,closePrompt);
        Popup charWarningPopup = new Popup();
        charWarningPopup.getContent().add(container);
        charWarningPopup.setAutoHide(true);
        charWarningPopup.show(primaryStage);
    }
}
