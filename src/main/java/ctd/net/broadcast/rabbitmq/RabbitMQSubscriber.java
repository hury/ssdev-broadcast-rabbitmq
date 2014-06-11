package ctd.net.broadcast.rabbitmq;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ctd.net.broadcast.Observer;
import ctd.net.broadcast.Subscriber;
import ctd.net.broadcast.Topic;
import ctd.net.broadcast.exception.BroadException;

public class RabbitMQSubscriber extends RabbitMQTransport implements Subscriber,RabbitMQTopicShutdownListener {
	private ExecutorService exec = Executors.newCachedThreadPool();
	private Map<String,RabbitMQTopic> topics = new ConcurrentHashMap<>();

	@Override
	public void attach(String topic, Observer observer) throws BroadException {
		RabbitMQTopic t = null;
		if(!topics.containsKey(topic)){
			t = createTopic(topic);
			exec.submit(t);
			topics.put(topic, t);
		}
		else{
			t = topics.get(topic);
		}
		t.attach(observer);
	}
	
	@Override
	public void detach(String topic, Observer observer) {
		if(topics.containsKey(topic)){
			Topic t = topics.get(topic);
			t.detach(observer);
		}
	}

	private RabbitMQTopic createTopic(String topic) throws BroadException {
		if(conn == null || !conn.isOpen()){
			connect();
		}
		try{
			RabbitMQTopic t = new  RabbitMQTopic(topic);
			t.setShutdownListener(this);
			t.setupQueue(conn.createChannel());
			t.setCodec(codec);
			return t;
		}
		catch (IOException e) {
			throw new BroadException(e);
		}
	}

	@Override
	public void onTopicShutdown(RabbitMQTopic topic,Exception ex) {
		topics.remove(topic.getId());
	}
	
	@Override
	public void disconnect() throws BroadException {
		
		exec.shutdownNow();
		try {
			exec.awaitTermination(30, TimeUnit.SECONDS);
		} 
		catch (InterruptedException e) {
			throw new BroadException(e);
		}
		finally{
			topics.clear();
			super.disconnect();
		}
	}

}
