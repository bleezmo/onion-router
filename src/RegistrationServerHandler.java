import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import commands.ORCommand;
import commands.ORCommands;


public class RegistrationServerHandler extends SimpleChannelHandler{

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
		final Channel ch = e.getChannel();
		ORCommand command = ORCommands.decode(buffer);
		ChannelFuture future = ch.write(command.getResult());
		future.addListener(new ChannelFutureListener(){
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				ch.close();
			}
		});
	}
}
