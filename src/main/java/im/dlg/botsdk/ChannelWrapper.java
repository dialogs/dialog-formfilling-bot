package im.dlg.botsdk;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class ChannelWrapper {

    private String host;
    private int port;
    private ManagedChannel channel;

    public ChannelWrapper(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() {

        if (channel != null) {
            channel.shutdownNow();
        }

        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .idleTimeout(10, TimeUnit.SECONDS)
                .usePlaintext()
                .build();
    }

    public synchronized ManagedChannel getChannel() {
        return channel;
    }
}
