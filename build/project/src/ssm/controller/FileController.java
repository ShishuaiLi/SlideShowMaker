package ssm.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.CANCEL_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import static ssm.LanguagePropertyType.PROMPT_NEW_SLIDE;
import static ssm.LanguagePropertyType.PROMPT_TO_SAVE_TEXT;
import static ssm.LanguagePropertyType.PROMPT_TO_SAVE_TITLE;
import static ssm.LanguagePropertyType.TOOLTIP_NEW_SLIDE_SHOW;
import static ssm.LanguagePropertyType.TOOLTIP_NEXT_SLIDE;
import static ssm.LanguagePropertyType.TOOLTIP_PREVIOUS_SLIDE;
import static ssm.StartupConstants.CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON;
import static ssm.StartupConstants.DEFAULT_THUMBNAIL_WIDTH;
import static ssm.StartupConstants.ICON_NEW_SLIDE_SHOW;
import static ssm.StartupConstants.ICON_NEXT;
import static ssm.StartupConstants.ICON_PREVIOUS;
import ssm.model.SlideShowModel;
import ssm.error.ErrorHandler;
import ssm.file.SlideShowFileManager;
import ssm.view.SlideShowMakerView;
import static ssm.StartupConstants.PATH_SLIDE_SHOWS;
import static ssm.file.SlideShowFileManager.SLASH;
import ssm.model.Slide;
import ssm.view.SlideEditView;
/**
 * This class serves as the controller for all file toolbar operations,
 * driving the loading and saving of slide shows, among other things.
 * 
 * @author McKilla Gorilla & _____________
 */
public class FileController {

    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    private boolean saved;

    // THE APP UI
    private SlideShowMakerView ui;
    
    // THIS GUY KNOWS HOW TO READ AND WRITE SLIDE SHOW DATA
    private SlideShowFileManager slideShowIO;

    /**
     * This default constructor starts the program without a slide show file being
     * edited.
     *
     * @param initSlideShowIO The object that will be reading and writing slide show
     * data.
     */
    public FileController(SlideShowMakerView initUI, SlideShowFileManager initSlideShowIO) {
        // NOTHING YET
        saved = true;
	ui = initUI;
        slideShowIO = initSlideShowIO;
    }
    
    public void markAsEdited() {
        saved = false;
        ui.updateToolbarControls(saved);
    }

    /**
     * This method starts the process of editing a new slide show. If a pose is
     * already being edited, it will prompt the user to save it first.
     */
    public void handleNewSlideShowRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave();
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                // RESET THE DATA, WHICH SHOULD TRIGGER A RESET OF THE UI
                SlideShowModel slideShow = ui.getSlideShow();
		slideShow.reset();
                ui.reloadSlideShowPane(slideShow);
                saved = false;

                // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
                // THE APPROPRIATE CONTROLS
                ui.updateToolbarControls(saved);

                // TELL THE USER THE SLIDE SHOW HAS BEEN CREATED
                // @todo
                JOptionPane.showMessageDialog(null, PropertiesManager.getPropertiesManager().getProperty(PROMPT_NEW_SLIDE));
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            eH.processError(LanguagePropertyType.ERROR_DATA_FILE_LOADING,LanguagePropertyType.ERROR_DATA_FILE_LOADING_TITLE , ioe.toString());
            
            // @todo provide error message
        }
    }

    /**
     * This method lets the user open a slideshow saved to a file. It will also
     * make sure data for the current slideshow is not lost.
     */
    public void handleLoadSlideShowRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave();
            }

            // IF THE USER REALLY WANTS TO OPEN A POSE
            if (continueToOpen) {
                // GO AHEAD AND PROCEED MAKING A NEW POSE
                promptToOpen();
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            eH.processError(LanguagePropertyType.ERROR_DATA_FILE_LOADING,LanguagePropertyType.ERROR_DATA_FILE_LOADING_TITLE , ioe.toString());
            //@todo provide error message
        }
    }

    /**
     * This method will save the current slideshow to a file. Note that we already
     * know the name of the file, so we won't need to prompt the user.
     */
    public boolean handleSaveSlideShowRequest() {
        try {
	    // GET THE SLIDE SHOW TO SAVE
	    SlideShowModel slideShowToSave = ui.getSlideShow();
	    
            // SAVE IT TO A FILE
            slideShowIO.saveSlideShow(slideShowToSave);

            // MARK IT AS SAVED
            saved = true;

            // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
            // THE APPROPRIATE CONTROLS
            ui.updateToolbarControls(saved);
	    return true;
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            // @todo
            eH.processError(LanguagePropertyType.ERROR_DATA_FILE_LOADING,LanguagePropertyType.ERROR_DATA_FILE_LOADING_TITLE , ioe.toString());
	    return false;
        }
    }
    
     /**
     * This method will exit the application, making sure the user doesn't lose
     * any data first.
     */
    public void handleExitRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToExit = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE
                continueToExit = promptToSave();
            }

            // IF THE USER REALLY WANTS TO EXIT THE APP
            if (continueToExit) {
                // EXIT THE APPLICATION
                System.exit(0);
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            // @todo
            eH.processError(LanguagePropertyType.ERROR_DATA_FILE_LOADING,LanguagePropertyType.ERROR_DATA_FILE_LOADING_TITLE , ioe.toString());
        }
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. Note that it could be used
     * in multiple contexts before doing other actions, like creating a new
     * pose, or opening another pose, or exiting. Note that the user will be
     * presented with 3 options: YES, NO, and CANCEL. YES means the user wants
     * to save their work and continue the other action (we return true to
     * denote this), NO means don't save the work but continue with the other
     * action (true is returned), CANCEL means don't save the work and don't
     * continue with the other action (false is retuned).
     *
     * @return true if the user presses the YES option to save, true if the user
     * presses the NO option to not save, false if the user presses the CANCEL
     * option to not continue.
     */
    public boolean promptToSave() throws IOException {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        boolean saveWork = true; // @todo change this to prompt
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        int option=JOptionPane.showConfirmDialog(null, props.getProperty(PROMPT_TO_SAVE_TEXT.toString()),props.getProperty(PROMPT_TO_SAVE_TITLE.toString()) , YES_NO_CANCEL_OPTION, WARNING_MESSAGE);
        if(option!=YES_OPTION) saveWork=false;

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (saveWork) {
            SlideShowModel slideShow = ui.getSlideShow();
            slideShowIO.saveSlideShow(slideShow);
            saved = true;
            ui.updateToolbarControls(saved);
        } // IF THE USER SAID CANCEL, THEN WE'LL TELL WHOEVER
        // CALLED THIS THAT THE USER IS NOT INTERESTED ANYMORE
        else if(option==CANCEL_OPTION){
            return false;
        }

        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
        return true;
    }

    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen() {
        // AND NOW ASK THE USER FOR THE COURSE TO OPEN
        FileChooser slideShowFileChooser = new FileChooser();
        slideShowFileChooser.setInitialDirectory(new File(PATH_SLIDE_SHOWS));
        File selectedFile = slideShowFileChooser.showOpenDialog(ui.getWindow());

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
		SlideShowModel slideShowToLoad = ui.getSlideShow();
                slideShowIO.loadSlideShow(slideShowToLoad, selectedFile.getAbsolutePath());
                ui.reloadSlideShowPane(slideShowToLoad);
                saved = true;
                ui.updateToolbarControls(saved);
            } catch (Exception e) {
                ErrorHandler eH = ui.getErrorHandler();
                // @todo
                eH.processError(LanguagePropertyType.ERROR_DATA_FILE_LOADING,LanguagePropertyType.ERROR_DATA_FILE_LOADING_TITLE , e.toString());
            }
        }
    }

    /**
     * This mutator method marks the file as not saved, which means that when
     * the user wants to do a file-type operation, we should prompt the user to
     * save current work first. Note that this method should be called any time
     * the pose is changed in some way.
     */
    public void markFileAsNotSaved() {
        saved = false;
    }

    /**
     * Accessor method for checking to see if the current pose has been saved
     * since it was last editing. If the current file matches the pose data,
     * we'll return true, otherwise false.
     *
     * @return true if the current pose is saved to the file, false otherwise.
     */
    public boolean isSaved() {
        return saved;
    }
}

