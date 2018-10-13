package me.blueat.dialog.bot;

import im.dlg.botsdk.Bot;
import im.dlg.botsdk.domain.Peer;
import im.dlg.botsdk.domain.interactive.InteractiveGroup;

public class Order {
    private String message;
    private InteractiveGroup interactiveGroup;
    private String actionName;
    private int type;

    public final static int ORDER_INPUT = 1;
    public final static int ORDER_INTERACTIVE = 2;

    public Order(String message) {
        this.message = message;
        this.type = 1;
    }

    public Order(InteractiveGroup interactiveGroup, String actionName) {
        this.interactiveGroup = interactiveGroup;
        this.actionName = actionName;
        this.type = 2;
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

    public void sendOrder(Bot bot, Peer peer) {
        if (this.getType() == Order.ORDER_INPUT) {
            bot.messaging().send(peer, this.getMessage());
        }
        else if (this.getType() == Order.ORDER_INTERACTIVE) {
            bot.interactiveApi().send(peer, this.getInteractiveGroup());
        }
    }
}
