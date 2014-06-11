package ctd.net.broadcast.rabbitmq;

import java.io.IOException;
import java.io.Serializable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import ctd.net.broadcast.codec.BroadcastCodec;
import ctd.net.broadcast.exception.BroadException;
import ctd.net.broadcast.support.AbstractTopic;

public class RabbitMQTopic extends AbstractTopic implements Runnable {
	private Channel channel;
	private volatile boolean running = true;
	private RabbitMQTopicShutdownListener shutdownListener;
	private BroadcastCodec codec;
	
	public RabbitMQTopic(String id){
		this.id = id;
	}
	
	public void setupQueue(Channel channel) throws IOException {
		this.channel = channel;
		String queueName = QueueNameUtils.getQueueName();
		channel.queueDeclare(queueName, false, false, false, null);
		channel.queueBind(queueName, id, "");

	}
	
	public void clearQueue() {
		String queueName = QueueNameUtils.getQueueName();
		try {
			if(channel.isOpen()){
				channel.queueDelete(queueName);
			}
		} 
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void onMessage(byte[] bytes) throws BroadException{
		Serializable[] args = codec.decode(bytes);
		notify(args);
	}

	@Override
	public void run() {
		Exception ex = null;
		try{
			QueueingConsumer consumer = new QueueingConsumer(channel);
			String queueName = QueueNameUtils.getQueueName();
	        try {
				channel.basicConsume(queueName, true, consumer);
			} 
	        catch (IOException e) {
				ex =  e;
				return;
			}
	        
			while(running && !Thread.currentThread().isInterrupted()){
				 try {
					QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					onMessage(delivery.getBody());
					
				} 
				catch (ShutdownSignalException e) {
					ex = e;
					break;
				} 
				catch (ConsumerCancelledException e) {
					 ex = e;
					 break;
				} 
				 catch (InterruptedException e) {
					 ex = e;
					 Thread.currentThread().interrupt();
				} 
				 catch (BroadException e) {
					
				}
			}
		}
		finally{
			clearQueue();
			if(shutdownListener != null){
				shutdownListener.onTopicShutdown(this,ex);
			}
		}
	}

	public void setShutdownListener(RabbitMQTopicShutdownListener shutdownListener) {
		this.shutdownListener = shutdownListener;
	}
	
	
	public void shutdown(){
		running = false;
	}

	public void setCodec(BroadcastCodec codec) {
		this.codec = codec;
	}
	
}
