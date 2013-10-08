package org.mina.example.tcputils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MinaTCPClient extends IoHandlerAdapter{

	private IoConnector connector;
	private static IoSession session;
	private boolean rcvd = false;
	
	public MinaTCPClient (String remoteAddress){
		session = null;
		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
		connector.setHandler(this);
		connector.getSessionConfig().setMaxReadBufferSize(1024);
		connector.getSessionConfig().setMinReadBufferSize(1024);
		
		ConnectFuture future = connector.connect(new InetSocketAddress(remoteAddress, MinaTCPServer.PORT));
		future.awaitUninterruptibly();
		session = future.getSession();
		session.write("HELLO FROM CLIENT MINA...");
	}
			
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		String remoteAddr = args[0];
		MinaTCPClient client = new MinaTCPClient(remoteAddr);
		// Make sure session has a value.
		while(session == null);
		
		long t0 = System.currentTimeMillis();
		for(int i = 0; i < MinaTCPServer.MAX_RCVD; i ++){
			
			IoBuffer buffer = IoBuffer.allocate(4);
			buffer.putInt(i);
			buffer.flip();
			
			session.write(buffer.toString());
			while(client.rcvd == false){
				Thread.sleep(1);
			}
			
			client.rcvd = false;
			if(i % 10000 == 0){
				System.out.println("Sent " + i + " messages.");
			}
		}
		long t1 = System.currentTimeMillis();
		System.out.println("Sent all messages. Time elapsed: " + (t1 - t0) + " miliseconds.");
		client.connector.dispose(true);
	}


	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		InetSocketAddress socketAddr = (InetSocketAddress) session.getRemoteAddress();
		String hostName = socketAddr.getAddress().getHostAddress();
		int port = socketAddr.getPort();
		System.out.println(hostName + "|" + port+ " joined.");
	}


	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(session.getRemoteAddress().toString() + " opened.");
	}


	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println(session.getRemoteAddress().toString() + " exception caught." + cause.getMessage());
	}


	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		rcvd = true;
	}


	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
