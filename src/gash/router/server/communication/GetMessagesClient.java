package gash.router.server.communication;

import gash.router.container.RoutingConf;
import gash.router.server.Node;
import gash.router.server.RoutingMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;

import java.util.ArrayList;
import java.util.List;

public class GetMessagesClient {
    protected static Logger logger = LoggerFactory.getLogger("getMessagesClient");
    RoutingConf conf;

    private Pipe.Route request;
    private Pipe.Route response;
    ChannelHandlerContext clientChannel;

    public GetMessagesClient(Pipe.Route request, Pipe.Route response, ChannelHandlerContext clientChannel) {
        this.request = request;
        this.response = response;
        this.clientChannel = clientChannel;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ServerInit(request, response, clientChannel, "getMessagesClientHandler"));
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.SO_KEEPALIVE, true);

            // Make the connection attempt.
            List<ChannelFuture> channels = new ArrayList<>();
            for (ArrayList<Node> nodes : RoutingMap.getInstance().getExternalServers().values()) {
                Node node = nodes.get(0);
                try {
                    ChannelFuture channel = b.connect(node.getNodeAddress(), node.getNodePort()).sync();
                    channel.channel().closeFuture().sync();
                    channels.add(channel);
                } catch (InterruptedException e) {
                    logger.error("Failed to connect to ", node.getNodeAddress(), e);
                }
            }

            /*System.out.println(channel.channel().localAddress() + " -> open: " + channel.channel().isOpen()
                    + ", write: " + channel.channel().isWritable() + ", reg: " + channel.channel().isRegistered());
            */

        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
