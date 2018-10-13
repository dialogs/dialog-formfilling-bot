package im.dlg.botsdk.domain;

import java.util.UUID;

public class InteractiveEvent {

    private UUID mid;
    private String id;
    private String value;
    private Peer peer;

    public InteractiveEvent(UUID mid, String id, String value, Peer peer) {
        this.mid = mid;
        this.id = id;
        this.value = value;
        this.peer = peer;
    }

    public UUID getMid() {
        return mid;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Peer getPeer() {
        return peer;
    }

    @Override
    public String toString() {
        return "InteractiveEvent{" +
                "mid=" + mid +
                ", id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", peer=" + peer +
                '}';
    }
}
