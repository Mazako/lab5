package simulation;

public enum SimulationTypes {
    ONLY_ONE("Tylko jeden bus na moście");

    private final String description;

    SimulationTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
