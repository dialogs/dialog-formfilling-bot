package im.dlg.botsdk.light;

import im.dlg.botsdk.domain.InteractiveEvent;

@FunctionalInterface
public interface InteractiveEventListener {
    void onEvent(InteractiveEvent event);
}
