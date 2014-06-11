package ctd.net.broadcast.rabbitmq;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import ctd.net.broadcast.Publisher;
import ctd.net.broadcast.codec.BroadcastCodec;
import ctd.net.broadcast.codec.BroadcastCodecFactory;
import ctd.net.broadcast.exception.BroadException;

@SuppressWarnings("unused")
public class RabbitMQPublisher extends RabbitMQTransport implements Publisher {
	public static final String EXECHANGE_TYPE = "fanout";
	private static Set<String> declareTopics = new HashSet<>();
	private Channel channel;
	
	
	@Override
	public void connect() throws BroadException {
		super.connect();
		try {
			channel = conn.createChannel();
		} 
		catch (IOException e) {
			throw new BroadException(e,BroadException.CONNECT_FALIED);
		}
	}
	
	private void addTopic(String topic) throws BroadException {
		try {
			if(channel == null || !channel.isOpen()){
				connect();
			}
			channel.exchangeDeclare(topic, EXECHANGE_TYPE,true);
			declareTopics.add(topic);
		}
		catch (IOException e) {
			throw new BroadException(e,BroadException.WRITE_FAILED);
		}
	}
	
	@Override
	public void publish(String topic, Serializable... args) throws BroadException {
		if(channel == null || !channel.isOpen()){
			connect();
		}
		try {
			if(!declareTopics.contains(topic)){
				addTopic(topic);
			}
			channel.basicPublish(topic, "", null, codec.encode(args));
		} 
		catch (IOException e) {
			throw new BroadException(e,BroadException.WRITE_FAILED);
		}
	}

}
