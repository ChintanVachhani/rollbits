package gash.router.client.discovery;

import gash.router.container.RoutingConf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSideDiscoveryServer implements Runnable {

    protected static Logger logger = LoggerFactory.getLogger("discovery");

    private RoutingConf conf;

    public ClientSideDiscoveryServer(RoutingConf conf) {
        this.conf = conf;
    }

    @Override
    public void run() {
        logger.info("Discovery starting");

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ClientSideDiscoveryInit(conf, false));

            logger.info("Starting server, listening on port = " + conf.getInternalDiscoveryPort());
            ChannelFuture f = b.bind(conf.getInternalDiscoveryPort()).sync();


            logger.info(f.channel().localAddress() + " -> open: " + f.channel().isOpen() + ", write: "
                    + f.channel().isWritable() + ", act: " + f.channel().isActive());

            f.channel().closeFuture().await();

        } catch (InterruptedException ex) {
            // on bind().sync()
            logger.error("Failed to setup handler.", ex);
        } finally {
            group.shutdownGracefully();
        }
    }
}