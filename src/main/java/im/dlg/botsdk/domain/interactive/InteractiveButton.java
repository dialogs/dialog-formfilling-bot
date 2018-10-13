package im.dlg.botsdk.domain.interactive;

public class InteractiveButton implements InteractiveWidget {
    private String value;
    private String label;

    public InteractiveButton(String value, String label) {
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
