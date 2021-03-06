package excelsior.data;

import excelsior.*;
import excelsior.UI;
import excelsior.panel.Character;
import excelsior.gui.ComicPane;
import excelsior.control.HighlightedPopup;
import excelsior.input.StringPreparer;
import excelsior.panel.Narration;
import excelsior.panel.TextBubble;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlLoader {
    UI ui;
    StringPreparer stringPreparer = new StringPreparer();
    Boolean issues = false;

    public XmlLoader(UI ui) {
        this.ui = ui;
    }

    public void load(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML file (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(ui.getPrimaryStage());

        if (file != null) {
            ui.getPanelController().clearWorkPanel();
            ui.getComicPanels().getChildren().clear();

            ui.getLoadingScreen().show();
            try {
                Thread.currentThread().sleep(100); //needed to avoid rendering issue
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run(){
                    try {
                        Thread.currentThread().sleep(500);      //minimum loading screen time
                        loadFile(file);
                        ui.getLoadingScreen().hide();
                        if(issues){
                            createWarning("There were issues loading your XML file...\nCharacter poses, colours or text may be changed");
                            Thread.currentThread().sleep(300);
                            ui.getXmlLoaderWarning().show(ui.getPrimaryStage());
                            issues = false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void loadFile(File inputFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.setErrorHandler(null);
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            parsePanels(doc.getElementsByTagName("panels"), ui.getComicPanels());
        } catch (Exception e) {
            createWarning("The XML file you wanted to load is not well formed.\nPlease fix the XML before loading again");
        }
    }

    private void parsePanels(NodeList nl, HBox comicPanels) {
        nl = nl.item(0).getChildNodes();
        int counter = comicPanels.getChildren().size();
        for (int temp = 0; temp < nl.getLength(); temp++) {
            Node nNode = nl.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                comicPanels.getChildren().add(new ComicPane());
                parse(nNode, (ComicPane) comicPanels.getChildren().get(counter++));
            }
        }

    }

    private void parse(Node n, ComicPane comic) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) n;
            comic.setTopNarration(parseAbove(eElement.getElementsByTagName("above")));
            parseLeft(eElement.getElementsByTagName("left"), comic);
            parseRight(eElement.getElementsByTagName("right"), comic);
            comic.setBottomNarrationMin(parseBelow(eElement.getElementsByTagName("below")));
            comic.minimise();
            ui.getPanelController().selectComicPanel(comic);
        }
    }

    private void parseLeft(NodeList left, ComicPane comic) {
        if (left.getLength() > 0) {
            Node nNode = left.item(0);

            if (nNode.getNodeType() == nNode.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                comic.setLeftCharacter(parseCharacter(eElement.getElementsByTagName("figure")));
                comic.setLeftSpeechBubble(parseBalloon(eElement.getElementsByTagName("balloon")));
            }
        }
    }

    private void parseRight(NodeList right, ComicPane comic) {
        if (right.getLength() > 0) {
            Node nNode = right.item(0);

            if (nNode.getNodeType() == nNode.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                comic.setRightCharacter(parseCharacter(eElement.getElementsByTagName("figure")));
                comic.setRightSpeechBubble(parseBalloon(eElement.getElementsByTagName("balloon")));

            }
        }
    }

    private TextBubble parseBalloon(NodeList balloon) {
        TextBubble tBub = new TextBubble();
        if (balloon.getLength() > 0) {
            Node nNode = balloon.item(0);

            if (nNode.getNodeType() == nNode.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("status").equals("speech"))
                    tBub.setSpeech();
                else if (eElement.getAttribute("status").equals("thought"))
                    tBub.setThought();
                String text = stringPreparer.prepareTBub(eElement.getElementsByTagName("content").item(0).getTextContent(), tBub);
                if(text == null)
                    issues = true;
                tBub.setText(text);
            }
        }

        return tBub;
    }

    private Narration parseAbove(NodeList above) {
        Narration label = new Narration();
        if (above.getLength() > 0) {
            Node nNode = above.item(0);
            if (nNode.getNodeType() == nNode.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String text = stringPreparer.prepareNarration(eElement.getTextContent(), label);
                if(text == null)
                    issues = true;
                label.setText(text);
            }
        }
        return label;
    }

    private Narration parseBelow(NodeList below) {
        Narration label = new Narration();
        if (below.getLength() > 0) {
            Node nNode = below.item(0);
            if (nNode.getNodeType() == nNode.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String text = stringPreparer.prepareNarration(eElement.getTextContent(), label);
                if(text == null)
                    issues = true;
                label.setText(text);

            }
        }
        return label;
    }

    private Character parseCharacter(NodeList figure) {
        Character c = new Character();
        for (int count = 0; count < figure.getLength(); count++) {
            Node nNode = figure.item(count);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                if (eElement.getElementsByTagName("pose").getLength() == 0) {
                    c.setCharacterPose("#empty.png");
                } else {
                    String character = eElement.getElementsByTagName("pose").item(0).getTextContent();
                    try {
                        if (!character.contains(".png")) {
                            c.setCharacterPose(character + ".png");
                        } else {
                            c.setCharacterPose(character);
                            c.setPoseString(character);
                        }
                    }catch (Exception e){
                        issues=true;
                        c.setDefaultPose();
                    }
                }

                if (eElement.getElementsByTagName("facing").getLength() > 0 &&
                        eElement.getElementsByTagName("facing").item(0).getTextContent().equals("left"))
                    c.flipDefaultOrientation();
                else if (!eElement.getElementsByTagName("facing").item(0).getTextContent().equals("right"))
                    issues = true;

                if (eElement.getElementsByTagName("appearance").getLength() > 0 &&
                        eElement.getElementsByTagName("appearance").item(0).getTextContent().equals("male"))
                    c.setFemale(false);
                else if (!eElement.getElementsByTagName("appearance").item(0).getTextContent().equals("female"))
                    issues = true;

                Color colour = getColour(eElement.getElementsByTagName("lips").item(0).getTextContent());
                if (eElement.getElementsByTagName("lips").getLength() > 0 && colour!= null) {
                    c.setLipColour(colour);
                }
                else if (colour == null)
                    issues = true;

                colour = getColour(eElement.getElementsByTagName("hair").item(0).getTextContent());
                if (eElement.getElementsByTagName("hair").getLength() > 0 && colour!= null) {
                    c.setHairColour(colour);
                }
                else if (colour == null)
                    issues = true;

                colour = getColour(eElement.getElementsByTagName("skin").item(0).getTextContent());
                if (eElement.getElementsByTagName("skin").getLength() > 0 && colour!= null) {
                    c.setSkinColour(colour);
                }
                else if (colour == null)
                    issues = true;
            }
        }
        c.minimise();
        return c;
    }

    private Color getColour(String colourString){
        Color colour;
        if (!colourString.startsWith("#")) {
            colour = ColourMap.getColorNamed(colourString);
        } else {
            try{
                colour = Color.web(colourString);
                if(colour.getRed()*255 <9)
                    colour = Color.rgb((int)(colour.getRed()*255)+9, (int)(colour.getGreen()*255), (int)(colour.getBlue()*255));
            }catch (Exception e){
                return null;
            }
        }

        return colour;
    }

    private void createWarning(String s){
        VBox container = new VBox();
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 18px;");
        container.setAlignment(Pos.CENTER);

        final Label warning = new Label("Warning!");
        warning.setStyle("-fx-font-size: 30px; -fx-text-fill: red; -fx-font-weight: bold;");
        Label loaderWarning = new Label(s);
        Label closePrompt = new Label("\nClick anywhere to close.");

        container.getChildren().addAll(warning, loaderWarning, closePrompt);

        ui.setXmlLoaderWarning(new HighlightedPopup(ui));
        ui.getXmlLoaderWarning().getContent().add(container);
    }
}
