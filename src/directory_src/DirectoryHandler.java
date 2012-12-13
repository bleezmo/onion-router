package directory_src;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import globals.NodeList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import utils.Log;

import commands.ORCommand;
import commands.Register;
import commands.RegisterSuccess;

public class DirectoryHandler extends SimpleChannelHandler{

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ORCommand orc = (ORCommand) e.getMessage();
		if(orc.isOk()){
			if(orc.getClass() == Register.class){
				final Register r = (Register) orc;
				final Channel ch = e.getChannel();
				RegisterSuccess rs = new RegisterSuccess();
				ArrayList<InetSocketAddress> nodeList = NodeList.getAll();
				rs.setNodeList(nodeList);
				byte[] data= rs.encode();
				ChannelBuffer cb = ChannelBuffers.buffer(data.length);
				cb.writeBytes(data);
				ch.write(cb).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture cf) throws Exception {
						InetSocketAddress sa = (InetSocketAddress) ch.getRemoteAddress();
						NodeList.add(new InetSocketAddress(sa.getAddress(), r.getServerPort())); //use the server port given
						ch.close();
					}
				});
			}else Log.w("received unexpected command: "+orc.getCommandType());
		}else Log.f(orc.getError());
	}

}
