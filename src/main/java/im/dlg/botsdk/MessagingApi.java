package im.dlg.botsdk;

import dialog.MessagingGrpc;
import dialog.MessagingOuterClass;
import dialog.MessagingOuterClass.MessageContent;
import dialog.MessagingOuterClass.RequestSendMessage;
import dialog.MessagingOuterClass.TextMessage;
import dialog.MessagingOuterClass.UpdateMessage;
import dialog.Peers;
import im.dlg.botsdk.domain.Message;
import im.dlg.botsdk.domain.Peer;
import im.dlg.botsdk.light.MessageListener;
import im.dlg.botsdk.utils.Constants;
import im.dlg.botsdk.utils.MsgUtils;
import im.dlg.botsdk.utils.PeerUtils;
import im.dlg.botsdk.utils.UUIDUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MessagingApi {

    private InternalBotApi privateBot;
    private MessageListener onMessage = null;


    MessagingApi(InternalBotApi privateBot) {
        this.privateBot = privateBot;

        privateBot.subscribeOn(UpdateMessage.class, msg -> {
            try {
                if (msg.getMessage().getBodyCase().getNumber() == 1) {
                    privateBot.findOutPeer(msg.getPeer()).thenAccept(optOutPeer -> {
                        optOutPeer.ifPresent(outPeer -> {
                            privateBot.loadSenderOutPeer(msg.getSenderUid(), outPeer, msg.getDate())
                                    .thenAcceptAsync(optSenderOutPeer ->
                                            optSenderOutPeer.ifPresent(senderOutPeer -> {
                                                final String text = msg.getMessage().getTextMessage().getText();
                                                final UUID uuid = UUIDUtils.convert(msg.getMid());
                                                onReceiveMessage(new Message(
                                                        PeerUtils.toDomainPeer(outPeer),
                                                        PeerUtils.toDomainPeer(senderOutPeer),
                                                        uuid, text, msg.getDate()));
                                            })
                                    );
                        });
                    });
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Subscribe to incoming messages
     *
     * @param listener - listener callback
     */
    public void onMessage(@Nullable MessageListener listener) {
        onMessage = listener;
    }

    /**
     * see #send
     */
    public CompletableFuture<UUID> send(@Nonnull Peer peer, @Nonnull String text) {
        return send(peer, text, null);
    }

    /**
     * Send message to particular peer
     *
     * @param peer       - the address peer user/channel/group
     * @param text       - text of message
     * @param targetUser - message will be visible only to this UID
     * @return - future with message UUID, that completes when deliver to server
     */
    public CompletableFuture<UUID> send(@Nonnull Peer peer, @Nonnull String text, @Nullable Integer targetUser) {
        Peers.OutPeer outPeer = PeerUtils.toServerOutPeer(peer);
        MessageContent msg = MessageContent.newBuilder()
                .setTextMessage(TextMessage.newBuilder().setText(text).build())
                .build();

        RequestSendMessage.Builder request = RequestSendMessage.newBuilder().setRid(MsgUtils.uniqueCurrentTimeMS())
                .setPeer(outPeer).setMessage(msg);

        if (targetUser != null) {
            request.setIsOnlyForUser(targetUser);
        }

        return privateBot.withToken(
                MessagingGrpc.newFutureStub(privateBot.channel.getChannel())
                        .withDeadlineAfter(2, TimeUnit.MINUTES),
                stub -> stub.sendMessage(request.build())
        ).thenApplyAsync(resp -> UUIDUtils.convert(resp.getMid()), privateBot.executor.getExecutor());
    }


    /**
     * Load history of messages in a chat
     *
     * @param peer      - peer chat
     * @param date      - date from
     * @param limit     - size of message batch
     * @param direction - direction
     * @return future with messages list
     */
    public CompletableFuture<List<Message>> load(Peer peer, long date, int limit, Direction direction) {

        MessagingOuterClass.RequestLoadHistory.Builder request =
                MessagingOuterClass.RequestLoadHistory.newBuilder()
                        .setPeer(PeerUtils.toServerOutPeer(peer))
                        .setDate(date).setLimit(limit)
                        .addAllOptimizations(Constants.OPTIMISATIONS);

        if (direction == Direction.FORWARD) {
            request.setLoadMode(MessagingOuterClass.ListLoadMode.LISTLOADMODE_FORWARD);
        } else if (direction == Direction.BACKWARD) {
            request.setLoadMode(MessagingOuterClass.ListLoadMode.LISTLOADMODE_BACKWARD);
        } else if (direction == Direction.BOTH) {
            request.setLoadMode(MessagingOuterClass.ListLoadMode.LISTLOADMODE_BOTH);
        }

        return privateBot.withToken(
                MessagingGrpc.newFutureStub(privateBot.channel.getChannel())
                        .withDeadlineAfter(2, TimeUnit.MINUTES),
                stub -> stub.loadHistory(request.build())
        ).thenApplyAsync(resp -> resp.getHistoryList().stream()
                .map(MsgUtils::toMessage).collect(Collectors.toList()));
    }

    /**
     * Marking a message and all previous as read
     *
     * @param peer - chat peer
     * @param date - date of message
     * @return a future
     */
    public CompletableFuture<Void> read(@Nonnull Peer peer, long date) {
        MessagingOuterClass.RequestMessageRead request = MessagingOuterClass.RequestMessageRead.newBuilder()
                .setPeer(PeerUtils.toServerOutPeer(peer)).setDate(date).build();

        return privateBot.withToken(
                MessagingGrpc.newFutureStub(privateBot.channel.getChannel())
                        .withDeadlineAfter(2, TimeUnit.MINUTES),
                stub -> stub.messageRead(request)
        ).thenApplyAsync(resp -> null, privateBot.executor.getExecutor());
    }

    private void onReceiveMessage(@Nonnull Message message) {
        if (onMessage != null) {
            onMessage.onMessage(message);
            return;
        }

        System.out.println("Got a message");
    }

    public enum Direction {
        FORWARD,
        BACKWARD,
        BOTH
    }
}
