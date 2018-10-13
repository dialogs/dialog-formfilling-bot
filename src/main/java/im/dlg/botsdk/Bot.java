package im.dlg.botsdk;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Bot {

    private Config config;
    private String token;

    private final Object stopLock = new Object();

    private InternalBotApi internalBotApi;
    private MessagingApi messagingApi;
    private UsersApi users;
    private InteractiveApi interactiveApi;
    private DialogExecutor executor;

    private volatile CompletableFuture<Bot> voidCompletableFuture;

    private Bot(String token) {
        this.token = token;

        this.config = ConfigFactory.load().getConfig("dialog.botsdk");
        this.executor = new DialogExecutor(4);
    }

    public static CompletableFuture<Bot> start(String token) {

        Bot instance = new Bot(token);
        instance.internalBotApi = new InternalBotApi(token, instance.config.getString("host"),
                instance.config.getInt("port"), instance.executor);

        instance.voidCompletableFuture = instance.internalBotApi.start().thenAccept(v -> {
            instance.runApis(instance.internalBotApi);
        }).thenCompose(aVoid -> CompletableFuture.completedFuture(instance));

        return instance.voidCompletableFuture;
    }

    private void runApis(InternalBotApi internalBotApi) {
        messagingApi = new MessagingApi(internalBotApi);
        users = new UsersApi(internalBotApi);
        interactiveApi = new InteractiveApi(internalBotApi);
    }

    public void await() throws InterruptedException {
        synchronized (stopLock) {
            stopLock.wait();
        }
    }

    public void stop() {
        synchronized (stopLock) {
            stopLock.notifyAll();
        }
    }

    private void lock() {
        try {
            voidCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public MessagingApi messaging() {
        lock();
        return messagingApi;
    }

    public UsersApi users() {
        lock();
        return users;
    }

    public InteractiveApi interactiveApi() {
        lock();
        return interactiveApi;
    }
}
