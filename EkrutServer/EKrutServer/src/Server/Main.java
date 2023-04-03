package Server;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


/**
 * This class extend the javaFx application class to start our javaFX application
 */
public class Main extends Application {
	/**
	 * This method start the primary stage to show in the window
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("ServerScreen.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("generalDesign.css").toExternalForm());
			primaryStage.setScene(scene);
	
				Image iconImage = new Image("/EKRUT.jpeg");
				primaryStage.getIcons().add(iconImage);
				primaryStage.setScene(scene);
				primaryStage.show();
		
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * main that launches the application
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
