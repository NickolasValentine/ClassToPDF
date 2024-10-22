package com.example.classtopdf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;


public class DocumentatorApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ClassToPDF");

        Label classnamelabel = new Label("Enter class name:"); // Create UI elements
        TextField classNameInput = new TextField();

        Label filenamelabel = new Label("Enter file name:"); // Create UI elements
        TextField filenameInput = new TextField();

        Button generateButton = new Button("Generate PDF");

        // Button click handler
        generateButton.setOnAction(event -> {
            String className = classNameInput.getText();
            try {
                Class<?> clazz = Class.forName(className);
                String fileName = filenameInput.getText();
                //generateClassDocumentation(clazz, "ClassDocumentation.pdf");
                Documentator doc = new Documentator(clazz, fileName);
                System.out.println("PDF generated successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + className);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        VBox vbox = new VBox(10, classnamelabel, classNameInput, filenamelabel, filenameInput, generateButton);// Layout
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icons/icon.png")));
        primaryStage.setScene(scene);// Set the scene
        primaryStage.show();
    }
}