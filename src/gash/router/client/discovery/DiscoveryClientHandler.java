package gash.router.client.discovery;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class DiscoveryClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket route) throws Exception {
        System.out.println("--> Discovery done.");
    }
}
