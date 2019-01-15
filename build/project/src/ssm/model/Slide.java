package ssm.model;

import java.io.File;
import java.net.URL;
import javafx.scene.image.Image;
import static ssm.file.SlideShowFileManager.SLASH;
import ssm.view.SlideEditView;

/**
 * This class represents a single slide in a slide show.
 * 
 * @author McKilla Gorilla & _____________
 */
public class Slide {
    String imageFileName;
    String imagePath;
    SlideEditView slideEditor; 
    private String caption;
    /**
     * Constructor, it initializes all slide data.
     * @param initImageFileName File name of the image.
     * 
     * @param initImagePath File path for the image.
     * 
     */
    public Slide(String initImageFileName, String initImagePath) {
	imageFileName = initImageFileName;
	imagePath = initImagePath;
	caption="";
    }
    public Slide(String initImageFileName, String initImagePath,String caption) {
	imageFileName = initImageFileName;
	imagePath = initImagePath;
        this.caption=caption;
    }
    
    // ACCESSOR METHODS
    public String getImageFileName() { return imageFileName; }
    public String getImagePath() { return imagePath; }
    
    // MUTATOR METHODS
    public void setImageFileName(String initImageFileName) {
	imageFileName = initImageFileName;
    }
    
    public void setImagePath(String initImagePath) {
	imagePath = initImagePath;
    }
    
    public Image getImage(){
        String imagePath = getImagePath() + SLASH + getImageFileName();
        Image slideImage=null;
	File file = new File(imagePath);
	try {
	    URL fileURL = file.toURI().toURL();
	    slideImage = new Image(fileURL.toExternalForm());
            } catch(Exception e){
               }
        return slideImage;
}
    public void setImage(String initPath, String initFileName) {
	imagePath = initPath;
	imageFileName = initFileName;
    }

    public void setSlideEditView(SlideEditView slideEditor){
        this.slideEditor=slideEditor;
    }
    
    public SlideEditView getSlideEditView(){
        return slideEditor;
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
   
}
