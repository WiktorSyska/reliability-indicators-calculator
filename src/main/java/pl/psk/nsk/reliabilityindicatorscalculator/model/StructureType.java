package pl.psk.nsk.reliabilityindicatorscalculator.model;


public enum StructureType {

    SZEREGOWA("Szeregowa", false),
    ROWNOLEGLA("Równoległa", false),
    K_Z_N("k-z-N", true);

    private final String displayName;
    private final boolean requiresK;

    StructureType(String displayName, boolean requiresK) {
        this.displayName = displayName;
        this.requiresK = requiresK;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean requiresK() {
        return requiresK;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
