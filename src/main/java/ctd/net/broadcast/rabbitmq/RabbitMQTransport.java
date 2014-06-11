package ctd.net.broadcast.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import ctd.net.broadcast.codec.BroadcastCodec;
import ctd.net.broadcast.codec.BroadcastCodecFactory;
import ctd.net.broadcast.exception.BroadException;

public class RabbitMQTransport {
	
	private String userName;
	private String password;
	private String virtualHost;
	private String hostName;
	private int port = 5672;
	
	protected boolean connectStatus;
	protected Connection conn;
	protected BroadcastCodec codec = BroadcastCodecFactory.getCodec(BroadcastCodecFactory.HESSIAN);
	
	public void setCodec(String codecName){
		codec = BroadcastCodecFactory.getCodec(codecName);
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void connect() throws BroadException {
		try{	
			if(conn == null || !conn.isOpen()){
				ConnectionFactory factory = new ConnectionFactory();
				factory.setUsername(userName);
				factory.setPassword(password);
				factory.setVirtualHost(virtualHost);
				factory.setHost(hostName);
				factory.setPort(port);
				conn = factory.newConnection();
			}
			connectStatus = true;
		}
		catch(IOException e){
			throw new BroadException(e,BroadException.CONNECT_FALIED);
		}
		catch(Exception e){
			throw new BroadException(e);
		}
	}
	
	public void disconnect() throws BroadException {
		try{	
			if(conn != null && conn.isOpen()){
				conn.close();
			}
		}
		catch(IOException e){
			throw new BroadException(e,BroadException.CONNECT_FALIED);
		}
		finally{
			connectStatus = false;
		}
	}
}
