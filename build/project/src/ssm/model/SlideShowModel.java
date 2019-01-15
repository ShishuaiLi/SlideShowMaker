package ssm.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import static ssm.StartupConstants.CSS_CLASS_SLIDE_SELECTED_VIEW;
import ssm.view.SlideEditView;
import ssm.view.SlideShowMakerView;

/**
 * This class manages all the data associated with a slideshow.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideShowModel {
    SlideShowMakerView ui;
    String title;
    ObservableList<Slide> slides;
    SlideEditView selectedSlide;
    
    public SlideShowModel(SlideShowMakerView initUI) {
	ui = initUI;
	slides = FXCollections.observableArrayList();
        title=PropertiesManager.getPropertiesManager().getProperty("DEFAULT_SLIDE_SHOW_TITLE");
	reset();	
    }

    // ACCESSOR METHODS
    public boolean isSlideSelected() {
	return selectedSlide != null;
    }
    
    public ObservableList<Slide> getSlides() {
	return slides;
    }
    
    public SlideEditView getSelectedSlide() {
	return selectedSlide;
    }

    public String getTitle() { 
	return title; 
    }
    
    // MUTATOR METHODS
    public void setSelectedSlide(SlideEditView initSelectedSlide) {
	selectedSlide = initSelectedSlide;
    }
    
    public void setTitle(String initTitle) { 
	title = initTitle; 
    }

    // SERVICE METHODS
    
    /**
     * Resets the slide show to have no slides and a default title.
     */
    public void reset() {
	slides.clear();
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	title = props.getProperty(LanguagePropertyType.DEFAULT_SLIDE_SHOW_TITLE);
	selectedSlide = null;
    }

    /**
     * Adds a slide to the slide show with the parameter settings.
     * @param initImageFileName File name of the slide image to add.
     * @param initImagePath File path for the slide image to add.
     */
    public void addSlide(   String initImageFileName,
			    String initImagePath) {
	Slide slideToAdd = new Slide(initImageFileName, initImagePath);
        
	slides.add(slideToAdd);
        ui.getFileController().markAsEdited();
	
        SlideEditView slideEditor = new SlideEditView(slideToAdd,ui.getFileController());
            slideToAdd.setSlideEditView(slideEditor);
            slideEditor.setOnMouseClicked(e->{
                if(this.selectedSlide!=null){
                    selectedSlide.getStyleClass().remove(1);
                }
                setSelectedSlide(slideEditor);
                slideEditor.getStyleClass().add(CSS_CLASS_SLIDE_SELECTED_VIEW);
                ui.updateSideBarControls(false);
            });
	ui.getSlidesEditorPane().getChildren().add(slideEditor);
    }
    public void addSlide(   String initImageFileName,
			    String initImagePath,String caption) {
	Slide slideToAdd = new Slide(initImageFileName, initImagePath,caption);
	slides.add(slideToAdd);
	this.selectedSlide=null;
	ui.reloadSlideShowPane(this);
    }
}