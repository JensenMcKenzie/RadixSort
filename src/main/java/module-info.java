module com.example.radixsort {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.radixsort to javafx.fxml;
    exports com.example.radixsort;
}