package ctd.net.broadcast.rabbitmq;

import ctd.util.NetUtils;

public class QueueNameUtils {
	public static String getQueueName(){
		return NetUtils.getLocalHost();
	}
}
