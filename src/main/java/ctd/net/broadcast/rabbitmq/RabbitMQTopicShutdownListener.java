package ctd.net.broadcast.rabbitmq;

public interface RabbitMQTopicShutdownListener {
	void onTopicShutdown(RabbitMQTopic topic, Exception ex);
}
