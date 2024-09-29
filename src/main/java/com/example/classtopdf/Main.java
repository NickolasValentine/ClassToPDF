package com.example.classtopdf;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class Main extends Application {
    private Set<Class<?>> processedClasses = new HashSet<>();
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Class Documentor");

        Label label = new Label("Enter class name:"); // Create UI elements
        TextField classNameInput = new TextField();
        Button generateButton = new Button("Generate PDF");

        // Button click handler
        generateButton.setOnAction(event -> {
            String className = classNameInput.getText();
            try {
                Class<?> clazz = Class.forName(className);
                generateClassDocumentation(clazz, "ClassDocumentation.pdf");
                System.out.println("PDF generated successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + className);
            } catch (FileNotFoundException e) {
                System.err.println("Error writing PDF: " + e.getMessage());
            }
        });

        VBox vbox = new VBox(10, label, classNameInput, generateButton);// Layout
        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setScene(scene);// Set the scene
        primaryStage.show();
    }
    // Recursive method to generate class documentation
    private void generateClassDocumentation(Class<?> clazz, String outputFile) throws FileNotFoundException {
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(outputFile));
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            // Start with the main class
            document.add(new Paragraph("Class: " + clazz.getName()));
            processClass(clazz, document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // Recursive helper method to process each class and its fields
    private void processClass(Class<?> clazz, Document document) {
        // Check if the class has already been processed to avoid loops
        if (processedClasses.contains(clazz)) {
            return;
        }

        processedClasses.add(clazz);
        document.add(new Paragraph("\nClass: " + clazz.getName()));// Add class name to the document
        document.add(new Paragraph("Fields:"));

        // Get all fields of the class
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            document.add(new Paragraph(" - " + field.getName() + ": " + field.getType().getName())); // Add field name and type
            // Process fields that are not primitive or part of standard Java library (excluding arrays and strings)
            Class<?> fieldType = field.getType();
            if (!fieldType.isPrimitive() && !fieldType.getName().startsWith("java.lang")) {
                if (fieldType.isArray()) {
                    // If it's an array, process its component type
                    processClass(fieldType.getComponentType(), document);
                } else if (Iterable.class.isAssignableFrom(fieldType)) {
                    // If it's a collection, we can't know the exact type of elements at runtime,
                    // but we could document that it's a collection.
                    document.add(new Paragraph(" (This is a collection, individual elements not shown.)"));
                } else {
                    processClass(fieldType, document); // Recursively process the field class
                }
            }
        }
        document.add(new Paragraph("Methods:")); // Add methods
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String methodSignature = getMethodSignature(method);
            document.add(new Paragraph(" - " + methodSignature));
        }
        // Add a line break after processing each class to avoid confusion between class structures
        document.add(new Paragraph("\n"));
    }
    // Helper method to get the method signature (access level, return type, name)
    private String getMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        int modifiers = method.getModifiers(); // Get access modifier
        if (Modifier.isPublic(modifiers)) {
            signature.append("public ");
        } else if (Modifier.isProtected(modifiers)) {
            signature.append("protected ");
        } else if (Modifier.isPrivate(modifiers)) {
            signature.append("private ");
        } else {
            signature.append("default ");
        }
        signature.append(method.getReturnType().getSimpleName()).append(" ");// Append return type
        signature.append(method.getName()).append("()");// Append method name
        return signature.toString();
    }
}