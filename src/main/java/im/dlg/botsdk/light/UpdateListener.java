package im.dlg.botsdk.light;

import com.google.protobuf.GeneratedMessageV3;

@FunctionalInterface
public interface UpdateListener<T extends GeneratedMessageV3> {
    void onUpdate(T update);
}
