module pl.psk.nsk.reliabilityindicatorscalculator {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.psk.nsk.reliabilityindicatorscalculator to javafx.fxml;
    exports pl.psk.nsk.reliabilityindicatorscalculator;
}