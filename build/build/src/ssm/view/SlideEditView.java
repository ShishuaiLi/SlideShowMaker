package ssm.view;

import java.io.File;
import java.net.URL;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import static ssm.StartupConstants.CSS_CLASS_LABEL_TEXT;
import static ssm.StartupConstants.CSS_CLASS_SLIDE_EDIT_VIEW;
import static ssm.StartupConstants.DEFAULT_THUMBNAIL_WIDTH;
import ssm.controller.FileController;
import ssm.controller.ImageSelectionController;
import ssm.model.Slide;
import static ssm.file.SlideShowFileManager.SLASH;

/**
 * This UI component has the controls for editing a single slide
 * in a slide show, including controls for selected the slide image
 * and changing its caption.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideEditView extends HBox {
    private FileController fileController;
    // SLIDE THIS COMPONENT EDITS
    private Slide slide;
    
    // DISPLAYS THE IMAGE FOR THIS SLIDE
    ImageView imageSelectionView;
    
    // CONTROLS FOR EDITING THE CAPTION    
    private TextField captionTextField;
    
    // PROVIDES RESPONSES FOR IMAGE SELECTION
    ImageSelectionController imageController;

    /**
     * THis constructor initializes the full UI for this component, using
     * the initSlide data for initializing values./
     * 
     * @param initSlide The slide to be edited by this component.
     */
    public SlideEditView(Slide initSlide,FileController fileController) {
	// FIRST SELECT THE CSS STYLE CLASS FOR THIS CONTAINER
	this.getStyleClass().add(CSS_CLASS_SLIDE_EDIT_VIEW);
	this.fileController=fileController;
	// KEEP THE SLIDE FOR LATER
	slide = initSlide;
	
	// MAKE SURE WE ARE DISPLAYING THE PROPER IMAGE
	imageSelectionView = new ImageView();
	updateSlideImage();

	// SETUP THE CAPTION CONTROLS
        VBox captionVBox;
	captionVBox = new VBox();
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        Label captionLabel;
	captionLabel = new Label(props.getProperty(LanguagePropertyType.LABEL_CAPTION));
        captionLabel.getStyleClass().add(CSS_CLASS_LABEL_TEXT);
	captionTextField = new TextField();
        captionTextField.setText(props.getProperty(LanguagePropertyType.DEFAULT_IMAGE_CAPTION));
        captionTextField.setOnAction(e->{
            slide.setCaption(captionTextField.getText());
            fileController.markAsEdited();
        });
	captionVBox.getChildren().add(captionLabel);
	captionVBox.getChildren().add(captionTextField);

	// LAY EVERYTHING OUT INSIDE THIS COMPONENT
	getChildren().add(imageSelectionView);
	getChildren().add(captionVBox);

	// SETUP THE EVENT HANDLERS
	imageController = new ImageSelectionController();
	imageSelectionView.setOnMouseClicked(e -> {
	    imageController.processSelectImage(slide, this);
	});
        
    }
    
    /**
     * This function gets the image for the slide and uses it to
     * update the image displayed.
     */
    public void updateSlideImage() {
	String imagePath = slide.getImagePath() + SLASH+slide.getImageFileName();
	File file = new File(imagePath);
	try {
	    // GET AND SET THE IMAGE
	    URL fileURL = file.toURI().toURL();
	    Image slideImage = new Image(fileURL.toExternalForm());
	    imageSelectionView.setImage(slideImage);
	    
	    // AND RESIZE IT
	    double scaledWidth = DEFAULT_THUMBNAIL_WIDTH;
	    double perc = scaledWidth / slideImage.getWidth();
	    double scaledHeight = slideImage.getHeight() * perc;
	    imageSelectionView.setFitWidth(scaledWidth);
	    imageSelectionView.setFitHeight(scaledHeight);
	} catch (Exception e) {
	    // @todo - use Error handler to respond to missing image
	}
    }    

    /**
     * @return the captionTextField
     */
    public TextField getCaptionTextField() {
        return captionTextField;
    }

    /**
     * @return the slide
     */
    public Slide getSlide() {
        return slide;
    }

    /**
     * @return the fileController
     */
    public FileController getFileController() {
        return fileController;
    }
}