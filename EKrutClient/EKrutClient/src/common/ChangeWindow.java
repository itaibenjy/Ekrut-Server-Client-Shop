package common;

import java.io.IOException;
import Client.EkrutClientUI;
import clientGUIControllers.PopUpController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class main purpose is to display windows, changeScreen display a regular scene
 * usually called after hide of the previous scene, and showPopup to display a pop up window
 * with the wanted information.
 */
public class ChangeWindow {

	/**
	 * This method used to open and display a new scene using an FXML file , apply 
	 * the project CSS to it and make it movable by dragging.
	 * @param primaryStage the stage we want to open the FXML on
	 * @param fxmlPath the path to the FXML file
	 */
	public void changeScreen(Stage primaryStage, String fxmlPath) {
		  try {
			 Parent root = (Parent)FXMLLoader.load(this.getClass().getResource(fxmlPath));
			 Scene scene = new Scene(root);
			 Image icon = new Image(this.getClass().getResourceAsStream("/ClientEkrut.jpeg"));
			 primaryStage.getIcons().add(icon);
			 scene.getStylesheets().add(getClass().getResource("/stylesSheets/generalDesign.css").toExternalForm());
			 primaryStage.setScene(scene);
			 primaryStage.initStyle(StageStyle.UNDECORATED);
			 this.makeUndecoratedScreenMovable(root, primaryStage);
			 primaryStage.show();
		  } catch (IOException e) {
			  e.printStackTrace();
			  EkrutClientUI.chat.display("Error while loading the new FXML screen, path = " + fxmlPath + " .");
		  }

	   }


	/**
	 * This method makes the screen movable by holding and dragging the mouse
	 * @param root the parent instance of the screen
	 * @param primaryStage the stage which we want to make movable
	 */
	public void makeUndecoratedScreenMovable(Parent root, Stage primaryStage) {
		  root.setOnMousePressed((pressEvent) -> {
			 root.setOnMouseDragged((dragEvent) -> {
				primaryStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
				primaryStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
			 });
		  });
	   }
   
   /**
	 * This method uses the showPopUp method of class ChangeWindow to display a pop up window on screen,
	 * it also can transfer user to a new screen when pressing OK or X on the pop screen.
	 * @param primaryStage the stage for the new pop up screen
	 * @param popUpTitle the title of the pop up screen
	 * @param popUpMessage the message shown in the pop up screen
	 * @param imagePath the image to show in the pop up screen (null if don't want to show image)
	 * @param linkToString the FXML path to a new screen if the wanted action when pressed OK or X on pop up is to transfer user to a new screen (null if want to stay on same screen)
	 * @param eventToClose the ActionEvent from the current screen to close it before transferring to a new screen (null if want to stay on same screen) 
	 */
   public void showPopUp(Stage primaryStage, String popUpTitle,String popUpMessage, String imagePath, String linkToString,ActionEvent eventToClose) {
		try {
		// Create a new stage
		Stage popUp = new Stage();
		popUp.initStyle(StageStyle.UNDECORATED);
		popUp.initStyle(StageStyle.TRANSPARENT);

	
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation((getClass().getResource("/clientGUIScreens/PopUpScreen.fxml")));
		AnchorPane anchorPane = (AnchorPane)fxmlLoader.load();

		//Parent root=(Parent)FXMLLoader.load(this.getClass().getResource(fxmlPath)); 
		Parent root=(Parent)anchorPane;

		PopUpController popUpController=(PopUpController) fxmlLoader.getController();;
		popUpController.setTitleAndMessage(popUpTitle,popUpMessage);
		/**
		 * if we want to press OK close the correct window and move to other window:
		 * we need to get linkToString and the event that we want to hide
		 * else: linkToString -> null && eventToClose -> null
		 */
		try {
			if (linkToString != null ) {
				popUpController.setLinkToScreen(linkToString);
				popUpController.setEventToClose(eventToClose);
				
			}
		} catch (Exception e) {
			EkrutClientUI.chat.display("When using the pop up screen and wanting to transfer window on "
					+ "click assign values to linkToString and eventToClose otherwise both should equal null.");
		}
		
		if(imagePath != null) {
			 Image image = new Image(imagePath);
			 popUpController.seImageView(image);
		}
		
		Scene popUpScene = new Scene(anchorPane);
		popUp.setAlwaysOnTop(true);
		popUpScene.setFill(Color.TRANSPARENT);
		popUpScene.getStylesheets().add(getClass().getResource("/stylesSheets/generalDesign.css").toExternalForm());

		popUp.setScene(popUpScene);
		this.makeUndecoratedScreenMovable(root, popUp);
		popUp.show();
		popUp.toFront();
	    
		} catch (IOException e) {
			EkrutClientUI.chat.display("Error while loading the FXML of the pop up screen.");
		}
 	}


}