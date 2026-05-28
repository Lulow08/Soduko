module com.bifasico.soduko {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bifasico.soduko to javafx.fxml;
    opens com.bifasico.soduko.controller to javafx.fxml;
    opens com.bifasico.soduko.controller.cellInputHandler to javafx.fxml;
    opens com.bifasico.soduko.view to javafx.fxml;

    exports com.bifasico.soduko;
    exports com.bifasico.soduko.controller;
    exports com.bifasico.soduko.view;
    exports com.bifasico.soduko.model;

}