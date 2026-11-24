package control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application {
	private static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainApp.primaryStage = primaryStage;
		launchLogin();
	}

	public static void launchLogin() {
		if (primaryStage == null) {
			System.out.println("❌ JavaFX not initialized yet!");
			return;
		}

		javafx.application.Platform.runLater(() -> { // ✅ Run on JavaFX Thread
			try {
				FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/main.fxml"));
				Pane root = loader.load();
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.setTitle("Haifa Court System - Login");

				// Get the MainController instance
				MainController controller = loader.getController();

				// Stop the media player when closing the application
				primaryStage.setOnCloseRequest(event -> {
//					controller.stopMedia();
				});

				primaryStage.show();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("❌ Error loading login screen.");
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
