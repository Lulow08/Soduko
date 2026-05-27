module com.bifasico.soduko {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bifasico.soduko to javafx.fxml;
    opens com.bifasico.soduko.controller to javafx.fxml;
    exports com.bifasico.soduko;
}