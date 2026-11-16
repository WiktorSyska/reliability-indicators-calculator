package pl.psk.nsk.reliabilityindicatorscalculator.model;

import java.util.EnumMap;
import java.util.Map;


public final class InputValidator {

    public static final int MAX_N = 1000;

    public ValidationResult validate(StructureType structure,
                                     String nText,
                                     String kText,
                                     String timeText,
                                     String lambdaText) {

        Map<Field, String> errors = new EnumMap<>(Field.class);

        if (structure == null) {
            errors.put(Field.STRUCTURE, "Wybierz rodzaj struktury.");
        }

        Integer n = parseInteger(nText, Field.N, "N", errors);
        if (n != null) {
            if (n < 1) {
                errors.put(Field.N, "N musi być większe lub równe 1.");
            } else if (n > MAX_N) {
                errors.put(Field.N, "N nie może przekraczać " + MAX_N + ".");
            }
        }

        Integer k = null;
        if (structure != null && structure.requiresK()) {
            k = parseInteger(kText, Field.K, "k", errors);
            if (k != null) {
                if (k < 1) {
                    errors.put(Field.K, "k musi być większe lub równe 1.");
                } else if (n != null && !errors.containsKey(Field.N) && k > n) {
                    errors.put(Field.K, "k nie może być większe od N (" + n + ").");
                }
            }
        }

        Double time = parseDouble(timeText, Field.TIME, "t", errors);
        if (time != null && time < 0) {
            errors.put(Field.TIME, "Czas t nie może być ujemny.");
        }

        Double lambda = parseDouble(lambdaText, Field.LAMBDA, "λ", errors);
        if (lambda != null && lambda <= 0) {
            errors.put(Field.LAMBDA, "λ musi być większa od zera.");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.invalid(errors);
        }

        int effectiveK = switch (structure) {
            case SZEREGOWA -> n;
            case ROWNOLEGLA -> 1;
            case K_Z_N -> k;
        };

        return ValidationResult.valid(new InputData(structure, n, effectiveK, time, lambda));
    }

    private static Integer parseInteger(String text, Field field, String name, Map<Field, String> errors) {
        String value = normalize(text);
        if (value.isEmpty()) {
            errors.put(field, "Podaj " + name + ".");
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            errors.put(field, name + " musi być liczbą całkowitą.");
            return null;
        }
    }

    private static Double parseDouble(String text, Field field, String name, Map<Field, String> errors) {
        String value = normalize(text).replace(',', '.');
        if (value.isEmpty()) {
            errors.put(field, "Podaj " + name + ".");
            return null;
        }
        try {
            double parsed = Double.parseDouble(value);
            if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
                errors.put(field, name + " musi być skończoną liczbą.");
                return null;
            }
            return parsed;
        } catch (NumberFormatException e) {
            errors.put(field, name + " musi być liczbą (np. 0,002 lub 2e-3).");
            return null;
        }
    }

    private static String normalize(String text) {
        return text == null ? "" : text.trim().replace(" ", "");
    }
}
