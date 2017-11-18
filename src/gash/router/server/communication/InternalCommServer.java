package gash.router.server.communication;

import gash.router.container.RoutingConf;
import gash.router.server.Server;
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
     *
     * port The port to listen to
     */
    public class InternalCommServer implements Runnable {
    protected static Logger logger = LoggerFactory.getLogger("internalCommServer");
    RoutingConf conf;

        public InternalCommServer(RoutingConf conf) {
            this.conf = conf;
        }

        public void run() {
            logger.info("Internal Communication starting");

            // construct boss and worker threads (num threads = number of cores)

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap();
                Server.bootstrap.put(conf.getInternalCommunicationPort(), b);

                b.group(bossGroup, workerGroup);
                b.channel(NioServerSocketChannel.class);
                b.option(ChannelOption.SO_BACKLOG, 100);
                //b.option(ChannelOption.TCP_NODELAY, true);
                //b.option(ChannelOption.SO_KEEPALIVE, true);
                // b.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR);

                boolean compressComm = false;
                b.childHandler(new ServerInit(conf, compressComm,"commServerHandler"));

                // Start the server.
                logger.info("Starting server, listening on port = " + conf.getInternalCommunicationPort());
                ChannelFuture f = b.bind(conf.getInternalCommunicationPort()).syncUninterruptibly();

                logger.info(f.channel().localAddress() + " -> open: " + f.channel().isOpen() + ", write: "
                        + f.channel().isWritable() + ", act: " + f.channel().isActive());

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