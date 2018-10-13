package im.dlg.botsdk.domain;

public class Peer {

    private int id;
    private PeerType type;
    private long accessHash;

    public Peer(int id, PeerType type, long accessHash) {
        this.id = id;
        this.type = type;
        this.accessHash = accessHash;
    }

    public int getId() {
        return id;
    }

    public PeerType getType() {
        return type;
    }

    public long getAccessHash() {
        return accessHash;
    }

    public enum PeerType {
        PRIVATE,
        GROUP,
        SIP,
        UNKNOWN
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id=" + id +
                ", type=" + type +
                ", accessHash=" + accessHash +
                '}';
    }
}
