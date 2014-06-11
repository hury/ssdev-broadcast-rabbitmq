package ctd.test.rabbitmq;

import java.util.concurrent.TimeUnit;

import ctd.net.broadcast.exception.BroadException;
import ctd.net.broadcast.rabbitmq.RabbitMQPublisher;

public class PublisherDemo {
	
	public static void main(String[] args) throws BroadException{
		RabbitMQPublisher p = new RabbitMQPublisher();
		p.setHostName("127.0.0.1");
		p.setVirtualHost("/");
		p.setUserName("guest");
		p.setPassword("guest");
		p.setCodec("json");
		
		p.connect();
		
		
		String topic = "DictionaryWatcher";
		
		int i = 0;
		while(!Thread.currentThread().isInterrupted()){
			String msg = "msg" + i;
			p.publish(topic, msg);
			System.out.println("send msg:" + msg);
			i ++;
			try {
				TimeUnit.SECONDS.sleep(1);
			} 
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		p.disconnect();
		
	}
}
