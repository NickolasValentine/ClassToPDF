module com.example.classtopdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires kernel;
    requires layout;


    opens com.example.classtopdf to javafx.fxml;
    exports com.example.classtopdf;
}