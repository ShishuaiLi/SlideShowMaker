/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssm.view;

import java.io.BufferedWriter;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static ssm.LanguagePropertyType.TOOLTIP_NEXT_SLIDE;
import static ssm.LanguagePropertyType.TOOLTIP_PREVIOUS_SLIDE;
import static ssm.StartupConstants.CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON;
import static ssm.StartupConstants.ICON_NEXT;
import static ssm.StartupConstants.ICON_PREVIOUS;
import static ssm.StartupConstants.PATH_ICONS;
import static ssm.StartupConstants.PATH_SITES;
import static ssm.StartupConstants.STYLE_SHEET_UI;
import ssm.model.Slide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import static ssm.StartupConstants.BASE_DIR;
import static ssm.StartupConstants.HTML_BASE;
import static ssm.StartupConstants.JS_BASE;
import static ssm.StartupConstants.PATH_DATA;
import static ssm.file.SlideShowFileManager.CAPTION;
import static ssm.file.SlideShowFileManager.JSON_IMAGE_FILE_NAME;
import static ssm.file.SlideShowFileManager.JSON_IMAGE_PATH;
import static ssm.file.SlideShowFileManager.SLASH;

/**
 * The class to initiate the view slide show window
 * and its functions.
 * @author Shishuai Li
 */
public class ViewSlideShow {    
    
    int slideNum;
    Stage viewStage;
    BorderPane viewPane;
    SlideEditView slideEditView;
    ImageView imageView;
    Image image;
    Label caption;
    Label title;
    Button previous;
    Button next;
    ObservableList<Slide> slides;
    static WebView browser=new WebView();
    
    public ViewSlideShow(SlideShowMakerView ui){
        if(!ui.getFileController().isSaved()){
            try {
                ui.getFileController().promptToSave();
            } catch (IOException ex) {
                Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Failed when saving slideshow,"+
                        "Try again.");
                errorAlert.showAndWait();
            }
        }
        File slideShowPath=new File(PATH_SITES+ui.getSlideShow().getTitle());
        if(!slideShowPath.exists()){
            Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Slideshow website does not exist. "+
                        "Try again.");
                errorAlert.showAndWait();
                return;
        }
        
        WebEngine webEngine = ViewSlideShow.browser.getEngine();
        Path htmlPath=slideShowPath.toPath().resolve(HTML_BASE);
        try {
        	
            webEngine.load(htmlPath.toUri().toURL().toString());
        } catch (MalformedURLException ex) {
           Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Invalid URL address. "+
                        "Try again.");
                errorAlert.showAndWait();
                return;
        }
        viewPane=new BorderPane();
        viewPane.setCenter(ViewSlideShow.browser);
        (viewStage=new Stage()).setScene(new Scene(viewPane));
        viewStage.setTitle(ui.getSlideShow().getTitle());
        viewStage.getIcons().add(new Image("file:"+PATH_ICONS+"window.png"));
        
        viewStage.show();  
        
        
    }

    public void updateSlide(){   
        image=slides.get(slideNum).getImage();
        imageView.setImage(image);
        caption.setText(slides.get(slideNum).getCaption());
        
    }
    
}
