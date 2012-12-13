package utils;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;

import commands.ORDecoder;

public class ORPipelineFactory implements ChannelPipelineFactory{
	private final SimpleChannelHandler handler;
	public ORPipelineFactory(SimpleChannelHandler handler){
		this.handler = handler;
	}
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		return Channels.pipeline(new ORDecoder(),handler);
	}

}
