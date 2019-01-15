package ssm.file;

import java.lang.StringBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.scene.control.Alert;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import static ssm.StartupConstants.BASE_DIR;
import static ssm.StartupConstants.JS_BASE;
import static ssm.StartupConstants.PATH_DATA;
import static ssm.StartupConstants.PATH_SITES;
import static ssm.StartupConstants.PATH_SLIDE_SHOWS;
import ssm.model.Slide;
import ssm.model.SlideShowModel;
import static java.nio.file.StandardOpenOption.*;
import javafx.collections.ObservableList;

/**
 * This class uses the JSON standard to read and write slideshow data files.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideShowFileManager {
    // JSON FILE READING AND WRITING CONSTANTS
    public static String JSON_TITLE = "title";
    public static String JSON_SLIDES = "slides";
    public static String JSON_IMAGE_FILE_NAME = "image_file_name";
    public static String JSON_IMAGE_PATH = "image_path";
    public static String JSON_EXT = ".json";
    public static String SLASH = "/";
    public static String CAPTION="caption";

    /**
     * This method saves all the data associated with a slide show to
     * a JSON file.
     * 
     * @param slideShowToSave The course whose data we are saving.
     * 
     * @throws IOException Thrown when there are issues writing
     * to the JSON file.
     */
    public void saveSlideShow(SlideShowModel slideShowToSave) throws IOException {
        // BUILD THE FILE PATH
        String slideShowTitle = slideShowToSave.getTitle();
        String jsonFilePath = PATH_SLIDE_SHOWS + slideShowTitle + JSON_EXT;
        
        // INIT THE WRITER
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os);  
           
        // BUILD THE SLIDES ARRAY
        JsonArray slidesJsonArray = makeSlidesJsonArray(slideShowToSave.getSlides());
        
        // NOW BUILD THE COURSE USING EVERYTHING WE'VE ALREADY MADE
        JsonObject courseJsonObject = Json.createObjectBuilder()
                                    .add(JSON_TITLE, slideShowToSave.getTitle())
                                    .add(JSON_SLIDES, slidesJsonArray)
                .build();
        
        // AND SAVE EVERYTHING AT ONCE
        jsonWriter.writeObject(courseJsonObject);
        jsonWriter.close();
        os.close();
        
        File slideShowPath=new File(PATH_SITES+slideShowTitle);
        if(slideShowPath.exists()){
            try {
                Files.walkFileTree(slideShowPath.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException
                    {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e)
                            throws IOException
                    {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            // directory iteration failed
                            throw e;
                        }
                    }
                });    } catch (IOException ex) {
                Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Failed when cleaning slideshow site,"+
                        "Try again.");
                errorAlert.showAndWait();
            }
        }

            try {
                Path baseDir=Paths.get(PATH_DATA+BASE_DIR);
                Path baseData=Paths.get(PATH_DATA);
                Path target=Paths.get(PATH_SITES);
                Files.createDirectories(target);
                Files.walkFileTree(baseDir,EnumSet.noneOf(FileVisitOption.class),2,
                        new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                                    throws IOException
                            {
                                Path targetdir = target.resolve(baseData.relativize(dir));
                                Files.copy(dir, targetdir);
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                    throws IOException
                            {
                                Files.copy(file, target.resolve(baseData.relativize(file)));
                                return FileVisitResult.CONTINUE;
                            }
                        });
                Files.move(Paths.get(PATH_SITES+BASE_DIR), slideShowPath.toPath());
            } catch (IOException ex) {
                Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Failed when creating slide show directory,"+
                        "Try again.");
                errorAlert.showAndWait();
                return;
            }
            //  Create a new JSON object to javascript file
                 //   Change the image path with relative to the HTML directory for portability             
            JsonArrayBuilder jsb = Json.createArrayBuilder();
            for (Slide slide : slideShowToSave.getSlides()) {
	    JsonObject jso = Json.createObjectBuilder()
		.add(JSON_IMAGE_FILE_NAME, slide.getImageFileName())
		.add(JSON_IMAGE_PATH, "img")
                .add(CAPTION, slide.getCaption())
		.build();
	    jsb.add(jso);
            }
            slidesJsonArray = jsb.build();
            courseJsonObject = Json.createObjectBuilder()
                                    .add(JSON_TITLE, slideShowToSave.getTitle())
                                    .add(JSON_SLIDES, slidesJsonArray)
                .build();

            // Now write the JSON object to javascript file
            try{
                // copy the original js file first
            Path tempJSFile = Files.createTempFile("SlideShowMaker", "");
            Files.copy(slideShowPath.toPath().resolve(JS_BASE), tempJSFile, StandardCopyOption.REPLACE_EXISTING);
            BufferedWriter newBufferedWriter = Files.newBufferedWriter(slideShowPath.toPath().resolve(JS_BASE), Charset.forName("UTF-8"));
            newBufferedWriter.write("var json=");
            jsonWriter = Json.createWriter(newBufferedWriter);
            jsonWriter.writeObject(courseJsonObject);
            newBufferedWriter.newLine();
            jsonWriter.close();
            newBufferedWriter.close();
            // Write back the original fs file
            SeekableByteChannel inChannel = Files.newByteChannel(tempJSFile, EnumSet.of(READ));
            SeekableByteChannel outChannel = Files.newByteChannel(slideShowPath.toPath().resolve(JS_BASE), EnumSet.of(APPEND));
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            while (inChannel.read(buffer) != -1) {
                buffer.flip();

                while(buffer.hasRemaining()){
                    outChannel.write(buffer);
                }

                buffer.clear();
            }
            inChannel.close();
            outChannel.close();
            tempJSFile.toFile().delete();
            }
            catch (IOException e) {
                        Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Failed when save javascript file,"+
                        "Try again.");
                errorAlert.showAndWait();
                return;
            }
            // Now copy the images from local to website directory
            try{
            ObservableList<Slide> slides = slideShowToSave.getSlides();
            
            for(Slide slide: slides){
            	String newFileName=slide.getImageFileName();
            	Path imgPath=slideShowPath.toPath().resolve("img").resolve(newFileName);
                Path imagePath=Paths.get(slide.getImagePath()).resolve(slide.getImageFileName());
                while (imgPath.toFile().exists()) {
                	StringBuilder oldFileName=new StringBuilder(newFileName);                	
                	oldFileName.insert(slide.getImageFileName().indexOf('.'), "_copy");
                	newFileName=oldFileName.toString();
                	imgPath=slideShowPath.toPath().resolve("img").resolve(newFileName);
                }
                Files.copy(imagePath, imgPath);
            }
            }
            catch (IOException e) {
                        Alert errorAlert=new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Failed when copy images to website folder,"+
                        "Try again.");
                errorAlert.showAndWait();
                return;
            }
        
    }
    
    /**
     * This method loads the contents of a JSON file representing a slide show
     * into a SlideSShowModel objecct.
     * 
     * @param slideShowToLoad The slide show to load
     * @param jsonFilePath The JSON file to load.
     * @throws IOException 
     */
    public void loadSlideShow(SlideShowModel slideShowToLoad, String jsonFilePath) throws IOException {
        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(jsonFilePath);
        
        // NOW LOAD THE COURSE
	slideShowToLoad.reset();
        slideShowToLoad.setTitle(json.getString(JSON_TITLE));
        JsonArray jsonSlidesArray = json.getJsonArray(JSON_SLIDES);
        for (int i = 0; i < jsonSlidesArray.size(); i++) {
	    JsonObject slideJso = jsonSlidesArray.getJsonObject(i);
	    slideShowToLoad.addSlide(	slideJso.getString(JSON_IMAGE_FILE_NAME),
					slideJso.getString(JSON_IMAGE_PATH),slideJso.getString(CAPTION));
	}
        
    }

    // AND HERE ARE THE PRIVATE HELPER METHODS TO HELP THE PUBLIC ONES
    
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }    
    
    private ArrayList<String> loadArrayFromJSONFile(String jsonFilePath, String arrayName) throws IOException {
        JsonObject json = loadJSONFile(jsonFilePath);
        ArrayList<String> items = new ArrayList();
        JsonArray jsonArray = json.getJsonArray(arrayName);
        for (JsonValue jsV : jsonArray) {
            items.add(jsV.toString());
        }
        return items;
    }
    
    private JsonArray makeSlidesJsonArray(List<Slide> slides) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Slide slide : slides) {
	    JsonObject jso = makeSlideJsonObject(slide);
	    jsb.add(jso);
        }
        JsonArray jA = jsb.build();
        return jA;        
    }
    
    private JsonObject makeSlideJsonObject(Slide slide) {
        JsonObject jso = Json.createObjectBuilder()
		.add(JSON_IMAGE_FILE_NAME, slide.getImageFileName())
		.add(JSON_IMAGE_PATH, slide.getImagePath())
                .add(CAPTION, slide.getCaption())
		.build();
	return jso;
    }
}
