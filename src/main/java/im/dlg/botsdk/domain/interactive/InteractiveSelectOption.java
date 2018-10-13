package im.dlg.botsdk.domain.interactive;

public class InteractiveSelectOption {
    private String value;
    private String label;

    public InteractiveSelectOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
