package commands;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public abstract class ORCommand extends FrameDecoder{
	private boolean finished = false;
	private String error = null;
	@Override
	protected final ORCommand decode(ChannelHandlerContext ctx, Channel ch, ChannelBuffer buffer) throws Exception {
		buffer.markReaderIndex();
		ORDecode(ctx,ch,buffer);
		if(finished) return this;
		else{
			buffer.resetReaderIndex();
			return null;
		}
	}
	/**
	 * called by decode so this ORCommand can retrieve it's values before being returned. 
	 * must call done when finished retrieving all bytes, otherwise, this ORCommand will never return as a frame
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	protected abstract void ORDecode(ChannelHandlerContext arg0, Channel arg1, ChannelBuffer arg2);
	/**
	 * must be called when the ORCommand is done decoding the message
	 */
	protected final void done(){
		finished = true;
	}
	protected final void setError(String error){
		this.error = error;
	}
	public String getError(){
		return error;
	}
	public boolean isOk(){
		if(error != null) return false;
		else return true;
	}
}
