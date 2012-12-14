package main.java.client_src.terminal;

import java.util.ArrayList;

import main.java.globals.NodeList;
import main.java.utils.ChannelWrite;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TerminalHandler extends SimpleChannelHandler{

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		byte[] welcome = "Welcome to Onion Router!\nType each command with a trailing semicolon\nType 'help;' to see the commands\n\n>".getBytes();
		Channel ch = e.getChannel();
		ChannelBuffer cb = ChannelBuffers.buffer(welcome.length);
		cb.writeBytes(welcome);
		ch.write(cb);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		String command = (String) e.getMessage();
		if(command.equals(TerminalCommands.HELP)){
			ChannelWrite.write(e.getChannel(), (TerminalCommands.NODE_LIST+" - Display the list of available nodes from the directory server\n\n").getBytes());
			ChannelWrite.write(e.getChannel(), (TerminalCommands.NEW_CIRCUIT+" - Create a new circuit with a random node from the list\n\n").getBytes());
			ChannelWrite.write(e.getChannel(), (TerminalCommands.EXTEND_CIRCUIT+" - Extend the current circuit. Must have created a new circuit first.\n\n").getBytes());
			ChannelWrite.write(e.getChannel(), (TerminalCommands.SEND+" <url> - use the newly created circuit to perform an HTTP GET anonymously!\n\n").getBytes());
			ChannelWrite.write(e.getChannel(), ">".getBytes());
		}else if(command.equals(TerminalCommands.QUIT)){
			ChannelWrite.write(e.getChannel(), "Goodbye\n".getBytes()).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					future.getChannel().close();
				}
			});
		}else if(command.equals(TerminalCommands.NEW_CIRCUIT)){
			
			ChannelWrite.write(e.getChannel(), ">".getBytes());
		}else if(command.equals(TerminalCommands.EXTEND_CIRCUIT)){
			
			ChannelWrite.write(e.getChannel(), ">".getBytes());
		}else if(command.equals(TerminalCommands.NODE_LIST)){
			ArrayList<NodeList.Node> nodeList = NodeList.getAll();
			for(int i = 0; i < nodeList.size(); i++){
				NodeList.Node node = nodeList.get(i);
				String out = node.getNodeName()+" "+node.getAddr().getAddress().getHostAddress()+" "+node.getAddr().getPort()+"\n";
				ChannelWrite.write(e.getChannel(), out.getBytes());
			}
			ChannelWrite.write(e.getChannel(), ">".getBytes());
		}else if(command.equals(TerminalCommands.SEND)){
			
			ChannelWrite.write(e.getChannel(), ">".getBytes());
		}else{
			ChannelWrite.write(e.getChannel(), ("unknown command: "+command+"\n").getBytes());
			ChannelWrite.write(e.getChannel(), ">".getBytes());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		e.getCause().printStackTrace();
	}
}
