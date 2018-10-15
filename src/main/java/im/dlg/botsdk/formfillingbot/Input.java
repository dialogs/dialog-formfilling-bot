package im.dlg.botsdk.formfillingbot;

import im.dlg.botsdk.Bot;
import im.dlg.botsdk.domain.Peer;
import im.dlg.botsdk.domain.interactive.InteractiveGroup;

import java.text.SimpleDateFormat;

public class Input {
    private String message;
    private InteractiveGroup interactiveGroup;
    private String actionName;
    private int type;
    private SimpleDateFormat simpleDateFormat;

    public final static int INPUT_STRING = 1;
    public final static int INPUT_INTERACTIVE = 2;
    public final static int INPUT_DATE = 3;
    public final static int INPUT_USERNAME = 4;

    public Input(String message) {
        this.message = message;
        this.type = 1;
    }

    public Input(InteractiveGroup interactiveGroup, String actionName) {
        this.interactiveGroup = interactiveGroup;
        this.actionName = actionName;
        this.type = 2;
    }

    public Input(int type, String format) {
        this.type = type;
        this.simpleDateFormat = new SimpleDateFormat(format);
    }

    public Input(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public InteractiveGroup getInteractiveGroup() {
        return interactiveGroup;
    }

    public int getType() {
        return type;
    }

    public String getActionName() {
        return actionName;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public void sendInput(Bot bot, Peer peer) {
        if (this.getType() == Input.INPUT_STRING) {
            bot.messaging().send(peer, this.getMessage());
        }
        else if (this.getType() == Input.INPUT_INTERACTIVE) {
            bot.interactiveApi().send(peer, this.getInteractiveGroup());
        }
    }
}
