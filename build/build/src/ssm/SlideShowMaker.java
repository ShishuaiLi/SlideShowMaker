package ssm;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;
import static ssm.LanguagePropertyType.TITLE_WINDOW;
import static ssm.StartupConstants.PATH_DATA;
import static ssm.StartupConstants.PROPERTIES_SCHEMA_FILE_NAME;
import static ssm.StartupConstants.UI_PROPERTIES_FILE_NAME_EN;
import static ssm.StartupConstants.UI_PROPERTIES_FILE_NAME_CN;
import ssm.error.ErrorHandler;
import ssm.file.SlideShowFileManager;
import ssm.view.SlideShowMakerView;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static ssm.StartupConstants.PATH_ICONS;


/**
 * SlideShowMaker is a program for making custom image slideshows. It will allow
 * the user to name their slideshow, select images to use, select captions for
 * the images, and the order of appearance for slides.
 *
 * @author McKilla Gorilla & _____________
 */
public class SlideShowMaker extends Application{
	
	public static void main(String[] args) {
        // TODO code application logic here
        launch(args);
    }
	
    // THIS WILL PERFORM SLIDESHOW READING AND WRITING
    SlideShowFileManager fileManager = new SlideShowFileManager();

    // THIS HAS THE FULL USER INTERFACE AND ONCE IN EVENT
    // HANDLING MODE, BASICALLY IT BECOMES THE FOCAL
    // POINT, RUNNING THE UI AND EVERYTHING ELSE
    SlideShowMakerView ui = new SlideShowMakerView(fileManager);

    public void start(Stage primaryStage) throws Exception {

        // LOAD APP SETTINGS INTO THE GUI AND START IT UP
        boolean success = loadProperties();
        if (success) {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String appTitle = props.getProperty(TITLE_WINDOW);

	    // NOW START THE UI IN EVENT HANDLING MODE
	    ui.startUI(primaryStage, appTitle);
	} // THERE WAS A PROBLEM LOADING THE PROPERTIES FILE
	else {
	    // LET THE ERROR HANDLER PROVIDE THE RESPONSE
	    ErrorHandler errorHandler = ui.getErrorHandler();
	    System.exit(0);
	}
    }
    
    /**
     * Loads this application's properties file, which has a number of settings
     * for initializing the user interface.
     * 
     * @return true if the properties file was loaded successfully, false otherwise.
     */
    public boolean loadProperties() {
        try {
        	// language selection window
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("Language Selection");
        	alert.setHeaderText(null);
        	alert.setContentText("Please select your language:");
        	ButtonType buttonTypeOne = new ButtonType("English");
        	ButtonType buttonTypeTwo = new ButtonType("ÖÐÎÄ");
        	alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        	Optional<ButtonType> result = alert.showAndWait();
        	String languageFileName=null;
        	if (result.get() == buttonTypeOne){
        		languageFileName=UI_PROPERTIES_FILE_NAME_EN;
        	} else if (result.get() == buttonTypeTwo) {
        		languageFileName=UI_PROPERTIES_FILE_NAME_CN;
        	}

        	
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(PropertiesManager.DATA_PATH_PROPERTY, PATH_DATA);
	    props.loadProperties(languageFileName, PROPERTIES_SCHEMA_FILE_NAME);
            return true;
       } catch (InvalidXMLFileFormatException ixmlffe) {
            // SOMETHING WENT WRONG INITIALIZING THE XML FILE
            ErrorHandler eH = ui.getErrorHandler();
            eH.processError(LanguagePropertyType.ERROR_PROPERTIES_FILE_LOADING,LanguagePropertyType.ERROR_PROPERTIES_FILE_LOADING_TITLE , ixmlffe.toString());
            return false;
        }        
    }

    /**
     * This is where the application starts execution. We'll load the
     * application properties and then use them to build our user interface and
     * start the window in event handling mode. Once in that mode, all code
     * execution will happen in response to user requests.
     *
     * @param args This application does not use any command line arguments.
     */
    
}
