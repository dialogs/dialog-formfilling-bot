package im.dlg.botsdk.utils;

import dialog.Peers;
import im.dlg.botsdk.domain.Peer;

public class PeerUtils {
    public static Peers.Peer toPeer(Peers.OutPeer op) {
        return Peers.Peer.newBuilder()
                .setType(op.getType())
                .setId(op.getId())
                .build();
    }

    public static Peers.Peer toPeer(Peers.UserOutPeer uop) {
        return Peers.Peer.newBuilder()
                .setType(Peers.PeerType.PEERTYPE_PRIVATE)
                .setId(uop.getUid())
                .build();
    }

    public static Peers.Peer toPeer(Peers.GroupOutPeer uop) {
        return Peers.Peer.newBuilder()
                .setType(Peers.PeerType.PEERTYPE_GROUP)
                .setId(uop.getGroupId())
                .build();
    }

    public static Peers.OutPeer toOutPeer(Peers.UserOutPeer uop) {
        return Peers.OutPeer.newBuilder()
                .setType(Peers.PeerType.PEERTYPE_PRIVATE)
                .setId(uop.getUid())
                .setAccessHash(uop.getAccessHash())
                .build();
    }

    public static Peers.OutPeer toOutPeer(Peers.GroupOutPeer uop) {
        return Peers.OutPeer.newBuilder()
                .setType(Peers.PeerType.PEERTYPE_GROUP)
                .setId(uop.getGroupId())
                .setAccessHash(uop.getAccessHash())
                .build();
    }

    public static Peers.UserOutPeer toUserOutPeer(Peers.OutPeer outPeer) {
        return Peers.UserOutPeer.newBuilder()
                .setUid(outPeer.getId())
                .setAccessHash(outPeer.getAccessHash())
                .build();
    }

    public static Peers.Peer toUserPeer(Integer userId) {
        return Peers.Peer.newBuilder()
                .setType(Peers.PeerType.PEERTYPE_PRIVATE)
                .setId(userId)
                .build();
    }

    public static Peer toDomainPeer(Peers.OutPeer outPeer) {

        Peer.PeerType type;

        switch (outPeer.getType()) {
            case PEERTYPE_PRIVATE:
                type = Peer.PeerType.PRIVATE;
                break;
            case PEERTYPE_GROUP:
                type = Peer.PeerType.GROUP;
                break;
            case PEERTYPE_SIP:
                type = Peer.PeerType.SIP;
                break;
            case PEERTYPE_ENCRYPTEDPRIVATE:
            case PEERTYPE_UNKNOWN:
            case UNRECOGNIZED:
            default:
                type = Peer.PeerType.UNKNOWN;
                break;
        }

        return new Peer(outPeer.getId(), type, outPeer.getAccessHash());
    }

    public static Peers.OutPeer toServerOutPeer(Peer peer) {

        Peers.PeerType type;
        switch (peer.getType()) {

            case PRIVATE:
                type = Peers.PeerType.PEERTYPE_PRIVATE;
                break;
            case GROUP:
                type = Peers.PeerType.PEERTYPE_GROUP;
                break;
            case SIP:
                type = Peers.PeerType.PEERTYPE_SIP;
                break;
            default:
                type = Peers.PeerType.PEERTYPE_UNKNOWN;
                break;
        }

        return Peers.OutPeer.newBuilder()
                .setType(type)
                .setId(peer.getId())
                .setAccessHash(peer.getAccessHash())
                .build();
    }
}
