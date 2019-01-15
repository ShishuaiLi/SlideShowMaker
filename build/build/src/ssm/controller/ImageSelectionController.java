package ssm.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import static ssm.StartupConstants.PATH_SLIDE_SHOW_IMAGES;
import ssm.model.Slide;
import ssm.view.SlideEditView;

/**
 * This controller provides a controller for when the user chooses to
 * select an image for the slide show.
 * 
 * @author McKilla Gorilla & _____________
 */
public class ImageSelectionController {
    
    /**
     * Default contstructor doesn't need to initialize anything
     */
    public ImageSelectionController() { }
    
    /**
     * This function provides the response to the user's request to
     * select an image.
     * 
     * @param slideToEdit - Slide for which the user is selecting an image.
     * 
     * @param view The user interface control group where the image
     * will appear after selection.
     */
    public void processSelectImage(Slide slideToEdit, SlideEditView view) {
	FileChooser imageFileChooser = new FileChooser();
	
	// SET THE STARTING DIRECTORY
	imageFileChooser.setInitialDirectory(new File(PATH_SLIDE_SHOW_IMAGES));
	
	// LET'S ONLY SEE IMAGE FILES
	FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
	FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
	FileChooser.ExtensionFilter gifFilter = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.GIF");
	imageFileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter, gifFilter);
	
	// LET'S OPEN THE FILE CHOOSER
	File file = imageFileChooser.showOpenDialog(view.getScene().getWindow());
	if (file != null) {
            //Path cwd=Paths.get("").toAbsolutePath();
            //System.out.println(cwd.toString());
            Path filePath=file.toPath();
            //System.out.println(filePath.toString());
	    //String path = cwd.relativize(filePath.getParent()).toString();
            String path =filePath.getParent().toString();
	    //System.out.println(path);
	    String fileName = file.getName();
	    slideToEdit.setImage(path, fileName);
	    view.updateSlideImage();
        view.getFileController().markAsEdited();
	}	    
	else {
	    // provide error message for no files selected
		Alert errorAlert=new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("No image selected, try again.");
        errorAlert.showAndWait();
	}
    }
    
}
