package im.dlg.botsdk.domain;

import java.util.UUID;

public class Message {

    private Peer peer;
    private Peer sender;
    private UUID messageId;
    private String text;
    private long date;

    public Message(Peer peer, Peer sender, UUID messageId, String text, long date) {
        this.peer = peer;
        this.sender = sender;
        this.messageId = messageId;
        this.text = text;
        this.date = date;
    }

    public Peer getPeer() {
        return peer;
    }

    public Peer getSender() {
        return sender;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getText() {
        return text;
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "peer=" + peer +
                ", sender=" + sender +
                ", messageId=" + messageId +
                ", text='" + text + '\'' +
                '}';
    }
}
