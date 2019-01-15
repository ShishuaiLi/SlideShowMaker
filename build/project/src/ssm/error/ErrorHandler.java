package ssm.error;

import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import ssm.view.SlideShowMakerView;

/**
 * This class provides error messages to the user when the occur. Note
 * that error messages should be retrieved from language-dependent XML files
 * and should have custom messages that are different depending on
 * the type of error so as to be informative concerning what went wrong.
 * 
 * @author McKilla Gorilla & _____________
 */
public class ErrorHandler {
    // APP UI
    private SlideShowMakerView ui;
    
    // KEEP THE APP UI FOR LATER
    public ErrorHandler(SlideShowMakerView initUI) {
	ui = initUI;
    }
    
    /**
     * This method provides all error feedback. It gets the feedback text,
     * which changes depending on the type of error, and presents it to
     * the user in a dialog box.
     * 
     * @param errorType Identifies the type of error that happened, which
     * allows us to get and display different text for different errors.
     */
    public void processError(LanguagePropertyType errorType,LanguagePropertyType  errorDialogTitle, String errorDialogMessage)
    {
        // GET THE FEEDBACK TEXT
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String errorFeedbackText = props.getProperty(errorType);
        String errorFeedbackTitle=props.getProperty(errorDialogTitle);
        JOptionPane.showMessageDialog(null,errorFeedbackText +"\n"+errorDialogMessage, errorFeedbackTitle, ERROR_MESSAGE);
             
        // POP OPEN A DIALOG TO DISPLAY TO THE USER
        // @todo
    }    
}
