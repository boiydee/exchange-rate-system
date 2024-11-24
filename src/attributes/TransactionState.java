package attributes;

public enum TransactionState {
    CANCELLED("CANCELLED"),
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED");

    private String description;

    TransactionState(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
