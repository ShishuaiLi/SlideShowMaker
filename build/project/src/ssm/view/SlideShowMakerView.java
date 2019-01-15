package ssm.view;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;

import static ssm.LanguagePropertyType.*; 
import static ssm.StartupConstants.*;
import static ssm.StartupConstants.PATH_ICONS;
import static ssm.StartupConstants.STYLE_SHEET_UI;
import ssm.controller.FileController;
import ssm.controller.SlideShowEditController;
import ssm.model.Slide;
import ssm.model.SlideShowModel;
import ssm.error.ErrorHandler;
import ssm.file.SlideShowFileManager;


/**
 * This class provides the User Interface for this application,
 * providing controls and the entry points for creating, loading, 
 * saving, editing, and viewing slide shows.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideShowMakerView {

    // THIS IS THE MAIN APPLICATION UI WINDOW AND ITS SCENE GRAPH
    Stage primaryStage;
    Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION GUI
    private BorderPane ssmPane;

    // THIS IS THE TOP TOOLBAR AND ITS CONTROLS
    FlowPane fileToolbarPane;
    Button newSlideShowButton;
    Button loadSlideShowButton;
    Button saveSlideShowButton;
    Button viewSlideShowButton;
    Button exitButton;
    
    // WORKSPACE
    HBox workspace;

    // THIS WILL GO IN THE LEFT SIDE OF THE SCREEN
    VBox slideEditToolbar;
    Button addSlideButton;
    Button removeSlideButton;
    Button moveUpButton, moveDownButton;
    
    // AND THIS WILL GO IN THE CENTER
    ScrollPane slidesEditorScrollPane;
    private VBox slidesEditorPane;

    // THIS IS THE SLIDE SHOW WE'RE WORKING WITH
    SlideShowModel slideShow;

    // THIS IS FOR SAVING AND LOADING SLIDE SHOWS
    SlideShowFileManager fileManager;

    // THIS CLASS WILL HANDLE ALL ERRORS FOR THIS PROGRAM
    private ErrorHandler errorHandler;

    // THIS CONTROLLER WILL ROUTE THE PROPER RESPONSES
    // ASSOCIATED WITH THE FILE TOOLBAR
    private FileController fileController;
    
    // THIS CONTROLLER RESPONDS TO SLIDE SHOW EDIT BUTTONS
    private SlideShowEditController editController;

    /**
     * Default constructor, it initializes the GUI for use, but does not yet
     * load all the language-dependent controls, that needs to be done via the
     * startUI method after the user has selected a language.
     */
    public SlideShowMakerView(SlideShowFileManager initFileManager) {
	// FIRST HOLD ONTO THE FILE MANAGER
	fileManager = initFileManager;
	
	// MAKE THE DATA MANAGING MODEL
	slideShow = new SlideShowModel(this);

	// WE'LL USE THIS ERROR HANDLER WHEN SOMETHING GOES WRONG
	errorHandler = new ErrorHandler(this);
    }

    // ACCESSOR METHODS
    public SlideShowModel getSlideShow() {
	return slideShow;
    }

    public Stage getWindow() {
	return primaryStage;
    }

    public ErrorHandler getErrorHandler() {
	return errorHandler;
    }

    /**
     * Initializes the UI controls and gets it rolling.
     * 
     * @param initPrimaryStage The window for this application.
     * 
     * @param windowTitle The title for this window.
     */
    public void startUI(Stage initPrimaryStage, String windowTitle) {
	// THE TOOLBAR ALONG THE NORTH
	initFileToolbar();

        // INIT THE CENTER WORKSPACE CONTROLS BUT DON'T ADD THEM
	// TO THE WINDOW YET
	initWorkspace();

	// NOW SETUP THE EVENT HANDLERS
	initEventHandlers();

	// AND FINALLY START UP THE WINDOW (WITHOUT THE WORKSPACE)
	// KEEP THE WINDOW FOR LATER
	primaryStage = initPrimaryStage;
	initWindow(windowTitle);
    }

    // UI SETUP HELPER METHODS
    private void initWorkspace() {
	// FIRST THE WORKSPACE ITSELF, WHICH WILL CONTAIN TWO REGIONS
	workspace = new HBox();
	workspace.setStyle("-fx-background-color: null;");
	// THIS WILL GO IN THE LEFT SIDE OF THE SCREEN
	slideEditToolbar = new VBox();        
	slideEditToolbar.getStyleClass().add(CSS_CLASS_SLIDE_SHOW_EDIT_VBOX);
        slideEditToolbar.spacingProperty().bind(slideEditToolbar.heightProperty().multiply(0.15));
        slideEditToolbar.setMaxWidth(50);
        slideEditToolbar.setMinWidth(50);
        slideEditToolbar.setPrefWidth(50);
	addSlideButton = this.initChildButton(slideEditToolbar,		ICON_ADD_SLIDE,	    TOOLTIP_ADD_SLIDE,	    CSS_CLASS_VERTICAL_TOOLBAR_BUTTON,  true);
        removeSlideButton=this.initChildButton(slideEditToolbar, ICON_REMOVE_SLIDE, TOOLTIP_REMOVE_SLIDE, CSS_CLASS_VERTICAL_TOOLBAR_BUTTON, true);
	moveUpButton=this.initChildButton(slideEditToolbar, ICON_MOVE_UP, TOOLTIP_MOVE_UP, CSS_CLASS_VERTICAL_TOOLBAR_BUTTON, true);
        moveDownButton=this.initChildButton(slideEditToolbar, ICON_MOVE_DOWN, TOOLTIP_MOVE_DOWN, CSS_CLASS_VERTICAL_TOOLBAR_BUTTON, true);
        
        Rectangle clip=new Rectangle(0,0,50,Double.MAX_VALUE);
        slideEditToolbar.setClip(clip);
	// AND THIS WILL GO IN THE CENTER
	slidesEditorPane = new VBox();        
	slidesEditorScrollPane = new ScrollPane(getSlidesEditorPane());
        slidesEditorScrollPane.setFitToWidth(true);        
	initTitlePane();
	// NOW PUT THESE TWO IN THE WORKSPACE
	workspace.getChildren().add(slideEditToolbar);
	workspace.getChildren().add(slidesEditorScrollPane);
    }
    /**
     * This class will reset the title pane and add it on SlidesEditorPane.
     */
    private void initTitlePane(){
        HBox titlePane=new HBox();
        titlePane.setPadding(new Insets(10,10,10,10));
        titlePane.setSpacing(10);
        titlePane.setAlignment(Pos.CENTER);
        Label titleLabel=new Label(PropertiesManager.getPropertiesManager().getProperty("LABEL_TITLE"));
        titleLabel.getStyleClass().add(CSS_CLASS_LABEL_TEXT);
        TextField titleField=new TextField();
        titleField.setPromptText(PropertiesManager.getPropertiesManager().getProperty("DEFAULT_SLIDE_SHOW_TITLE"));
        titleField.setText(slideShow.getTitle());
        titleField.focusedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if(!titleField.isFocused()){
                    titleField.setText(slideShow.getTitle());
                }
            }
        });
        titleField.setOnAction(e->{
            if(!slideShow.getTitle().equals(titleField.getText()))
                this.getFileController().markAsEdited();
            
            slideShow.setTitle(titleField.getText());
        });
        
        titlePane.getChildren().addAll(titleLabel,titleField);
        getSlidesEditorPane().getChildren().add(titlePane);
    }

    private void initEventHandlers() {
	// FIRST THE FILE CONTROLS
	fileController=new FileController(this, fileManager);
	newSlideShowButton.setOnAction(e -> {
	    getFileController().handleNewSlideShowRequest();
	});
	loadSlideShowButton.setOnAction(e -> {
	    getFileController().handleLoadSlideShowRequest();
	});
	saveSlideShowButton.setOnAction(e -> {
	    getFileController().handleSaveSlideShowRequest();
	});
        viewSlideShowButton.setOnAction(e->{
        	
            ViewSlideShow vss=new ViewSlideShow(this);
            ViewSlideShow.browser.getEngine().reload();
        });
	exitButton.setOnAction(e -> {
	    getFileController().handleExitRequest();
	});
	
	// THEN THE SLIDE SHOW EDIT CONTROLS
	editController = new SlideShowEditController(this);
	addSlideButton.setOnAction(e -> {
	    editController.processAddSlideRequest();
	});
        removeSlideButton.setOnAction(e->{
            SlideEditView slideSelected = slideShow.getSelectedSlide();
            if(slideSelected!=null){
                slideShow.getSlides().remove(slideSelected.getSlide());
                slidesEditorPane.getChildren().remove(slideSelected);
                slideShow.setSelectedSlide(null);
                updateSideBarControls(true);
                fileController.markAsEdited();              
                
            }
        });
    moveUpButton.setOnAction(e->{
        SlideEditView slideSelected = slideShow.getSelectedSlide();
        Slide slide=slideSelected.getSlide();
            if(slideSelected!=null){
                
                int index=slideShow.getSlides().indexOf(slide)-1;
                if(index<0) return;
                slideShow.getSlides().remove(slide);
                
                slideShow.getSlides().add(index, slide);                
              
                slidesEditorPane.getChildren().remove(slideSelected);
                slidesEditorPane.getChildren().add(++index, slideSelected);                                
                
                fileController.markAsEdited();              
                
            }
    });
            
            moveDownButton.setOnAction(e->{
                SlideEditView slideSelected = slideShow.getSelectedSlide();
                Slide slide=slideSelected.getSlide();
                if(slideSelected!=null){
                    int index=slideShow.getSlides().indexOf(slide)+1;
                    if(index>=slideShow.getSlides().size()) return;
                 slideShow.getSlides().remove(slide);
                 
                 slideShow.getSlides().add(index, slide);                
              
                 slidesEditorPane.getChildren().remove(slideSelected);
                 slidesEditorPane.getChildren().add(++index, slideSelected);                                
                
                 fileController.markAsEdited();              
                
            }
            });
    }

    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initFileToolbar() {
	fileToolbarPane = new FlowPane();
        fileToolbarPane.getStyleClass().add(CSS_CLASS_SLIDE_SHOW_FILE_HBOX);
        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
	// START AS ENABLED (false), WHILE OTHERS DISABLED (true)
	newSlideShowButton = initChildButton(fileToolbarPane, ICON_NEW_SLIDE_SHOW,	TOOLTIP_NEW_SLIDE_SHOW,	    CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, false);
	loadSlideShowButton = initChildButton(fileToolbarPane, ICON_LOAD_SLIDE_SHOW,	TOOLTIP_LOAD_SLIDE_SHOW,    CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, false);
	saveSlideShowButton = initChildButton(fileToolbarPane, ICON_SAVE_SLIDE_SHOW,	TOOLTIP_SAVE_SLIDE_SHOW,    CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, true);
	viewSlideShowButton = initChildButton(fileToolbarPane, ICON_VIEW_SLIDE_SHOW,	TOOLTIP_VIEW_SLIDE_SHOW,    CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, true);
	exitButton = initChildButton(fileToolbarPane, ICON_EXIT, TOOLTIP_EXIT, CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, false);
    }

    private void initWindow(String windowTitle) {
	// SET THE WINDOW TITLE
	primaryStage.setTitle(windowTitle);

	// GET THE SIZE OF THE SCREEN
	Screen screen = Screen.getPrimary();
	Rectangle2D bounds = screen.getVisualBounds();

	// AND USE IT TO SIZE THE WINDOW
        primaryStage.setWidth((bounds.getWidth())*0.75);
	primaryStage.setHeight((bounds.getHeight())*0.75);
	primaryStage.setX((bounds.getWidth())*0.25/2);
	primaryStage.setY((bounds.getHeight())*0.25/2);
	

        // SETUP THE UI, NOTE WE'LL ADD THE WORKSPACE LATER
	ssmPane = new BorderPane();
        ssmPane.getStyleClass().add(CSS_CLASS_BACKGROUND);
	ssmPane.setTop(fileToolbarPane);	
	primaryScene = new Scene(ssmPane);
	Image image = new Image("file:"+PATH_ICONS+"lol.png");
        primaryScene.setCursor(new ImageCursor(image));
        // NOW TIE THE SCENE TO THE WINDOW, SELECT THE STYLESHEET
	// WE'LL USE TO STYLIZE OUR GUI CONTROLS, AND OPEN THE WINDOW
	primaryScene.getStylesheets().add("file:"+STYLE_SHEET_UI);
        //primaryScene.getStylesheets().add("SlideShowMakerStyle.css");
    //System.out.println(getClass().getResource("SlideShowMakerStyle.css").toExternalForm());
	//primaryScene.getStylesheets().add(getClass().getResource("SlideShowMakerStyle.css").toExternalForm());
	primaryStage.setScene(primaryScene);
        primaryStage.getIcons().add(new Image("file:"+PATH_ICONS+PropertiesManager.getPropertiesManager().getProperty("WINDOW_ICON_NAME")));
	primaryStage.show();
    }
    
    /**
     * This helps initialize buttons in a toolbar, constructing a custom button
     * with a customly provided icon and tooltip, adding it to the provided
     * toolbar pane, and then returning it.
     */
    public Button initChildButton(
	    Pane toolbar, 
	    String iconFileName, 
	    LanguagePropertyType tooltip, 
	    String cssClass,
	    boolean disabled) {
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	String imagePath = "file:" + PATH_ICONS + iconFileName;
	Image buttonImage = new Image(imagePath);
	Button button = new Button();
	button.getStyleClass().add(cssClass);
	button.setDisable(disabled);
	button.setGraphic(new ImageView(buttonImage));
	Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip.toString()));
	button.setTooltip(buttonTooltip);
	toolbar.getChildren().add(button);
	return button;
    }
    public Button initChildButton(	     
	    String iconFileName, 
	    LanguagePropertyType tooltip, 
	    String cssClass) {
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	String imagePath = "file:" + PATH_ICONS + iconFileName;
	Image buttonImage = new Image(imagePath);
	Button button = new Button();
	button.getStyleClass().add(cssClass);
	
	button.setGraphic(new ImageView(buttonImage));
	Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip.toString()));
	button.setTooltip(buttonTooltip);
	
	return button;
    }
    
    /**
     * Updates the enabled/disabled status of all toolbar
     * buttons.
     * 
     * @param saved 
     */
    public void updateToolbarControls(boolean saved) {
	// FIRST MAKE SURE THE WORKSPACE IS THERE
	ssmPane.setCenter(workspace);
	
	// NEXT ENABLE/DISABLE BUTTONS AS NEEDED IN THE FILE TOOLBAR
	saveSlideShowButton.setDisable(saved);
	viewSlideShowButton.setDisable(false);
	
	// AND THE SLIDESHOW EDIT TOOLBAR
	addSlideButton.setDisable(false);
    }
    public void updateSideBarControls(boolean bo){
        if(bo==false){
    removeSlideButton.setDisable(false);
    moveUpButton.setDisable(false);
    moveDownButton.setDisable(false);
    }
        else{
            removeSlideButton.setDisable(true);
            moveUpButton.setDisable(true);
            moveDownButton.setDisable(true);
        }
    }

    /**
     * Uses the slide show data to reload all the components for
     * slide editing.
     * 
     * @param slideShowToLoad SLide show being reloaded.
     */
    public void reloadSlideShowPane(SlideShowModel slideShowToLoad) {
        
	getSlidesEditorPane().getChildren().clear();
        initTitlePane();
        
	for (Slide slide : slideShowToLoad.getSlides()) {
	    SlideEditView slideEditor = new SlideEditView(slide,fileController);
            slide.setSlideEditView(slideEditor);
            if(slide.getCaption()!=null){
                slideEditor.getCaptionTextField().setText(slide.getCaption());
            }
            
            slideEditor.setOnMouseClicked(e->{
                if(slideShow.getSelectedSlide()!=null){
                    slideShow.getSelectedSlide().getStyleClass().remove(1);
                }
                slideShow.setSelectedSlide(slideEditor);
                slideEditor.getStyleClass().add(CSS_CLASS_SLIDE_SELECTED_VIEW);
                updateSideBarControls(false);
            });
            getSlidesEditorPane().getChildren().add(slideEditor);
            
	}                
        
    }

    /**
     * @return the fileController
     */
    public FileController getFileController() {
        return fileController;
    }

    /**
     * @return the slidesEditorPane
     */
    public VBox getSlidesEditorPane() {
        return slidesEditorPane;
    }

    /**
     * @return the ssmPane
     */
    public BorderPane getSsmPane() {
        return ssmPane;
    }

    
}
