package main.java.client_src;

import main.java.commands.NewCircuit;
import main.java.commands.NewCircuitAck;
import main.java.commands.ORCommand;
import main.java.utils.Log;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;


public class ServerHandler  extends SimpleChannelHandler{
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		e.getCause().printStackTrace();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ORCommand command = (ORCommand) e.getMessage();
		if(command.isOk()){
			if(command instanceof NewCircuit){
				NewCircuit circuit = (NewCircuit) command;
				NewCircuitAck ack = new NewCircuitAck();
				ack.setCircuitId(circuit.getCircuitId());
				Log.db("received circuit request. sending acknowledgement");
				e.getChannel().write(ack).addListener(new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						future.getChannel().close();
					}
				});
			}else{
				Log.e("received an unexpected command "+command.getCommandType());
				e.getChannel().close();
			}
		}else{
			Log.e("an error occurred while decoding ["+command.getError()+"]");
			main.java.commands.Error error = new main.java.commands.Error();
			error.setMessage(command.getError());
			e.getChannel().write(error).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					future.getChannel().close();
				}
			});
		}
	}

}
