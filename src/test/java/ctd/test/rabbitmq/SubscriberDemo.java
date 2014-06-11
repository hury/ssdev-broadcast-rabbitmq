package ctd.test.rabbitmq;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import ctd.net.broadcast.Observer;
import ctd.net.broadcast.exception.BroadException;
import ctd.net.broadcast.rabbitmq.RabbitMQSubscriber;

public class SubscriberDemo {

	
	public static void main(String[] args) throws BroadException {
		final RabbitMQSubscriber s = new RabbitMQSubscriber();
		s.setHostName("127.0.0.1");
		s.setVirtualHost("/");
		s.setUserName("guest");
		s.setPassword("guest");
		s.setCodec("json");
		s.connect();
		
		String topic = "DictionaryWatcher";
		
		s.attach(topic, new Observer() {			
			@Override
			public void onMessage(Serializable... arg) {
				System.out.println(arg[0]);
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread() { 
			@Override
			public void run() {
				System.out.println("jvm shutdown");
				try {
					s.disconnect();
				} 
				catch (BroadException e) {
					
					e.printStackTrace();
				}
			}
		});
		
		try {
			TimeUnit.SECONDS.sleep(60);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			s.disconnect();
		}
	}

}
