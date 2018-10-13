package im.dlg.botsdk.light;

import im.dlg.botsdk.domain.Message;

@FunctionalInterface
public interface MessageListener {
    void onMessage(Message message);
}
