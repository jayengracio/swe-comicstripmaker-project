package excelsior;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

public class UI {
    private final Stage primaryStage;
    private final HBox comicPanels = new HBox(15);
    private final ComicPane workPanel = new ComicPane();
    private final PanelController panelController = new PanelController(this);
    private final ButtonController buttonController = new ButtonController(this);
    private final ComicStripController comicStripController = new ComicStripController(this);
    private final StringPreparer stringPreparer = new StringPreparer();
    private TilePane buttonBox;
    private Character selectedCharacter;
    private HighlightedPopup charWarningPopup;
    private HighlightedPopup charPosesPopup;
    private HighlightedPopup bottomNarrationInput;
    private HighlightedPopup topNarrationInput;
    private HighlightedPopup textBubbleInput;
    private HighlightedPopup htmlTitleInput;
    private HighlightedPopup xmlLoaderWarning;
    private ColourPalette palette;

    public UI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ComicPane getWorkPanel() {
        return workPanel;
    }

    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(Character selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
    }

    public HBox getComicPanels() {
        return comicPanels;
    }

    public HighlightedPopup getCharWarningPopup() {
        return charWarningPopup;
    }

    public PanelController getPanelController() {
        return panelController;
    }

    public HighlightedPopup getCharPosesPopup() {
        return charPosesPopup;
    }

    public HighlightedPopup getBottomNarrationInput() {
        return bottomNarrationInput;
    }

    public HighlightedPopup getTopNarrationInput() {
        return topNarrationInput;
    }

    public HighlightedPopup getTextBubbleInput() {
        return textBubbleInput;
    }

    public HighlightedPopup getHtmlTitleInput() {
        return htmlTitleInput;
    }

    public TilePane getButtonBox() {
        return buttonBox;
    }

    public HighlightedPopup getXmlLoaderWarning() {
        return xmlLoaderWarning;
    }

    public void setXmlLoaderWarning(HighlightedPopup xmlLoaderWarning) {
        this.xmlLoaderWarning = xmlLoaderWarning;
    }

    /**
     * Sets up the stage.
     */
    public void setStage() {
        VBox root = new VBox();
        root.getChildren().add(createMenu());
        root.getChildren().add(createView());
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1250, 800));
        createPopups();
        primaryStage.show();
    }

    /**
     * Creates all of the popups at the start of the application rather than creating them each time a popup is used.
     */
    private void createPopups() {
        createSelectCharacterWarning();
        createBottomNarrationPopup();
        createTopNarrationPopup();
        createCharacterPoses();
        createTextBubbleInput();
        createHtmlTitlePopup();
    }

    /**
     * Creates a menu bar at the top of the application.
     *
     * @return a menu bar with its menus
     */
    public MenuBar createMenu() {
        Menu file = comicStripController.FileMenu();
        Menu panel = panelController.PanelMenu();
        Menu help = new HelpMenu(primaryStage).create();
        MenuBar mb = new MenuBar();
        mb.getMenus().add(file);
        mb.getMenus().add(panel);
        mb.getMenus().add(help);
        return mb;
    }

    /**
     * Creates the structure of the application.
     *
     * @return a grid pane that contains all the UI elements
     */
    public GridPane createView() {
        GridPane view = new GridPane();
        view.setPadding(new Insets(15));
        view.setHgap(30);
        view.setVgap(15);
        view.add(workPanel, 0, 0);
        view.add(createComicsView(), 0, 1, 3, 1);
        view.add(createColourPalette(), 1, 0);
        view.add(createButtons(), 2, 0);
        return view;
    }

    /**
     * Creates colour groups for changing a character's features.
     *
     * @return a colour palette for this UI
     */
    public ColourPalette createColourPalette() {
        palette = new ColourPalette(this);
        return palette;
    }

    /**
     * Creates the buttons that is used to modify content on the working panel.
     *
     * @return a tile pane with all the buttons
     */
    public TilePane createButtons() {
        buttonBox = new TilePane();
        buttonBox.setPrefSize(375, 500);
        buttonBox.setPrefColumns(2);
        buttonBox.setVgap(11);
        buttonBox.setHgap(14);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setPrefRows(4);
        buttonController.start();
        return buttonBox;
    }

    /**
     * Creates the scrollable pane that contains every saved comic panels.
     *
     * @return a scroll pane with comic panels as its content
     */
    public ScrollPane createComicsView() {
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 5;");
        scroll.setPrefHeight(300);
        comicPanels.setPrefSize(0, 235);
        comicPanels.setAlignment(Pos.CENTER_LEFT);
        comicPanels.setPadding(new Insets(0, 0, 0, 6));
        scroll.setContent(comicPanels);
        scroll.setPannable(true);
        return scroll;
    }

    private void createHtmlTitlePopup() {
        VBox container = new VBox();
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 3; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);

        TextField textBox = new TextField("Enter Comic Title");
        container.getChildren().add(textBox);

        htmlTitleInput = new HighlightedPopup(primaryStage);
        htmlTitleInput.getContent().add(container);

        EventHandler<ActionEvent> eventHandler = e -> {
            htmlTitleInput.hide();
        };
        textBox.setOnAction(eventHandler);
        htmlTitleInput.setOnHidden(e -> {
            comicStripController.getHtmlSaver().setComicTitle(textBox.getText());
            comicStripController.getHtmlSaver().htmlFormer();
            textBox.setText("Enter Comic Title");
            textBox.selectAll();
        });
    }

    /**
     * Input box to set text of speech/thought bubbles
     */
    private void createTextBubbleInput() {
        Label input = new Label();
        VBox container = new VBox();
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 3; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);

        TextField textBox = new TextField("Enter text");
        container.getChildren().add(textBox);
        Label warning = new Label();
        warning.setStyle("-fx-font-size: 16; -fx-text-fill: red");
        container.getChildren().add(warning);

        textBubbleInput = new HighlightedPopup(primaryStage);
        textBubbleInput.getContent().add(container);

        EventHandler<ActionEvent> eventHandler = e -> {
            TextBubble tBub = getCurrentSpeechBubble();
            String output = stringPreparer.prepareTBub(textBox.getText(), tBub);
            if (output == null) {
                warning.setText("Text Too Long");
            } else {
                input.setText(textBox.getText());
                tBub.setText(output);
                textBubbleInput.hide();
                if (tBub.isEmpty())
                    tBub.setEmpty();
                else {
                    if (buttonController.isSpeechBubble())
                        tBub.setSpeech();
                    else
                        tBub.setThought();
                }
            }
        };
        textBox.setOnAction(eventHandler);
        textBubbleInput.setOnHidden(e -> {
            warning.setText(null);
            textBox.setText("Enter text");
            textBox.selectAll();
        });
    }

    private TextBubble getCurrentSpeechBubble() {
        return selectedCharacter == workPanel.getRightCharacter() ? workPanel.getRightSpeechBubble() : workPanel.getLeftSpeechBubble();
    }

    /**
     * Creates Top Narration Popup
     */
    private void createTopNarrationPopup() {
        Narration narration;
        Label text = new Label();
        narration = workPanel.getTopNarration();
        topNarrationInput = createNarrationInput(narration, text);
    }

    /**
     * Creates Bottom Narration Popup
     */
    private void createBottomNarrationPopup() {
        Narration narration;
        Label text = new Label();
        narration = workPanel.getBottomNarration();
        bottomNarrationInput = createNarrationInput(narration, text);
    }

    /**
     * Creates an input box to set text for top/bottom narration
     *
     * @param narration top or bottom narration of the panel
     * @param input     text
     * @return a HighlightedPopup window
     */
    private HighlightedPopup createNarrationInput(Narration narration, Label input) {
        VBox container = new VBox();
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 3; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);

        TextField textBox = new TextField("Enter text");
        Label warning = new Label();
        warning.setStyle("-fx-font-size: 16px; -fx-text-fill: red");
        container.getChildren().addAll(textBox, warning);

        HighlightedPopup inputWindow = new HighlightedPopup(primaryStage);
        inputWindow.getContent().add(container);

        EventHandler<ActionEvent> eventHandler = e -> {
            String output = stringPreparer.prepareNarration(textBox.getText(), narration);
            if (output == null) {
                warning.setText("Text Too Long");
            } else {
                input.setText(textBox.getText());
                narration.setText(output);
                inputWindow.hide();
            }
        };
        textBox.setOnAction(eventHandler);
        inputWindow.setOnHidden(e -> {
            warning.setText(null);
            textBox.setText("Enter text");
            textBox.selectAll();
        });
        return inputWindow;
    }

    /**
     * Adds event handler to close popup input as parameter when clicked
     *
     * @param button to click
     * @param popup  to hide
     */
    private void closePopupButton(Node button, Popup popup) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> popup.hide());
    }

    /**
     * Displays options for character choices in scrollable popup
     */
    private void createCharacterPoses() {
        ScrollPane selection = new ScrollPane();
        selection.setPrefSize(261, 360);

        selection.setContent(createPoses());
        selection.setPannable(true);

        selection.setPadding(new Insets(5));
        selection.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");

        Button closeButton = new Button("Confirm");
        closeButton.setPrefSize(255, 10);
        closeButton.setStyle("-fx-border-color: #000000; -fx-border-radius: 10px; -fx-text-fill : #ffffff; -fx-background-color: #1CC415; -fx-background-radius: 10px");

        VBox container = new VBox(10);
        container.setPadding(new Insets(10, 1, 0, 1));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(closeButton, selection);

        charPosesPopup = new HighlightedPopup(primaryStage);
        charPosesPopup.getContent().add(container);
        closePopupButton(closeButton, charPosesPopup);
    }

    /**
     * Creates and gets character image options in TilePane
     *
     * @return TilePane of poses
     */
    private TilePane createPoses() {
        TilePane Poses = new TilePane();
        Poses.setMaxSize(500, 10);
        Poses.setPrefColumns(2);
        Poses.setPrefRows(6);
        Poses.setVgap(11);
        Poses.setHgap(14);
        Poses.setAlignment(Pos.CENTER_RIGHT);
        Poses.setStyle("-fx-background-color: white;-fx-font-size: 18px;");

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] charPoseFiles = resolver.getResources("Character_Images/*.png");

            int i = 0;
            for (Resource charPose : charPoseFiles) {
                Poses.getChildren().add(i, new CharacterPoseButton(charPose.getFilename()));
                Poses.setTileAlignment(Pos.TOP_LEFT);
                changePose(Poses.getChildren().get(i), charPose.getFilename());
                i++;
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to retrieve character images");
            e.printStackTrace();
        }

        return Poses;
    }

    /**
     * Waits for update for button being pressed and then changes the pose to the selected
     *
     * @param button node
     * @param pose   to change to
     */
    private void changePose(Node button, String pose) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            selectedCharacter.setCharacterPose(pose);
            selectedCharacter.setPoseString(pose);
            workPanel.setEditMode(true);
            if (selectedCharacter.isEmpty()) {
                if (workPanel.getLeftCharacter() == selectedCharacter)
                    workPanel.getLeftSpeechBubble().setEmpty();
                else
                    workPanel.getRightSpeechBubble().setEmpty();
            }
        });
    }

    /**
     * Creates a popup telling the user to select a character
     */
    private void createSelectCharacterWarning() {
        VBox container = new VBox();
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);

        final Label warning = new Label("Warning!");
        warning.setStyle("-fx-font-size: 30px; -fx-text-fill: red; -fx-font-weight: bold;");
        final Label selectCharacterWarning = new Label("You must select a character first.");
        final Label closePrompt = new Label("Click anywhere to close.");

        container.getChildren().addAll(warning, selectCharacterWarning, closePrompt);

        charWarningPopup = new HighlightedPopup(primaryStage);
        charWarningPopup.getContent().add(container);
    }

    /**
     * Reset the entire application interface to its original startup look
     */
    public void resetAppFace() {
        workPanel.clear();
        palette.reset();
        if (selectedCharacter != null) {
            selectedCharacter.setEffect(null);
            boolean isLeft = (selectedCharacter == workPanel.getLeftCharacter());
            IconButtons curCharBtn = (IconButtons) buttonBox.getChildren().get(isLeft ? 0 : 1);
            curCharBtn.setIcon(isLeft ? "Left.png" : "Right.png");
            buttonController.createCharacterButtonTooltip(curCharBtn, null);
            selectedCharacter = null;
        }
    }
}
