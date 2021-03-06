package excelsior.data;

import excelsior.UI;
import excelsior.gui.ComicPane;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HtmlSaver {
    private final UI ui;
    private String comicTitle;
    private File comicDir;
    private int currentComicFolderIndex;
    private Image endScreen;
    private HashMap<String,String[]> themes = new HashMap<String,String[]>();

    public HtmlSaver(UI ui) {
        this.ui = ui;
        this.comicTitle="Title";
        endScreen = new Image("/Icons/end_screen.png");
        loadThemes();
    }

    /**
     * returns an array of names of all of the themes
     */
    public String[] getThemes()
    {
        return themes.keySet().toArray(new String[themes.keySet().size()]);
    }

    /**
     * returns the colours associated with a particular theme
     */
    public String[] getThemeColourSet(String theme)
    {
        return themes.get(theme);
    }

    /**
     * loads the themes hashmap with themes
     */
    public void loadThemes()
    {
        themes.put("Dark-red",new String[]{"#23272a","#2c2f33","#B22222"});
        themes.put("Dark-blue",new String[]{"#23272a","#2c2f33","#2362FF"});
        themes.put("Light-red",new String[]{"#EEEEEE","#F8F8F8","#B22222"});
        themes.put("Light-blue",new String[]{"#EEEEEE","#F8F8F8","#2362FF"});
        themes.put("Sky",new String[]{"#3B99FF","#94E0FF","#0E4CFF"});
        themes.put("Desert",new String[]{"#FFD904","#FFFF65","#FFA241"});
        themes.put("Forest",new String[]{"#005F04","#7EFF66","#3EA73A"});
        themes.put("Seaside",new String[]{"#00ADDE","#FFFF65","#60CCFF"});
        themes.put("Arctic",new String[]{"#4f6f86","#c1ccde","#9eddee"});
        themes.put("Cotton Candy",new String[]{"#FF23C7","#FF98C7","#FF58C1"});
    }

    /**
     * Saves the html file
     * -Creates the directory in required location and loads the comic images into the folder
     * -Runs loading screen while creating images, then HtmlOptionMenuPopup shows
     */
    public void save() {
        try {
            String dir = chooseDirectory();
            //if they chose a directory
            if (dir != null)
            {
                int i = 0;
                Boolean folderCreated = false;

                //Creates comic directory with name "comic-0", iterating through numbers until it creates a directory that hadn't existed
                while(!folderCreated)
                {
                    comicDir = new File(dir + "\\comic-"+i);
                    if(comicDir.mkdirs())
                    {
                        folderCreated = true;
                        currentComicFolderIndex = i;
                        ui.getLoadingScreen().show();
                        Thread.currentThread().sleep(100); //needed to avoid rendering issue

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run(){
                                try {
                                    Thread.currentThread().sleep(500);      //minimum loading screen time
                                    createImages(comicDir.getAbsolutePath());
                                    ui.getLoadingScreen().hide();
                                    Thread.currentThread().sleep(200);     //pause before html options is loaded
                                    ui.getHtmlOptionMenu().show(ui.getPrimaryStage());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    i++;
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Forms the html file based on selected type of comic and colour theme
     */
    public void htmlFormer(int selectedType,String theme)
    {
        String[] colours = themes.get(theme);
        try {
            File comic = new File(comicDir.getAbsolutePath() + "\\comic" + currentComicFolderIndex + ".html");
            PrintWriter writer = new PrintWriter(comic);
            writer.print("<html>\n" +
                    "<head>\n" +
                    "\t<style>\n" +
                    "\t\tbody{\n" +
                    "\t\t\tbackground-color: "+colours[0]+";\n" +
                    "\t\t\tmargin: 0px 0px 0px 0px;\n" +
                    "\t\t\t\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\th2{\n" +
                    "\t\t\ttext-align: center;\n" +
                    "\t\t\tcolor: #ffffff;\n" +
                    "\t\t\tfont-style: italic;\n" +
                    "\t\t\tfont-weight: bold;\n" +
                    "\t\t\tfont-family: Copperplate;\n" +
                    "\t\t\tpadding: 25px 10px 25px 10px;\n" +
                    "\t\t\tbackground-color: "+colours[2]+";\n" +
                    "\t\t\tborder-radius: 10px;\n" +
                    "\t\t\tmargin: 10px 140px 15px 140px;\n" +
                    "\t\t\ttext-shadow: 2px 2px 3px #000000;\n" +
                    "\t\t}\t\n" +
                    "\t\t\t\n" +
                    "\t\t#mainSlider{\n");

            if (selectedType == 2) {
                writer.print("\t\t\tmargin: 30px 0vw 0px 0vw;\n" +
                        "\t\t\toverflow: auto;\n");
            }
            else {
                writer.print("\t\t\tmargin: 0px 15vw 0px 15vw;\n");
            }

            writer.print("\t\t\tbackground-color: "+colours[1]+";\n" +
                    "\t\t\tpadding: 10px 30px 10px 30px;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\ttable{\n" +
                    "\t\t\tborder: 3px solid "+colours[2]+";\n" +
                    "\t\t\tborder-radius: 15px;\n" +
                    "\t\t\tpadding: 10px;\n" +
                    "\t\t\tmargin-left: auto;\n" +
                    "\t\t\tmargin-right: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\timg{\n");

            if(selectedType == 0 || selectedType == 3){
                writer.print("\t\t\twidth: 45vw;\n" +
                        "\t\t\theight: 40vw;\n");
            }

            if(selectedType == 1){
                writer.print("\t\t\twidth: 30vw;\n" +
                        "\t\t\theight: 27vw;\n");
            }

            if(selectedType == 2){
                writer.print("\t\t\twidth: 79vmin;\n" +
                        "\t\t\theight: 70vmin;\n");
            }

            writer.print("\t\t\tborder-radius: 10px;\n" +
                    "\t\t}\n" +
                    "\t</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    (selectedType == 2? "\t\t<h2>" + comicTitle + "</h2>\n" : "") +
                    "\t<div id=\"mainSlider\">\n" +
                    (selectedType != 2? "\t\t<h2>" + comicTitle + "</h2>\n" : "") +
                    "\t\t<table>\n");

            //get all of the comic Panes loaded into the folder and add them to the html comic
            File[] comicImagePanels = comicDir.listFiles();
            comicImagePanels = clean(comicImagePanels);
            if (selectedType == 0) {
                for (int i = 0; i < comicImagePanels.length; i++) {
                    writer.print("\t\t\t<tr><td><img src=\"" + i + ".png" + "\" ></td></tr>\n");
                }
            }

            else if (selectedType == 1) {
                for (int i = 0; i < comicImagePanels.length; i++) {
                    if (i % 2 == 0)
                        writer.print("\t\t\t<tr><td><img src=\"" + i + ".png" + "\" ></td>\n");
                    else
                        writer.print("\t\t\t<td><img src=\"" + i + ".png" + "\" ></td></tr>\n");
                }
            }

            else if (selectedType == 2) {
                writer.print("\t\t\t<tr>");
                for (int i = 0; i < comicImagePanels.length; i++) {
                    writer.print("<td><img src=\"" + i + ".png" + "\" ></td>");
                }
                writer.print("</tr>\n");
            }
            else if(selectedType == 3){
                createGif();
                writer.print("<td><img src=\"" + "GIF.gif" + "\" ></td>");
            }

            writer.print("\t\t</table>\n" +
                    "\t</div>\n" +
                    "</body>\n" +
                    "</html>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens directory chooser and returns the path of the chosen directory
     */
    private String chooseDirectory(){
        DirectoryChooser dirToSaveHtmlFolder = new DirectoryChooser();
        dirToSaveHtmlFolder.setTitle("Choose Directory to Save HTML comic folder");
        File chosenDirectory = dirToSaveHtmlFolder.showDialog(ui.getPrimaryStage());
        if(chosenDirectory != null)
        {
            return chosenDirectory.getAbsolutePath();
        }else
        {
            return null;
        }
    }

    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }

    /**
     * Create all the images of comic panels from the comic
     */
    public void createImages(String fileLocation) throws IOException, URISyntaxException {
        for (int i = 0; i < ui.getComicPanels().getChildren().size(); i++) {
            ComicPane pane = (ComicPane) ui.getComicPanels().getChildren().get(i);
            ui.getWorkPanel().setTo(pane, true);
            ui.getWorkPanel().saveAsPng(i + ".png", fileLocation);
            ui.resetAppFace();
        }

        File file = new File(fileLocation + "\\" + ui.getComicPanels().getChildren().size() + ".png");
        ImageIO.write(SwingFXUtils.fromFXImage(endScreen, null), "png", file);
    }

    /**
     * Creates a gif using comic panel images in directory created earlier in html process
     */
    public void createGif() throws IOException {
        File[] comicImagePanels = comicDir.listFiles();
        comicImagePanels = clean(comicImagePanels);
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.start(comicDir.getAbsolutePath() + "/GIF" + ".gif");
        e.setDelay(6000);   // number of milliseconds between frames

        for (File comicImagePanel : comicImagePanels) {
            BufferedImage nextImage = ImageIO.read(comicImagePanel);
            e.addFrame(nextImage);
        }
        e.finish();
    }

    /**
     * Clean function removes all non png files from the input array and returns it
     */
    private File[] clean(File[] images){
        int count = 0;
        for (File image : images) {
            if (image.getName().endsWith(".png")) {
                count++;
            }
        }
        File[] array = new File[count];
        for(int i=0, j=0; i< images.length; i++){
            if(images[i].getName().endsWith(".png")){
                array[j++] = images[i];
            }
        }
        return array;
    }
}