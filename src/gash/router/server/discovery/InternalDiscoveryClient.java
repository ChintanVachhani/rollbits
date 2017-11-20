package gash.router.server.discovery;

import gash.router.container.RoutingConf;
import gash.router.server.communication.HeartbeatServer;
import gash.router.server.raft.Raft;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import routing.Pipe.NetworkDiscoveryPacket;
import routing.Pipe.Route;

;

public final class InternalDiscoveryClient implements Runnable {

    // track requests
    private static long curID = 0;

    private RoutingConf conf;

    public InternalDiscoveryClient(RoutingConf conf) {
        this.conf = conf;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new DiscoveryClientHandler());

            Channel ch = b.bind(0).sync().channel();

            // Broadcast the NetworkDiscovery request to internal discovery port.
            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(buildNetworkDiscoveryRoutePacket(NetworkDiscoveryPacket.Mode.REQUEST, NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE).toByteArray()),
                    SocketUtils.socketAddress(conf.getNodeBroadcastAddress(), conf.getInternalDiscoveryPort()))).sync();


            System.out.println("Internal Network Discovery Request Sent.");
            // ClientSideDiscoveryClientHandler will close the DatagramChannel when a
            // response is received.  If the channel is not closed within 5 seconds,
            // print an error message and quit.
            /*if (!ch.closeFuture().await(5000)) {
                System.err.println("NetworkDiscovery request timed out.");
            }*/

        } catch (Exception e) {
            System.out.println("Failed to read route." + e);
        } finally {
            group.shutdownGracefully();
        }
    }

    // construct the networkDiscoveryPacket to send
    private Route buildNetworkDiscoveryRoutePacket(NetworkDiscoveryPacket.Mode mode, NetworkDiscoveryPacket.Sender sender) {
        NetworkDiscoveryPacket.Builder ndpb = NetworkDiscoveryPacket.newBuilder();
        ndpb.setMode(mode);
        ndpb.setSender(sender);
        ndpb.setGroupTag(conf.getGroupTag());
        ndpb.setNodeAddress(conf.getNodeAddress());
        ndpb.setNodeId(conf.getNodeId());
        ndpb.setNodePort(conf.getInternalCommunicationPort());
        ndpb.setSecret(conf.getSecret());

        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.NETWORK_DISCOVERY);
        rb.setNetworkDiscoveryPacket(ndpb);

        return rb.build();
    }

    /**
     * Since the service/server is asychronous we need a unique ID to associate
     * our requests with the server's reply
     *
     * @return
     */
    private static synchronized long nextId() {
        return ++curID;
    }

}

