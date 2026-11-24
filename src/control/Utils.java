package control;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
//Utils.java
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Utils {

	// Show Alert Dialog
	public static void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Switch Scenes
	public static void switchScene(MouseEvent event, String fxmlFile, String title) {
		try {
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close(); // Close current stage

			FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle(title);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static java.sql.Date parseDate(String string) {
		try {
			Date utilDate = (Date) new SimpleDateFormat("dd/MM/yyyy").parse(string);
			return new java.sql.Date(utilDate.getTime()); // ✅ Convert to java.sql.Date
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String formatDate(Date date) {
		if (date == null)
			return "N/A";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

}
