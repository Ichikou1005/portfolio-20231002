module com.example.java_ml {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.java_ml to javafx.fxml;
    exports com.example.java_ml;
}