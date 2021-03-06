package gash.router.server.communication;

import gash.router.container.RoutingConf;
import gash.router.server.Server;
import gash.router.server.raft.Raft;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * initialize netty communication
 * <p>
 * port The port to listen to
 */
public class HeartbeatServer implements Runnable {
    protected static Logger logger = LoggerFactory.getLogger("heartbeatServer");
    RoutingConf conf;

    public HeartbeatServer(RoutingConf conf) {
        this.conf = conf;
    }

    public void run() {
        // construct boss and worker threads (num threads = number of cores)

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            Server.bootstrap.put(conf.getHeartbeatPort(), b);

            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 100);
            //b.option(ChannelOption.TCP_NODELAY, true);
            //b.option(ChannelOption.SO_KEEPALIVE, true);
            // b.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR);

            boolean compressComm = false;
            b.childHandler(new ServerInit(conf, compressComm, "commServerHandler"));

            // Start the server.
            logger.info("Starting server, listening on port = " + conf.getHeartbeatPort());
            ChannelFuture f = b.bind(conf.getHeartbeatPort()).sync();

            logger.info(f.channel().localAddress() + " -> open: " + f.channel().isOpen() + ", write: "
                    + f.channel().isWritable() + ", act: " + f.channel().isActive());

            Raft.getInstance().setConf(conf);

            // block until the server socket is closed.
            f.channel().closeFuture().sync();

        } catch (Exception ex) {
            // on bind().sync()
            logger.error("Failed to setup handler.", ex);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}