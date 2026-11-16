package pl.psk.nsk.reliabilityindicatorscalculator.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.psk.nsk.reliabilityindicatorscalculator.model.Field;
import pl.psk.nsk.reliabilityindicatorscalculator.model.InputValidator;
import pl.psk.nsk.reliabilityindicatorscalculator.model.StructureType;
import pl.psk.nsk.reliabilityindicatorscalculator.model.ValidationResult;
import pl.psk.nsk.reliabilityindicatorscalculator.model.InputData;

import java.util.EnumMap;
import java.util.Map;


public class MainView {

    private static final String ERROR_STYLE_CLASS = "field-error";
    private static final String EMPTY_RESULT = "—";

    private final InputValidator validator = new InputValidator();

    private final ToggleGroup structureGroup = new ToggleGroup();
    private final Map<StructureType, RadioButton> structureButtons = new EnumMap<>(StructureType.class);
    private final Label structureDescription = new Label();

    private final TextField nField = numberField("np. 5");
    private final TextField kField = numberField("np. 3");
    private final TextField timeField = numberField("np. 500");
    private final TextField lambdaField = numberField("np. 0,002 lub 2e-3");

    private final Map<Field, TextField> inputs = new EnumMap<>(Field.class);
    private final Map<Field, Label> errorLabels = new EnumMap<>(Field.class);
    private Label kLabel;

    private final Label reliabilityValue = resultValue();
    private final Label unreliabilityValue = resultValue();
    private final Label meanTimeValue = resultValue();
    private final Label statusLabel = new Label();

    private final VBox root;

    public MainView() {
        inputs.put(Field.N, nField);
        inputs.put(Field.K, kField);
        inputs.put(Field.TIME, timeField);
        inputs.put(Field.LAMBDA, lambdaField);

        Label title = new Label("Wskaźniki niezawodności obiektów nienaprawialnych");
        title.getStyleClass().add("title");
        Label subtitle = new Label("R(t), F(t) i ET dla struktury szeregowej, równoległej i k-z-N");
        subtitle.getStyleClass().add("subtitle");

        root = new VBox(14,
                new VBox(2, title, subtitle),
                section("1. Rodzaj struktury", buildStructurePane()),
                section("2. Dane wejściowe", buildInputPane()),
                buildButtons(),
                section("3. Wyniki", buildResultsPane()));
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-pane");

        structureGroup.selectToggle(structureButtons.get(StructureType.SZEREGOWA));
        onStructureChanged(StructureType.SZEREGOWA);
    }

    public Parent getRoot() {
        return root;
    }


    private Parent buildStructurePane() {
        HBox buttons = new HBox(20);
        for (StructureType type : StructureType.values()) {
            RadioButton button = new RadioButton(type.getDisplayName());
            button.setUserData(type);
            button.setToggleGroup(structureGroup);
            structureButtons.put(type, button);
            buttons.getChildren().add(button);
        }
        structureGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                onStructureChanged((StructureType) newToggle.getUserData());
            }
        });

        structureDescription.setWrapText(true);
        structureDescription.getStyleClass().add("hint");
        return new VBox(8, buttons, structureDescription);
    }

    private Parent buildInputPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(2);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(260);
        ColumnConstraints fieldColumn = new ColumnConstraints(180);
        ColumnConstraints unitColumn = new ColumnConstraints();
        unitColumn.setMinWidth(40);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn, unitColumn);

        int row = 0;
        row = addInputRow(grid, row, "Liczba elementów w strukturze N:", Field.N, "szt.");
        kLabel = new Label("Liczba elementów, które muszą być sprawne k:");
        row = addInputRow(grid, row, kLabel, Field.K, "szt.");
        row = addInputRow(grid, row, "Chwila czasu t:", Field.TIME, "h");
        addInputRow(grid, row, "Intensywność uszkodzeń elementu λ:", Field.LAMBDA, "1/h");

        Label note = new Label("Przyjęto jednakowe elementy o stałej intensywności uszkodzeń λ. "
                + "Można wpisać przecinek dziesiętny lub zapis wykładniczy (2e-3 = 0,002).");
        note.setWrapText(true);
        note.getStyleClass().add("hint");

        return new VBox(8, grid, note);
    }

    private int addInputRow(GridPane grid, int row, String labelText, Field field, String unit) {
        return addInputRow(grid, row, new Label(labelText), field, unit);
    }

    private int addInputRow(GridPane grid, int row, Label label, Field field, String unit) {
        TextField input = inputs.get(field);
        Label error = new Label();
        error.getStyleClass().add("error-text");
        error.setManaged(false);
        error.setVisible(false);
        errorLabels.put(field, error);

        input.textProperty().addListener((obs, oldText, newText) -> clearError(field));
        input.setOnAction(e -> onCalculate());

        grid.add(label, 0, row);
        grid.add(input, 1, row);
        grid.add(new Label(unit), 2, row);
        grid.add(error, 1, row + 1, 2, 1);
        return row + 2;
    }

    private Parent buildButtons() {
        Button calculate = new Button("Oblicz");
        calculate.setDefaultButton(true);
        calculate.getStyleClass().add("primary");
        calculate.setOnAction(e -> onCalculate());

        Button example = new Button("Wstaw przykład");
        example.setOnAction(e -> fillExample());

        Button clear = new Button("Wyczyść");
        clear.setOnAction(e -> clearAll());

        HBox box = new HBox(10, calculate, example, clear);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Parent buildResultsPane() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(6);
        grid.addRow(0, new Label("Funkcja niezawodności R(t):"), reliabilityValue);
        grid.addRow(1, new Label("Funkcja zawodności F(t) = 1 − R(t):"), unreliabilityValue);
        grid.addRow(2, new Label("Średni czas poprawnej pracy ET:"), meanTimeValue, new Label("h"));

        statusLabel.setWrapText(true);
        statusLabel.getStyleClass().add("hint");
        return new VBox(10, grid, statusLabel);
    }


    private void onStructureChanged(StructureType type) {
        boolean needsK = type.requiresK();
        kField.setDisable(!needsK);
        kLabel.setDisable(!needsK);
        if (!needsK) {
            kField.clear();
            clearError(Field.K);
        }

        structureDescription.setText(switch (type) {
            case SZEREGOWA -> "Struktura jest sprawna tylko wtedy, gdy sprawne są wszystkie N elementów.";
            case ROWNOLEGLA -> "Struktura jest sprawna, dopóki sprawny jest co najmniej jeden z N elementów.";
            case K_Z_N -> "Struktura jest sprawna, gdy sprawnych jest co najmniej k spośród N elementów.";
        });
        resetResults("Wprowadź dane i naciśnij „Oblicz”.");
    }

    private void onCalculate() {
        clearAllErrors();
        ValidationResult result = validator.validate(
                selectedStructure(),
                nField.getText(),
                kField.getText(),
                timeField.getText(),
                lambdaField.getText());

        if (!result.isValid()) {
            result.getErrors().forEach(this::showError);
            resetResults("Popraw zaznaczone pola.");
            focusFirstError(result);
            return;
        }

        InputData data = result.getData().orElseThrow();
        // TODO obliczenia R(t), F(t), ET w klasie ReliabilityCalculator
        resetResults("Dane poprawne: " + describe(data)
                + ". Obliczenia zostaną dodane w kolejnym etapie.");
    }

    private void fillExample() {
        nField.setText("5");
        if (selectedStructure() == StructureType.K_Z_N) {
            kField.setText("3");
        }
        timeField.setText("500");
        lambdaField.setText("2e-3");
    }

    private void clearAll() {
        inputs.values().forEach(TextField::clear);
        clearAllErrors();
        resetResults("Wprowadź dane i naciśnij „Oblicz”.");
    }


    private StructureType selectedStructure() {
        return structureGroup.getSelectedToggle() == null
                ? null
                : (StructureType) structureGroup.getSelectedToggle().getUserData();
    }

    private void showError(Field field, String message) {
        Label error = errorLabels.get(field);
        TextField input = inputs.get(field);
        if (error == null || input == null) {
            statusLabel.setText(message);
            return;
        }
        error.setText(message);
        error.setManaged(true);
        error.setVisible(true);
        if (!input.getStyleClass().contains(ERROR_STYLE_CLASS)) {
            input.getStyleClass().add(ERROR_STYLE_CLASS);
        }
    }

    private void clearError(Field field) {
        Label error = errorLabels.get(field);
        if (error != null) {
            error.setText("");
            error.setManaged(false);
            error.setVisible(false);
        }
        TextField input = inputs.get(field);
        if (input != null) {
            input.getStyleClass().remove(ERROR_STYLE_CLASS);
        }
    }

    private void clearAllErrors() {
        for (Field field : Field.values()) {
            clearError(field);
        }
    }

    private void focusFirstError(ValidationResult result) {
        for (Field field : Field.values()) {
            if (result.errorFor(field).isPresent() && inputs.containsKey(field)) {
                inputs.get(field).requestFocus();
                return;
            }
        }
    }

    private void resetResults(String status) {
        reliabilityValue.setText(EMPTY_RESULT);
        unreliabilityValue.setText(EMPTY_RESULT);
        meanTimeValue.setText(EMPTY_RESULT);
        statusLabel.setText(status);
    }

    private static String describe(InputData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("struktura ").append(data.structure().getDisplayName().toLowerCase())
                .append(", N = ").append(data.n());
        if (data.structure().requiresK()) {
            sb.append(", k = ").append(data.k());
        }
        sb.append(", t = ").append(data.time()).append(" h")
                .append(", λ = ").append(data.lambda()).append(" 1/h");
        return sb.toString();
    }

    private static TextField numberField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private static Label resultValue() {
        Label label = new Label(EMPTY_RESULT);
        label.getStyleClass().add("result-value");
        label.setMinWidth(140);
        return label;
    }

    private static TitledPane section(String title, Parent content) {
        TitledPane pane = new TitledPane(title, content);
        pane.setCollapsible(false);
        VBox.setVgrow(pane, Priority.NEVER);
        return pane;
    }
}
