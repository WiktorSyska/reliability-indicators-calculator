package pl.psk.nsk.reliabilityindicatorscalculator.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;


public final class ValidationResult {

    private final InputData data;
    private final Map<Field, String> errors;

    private ValidationResult(InputData data, Map<Field, String> errors) {
        this.data = data;
        this.errors = errors;
    }

    static ValidationResult valid(InputData data) {
        return new ValidationResult(data, Collections.emptyMap());
    }

    static ValidationResult invalid(Map<Field, String> errors) {
        return new ValidationResult(null, Collections.unmodifiableMap(new EnumMap<>(errors)));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Optional<InputData> getData() {
        return Optional.ofNullable(data);
    }

    public Map<Field, String> getErrors() {
        return errors;
    }

    public Optional<String> errorFor(Field field) {
        return Optional.ofNullable(errors.get(field));
    }
}
