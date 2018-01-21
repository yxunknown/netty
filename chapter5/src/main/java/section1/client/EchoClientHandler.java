/**
 * EchoClientHandler.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/21/18 11:03
 */
package section1.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

    private int counter = 0;

    private static final String ECHO_REQ = "HELLO, THIS IS FROM NETTY CLIENT GREET.$_";

    public EchoClientHandler() {}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        counter++;
        System.out.println("This is " + counter + " times receive server :[" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
