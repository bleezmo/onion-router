package main.java.client_src.terminal;

import java.util.ArrayList;

import main.java.actions.ClientRegister;
import main.java.actions.CreateCircuit;
import main.java.actions.ExtendMyCircuit;
import main.java.client_src.Node;
import main.java.globals.GlobalVars;
import main.java.globals.MyCircuit;
import main.java.globals.NodeList;

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
		String welcome = "Welcome to Onion Router!\nYou're node name is "+GlobalVars.nodeName()+"\nType each command with a trailing semicolon\nType 'help;' to see the commands\n\n>";
		TerminalWrite.write(e.getChannel(), welcome);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		String command = (String) e.getMessage();
		if(command.equals(TerminalCommands.HELP)){
			TerminalWrite.write(e.getChannel(), (TerminalCommands.NODE_LIST+" - Display the list of available nodes from the directory server\n\n").getBytes());
			TerminalWrite.write(e.getChannel(), (TerminalCommands.NEW_CIRCUIT+" - Create a new circuit with a random node from the list\n\n").getBytes());
			TerminalWrite.write(e.getChannel(), (TerminalCommands.EXTEND_CIRCUIT+" - Extend the current circuit. Must have created a new circuit first.\n\n").getBytes());
			TerminalWrite.write(e.getChannel(), (TerminalCommands.SEND+" <url> - use the newly created circuit to perform an HTTP GET anonymously!\n\n").getBytes());
			TerminalWrite.write(e.getChannel(), ">".getBytes());
		}else if(command.equals(TerminalCommands.QUIT)){
			TerminalWrite.write(e.getChannel(), "Goodbye\n".getBytes()).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					future.getChannel().close();
				}
			});
		}else if(command.equals(TerminalCommands.NEW_CIRCUIT)){
			CreateCircuit.run(e.getChannel());
		}else if(command.equals(TerminalCommands.EXTEND_CIRCUIT)){
			if(MyCircuit.hasCircuit()){
				ExtendMyCircuit.run(e.getChannel());
			}else{
				TerminalWrite.write(e.getChannel(), "You do not have a circuit to extend. Try calling nc;\n>");
			}
		}else if(command.equals(TerminalCommands.NODE_LIST)){
			ClientRegister.run(e.getChannel());
		}else if(command.equals(TerminalCommands.SEND)){
			
			TerminalWrite.write(e.getChannel(), ">".getBytes());
		}else{
			TerminalWrite.write(e.getChannel(), "unknown command: "+command+"\n>");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		e.getCause().printStackTrace();
	}
}
