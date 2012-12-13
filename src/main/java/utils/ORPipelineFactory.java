package main.java.utils;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;

import main.java.commands.ORCoder;

public class ORPipelineFactory implements ChannelPipelineFactory{
	private final SimpleChannelHandler handler;
	public ORPipelineFactory(SimpleChannelHandler handler){
		this.handler = handler;
	}
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		return Channels.pipeline(new ORCoder.ORDecoder(),new ORCoder.OREncoder(),handler);
	}

}
