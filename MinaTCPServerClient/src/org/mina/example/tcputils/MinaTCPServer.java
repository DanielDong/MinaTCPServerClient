package org.mina.example.tcputils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaTCPServer extends IoHandlerAdapter{
	
	public static final int PORT = 8086;
	public static final int MAX_RCVD = 10000;
	
	private long t0 = 0;
	private AtomicInteger nbRcvd = null;
	
	public MinaTCPServer () throws IOException{
		nbRcvd = new AtomicInteger();
		IoAcceptor acceptor = new NioSocketAcceptor();
		DefaultIoFilterChainBuilder filterChainBuilder = acceptor.getFilterChain();
		IoSessionConfig config = acceptor.getSessionConfig();
		
//		filterChainBuilder.addLast("logging", new LoggingFilter());
		filterChainBuilder.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
		
		acceptor.setHandler(this);
		
		config.setMinReadBufferSize(1024);
		config.setMaxReadBufferSize(1024);
		config.setIdleTime(IdleStatus.BOTH_IDLE, 5);
		
		acceptor.bind(new InetSocketAddress(PORT));
	}
	
//	@Override
//	public void messageSent(IoSession session, Object msg) throws IOException{
//		
//		System.out.println("Ack received by client.");
//	}
//	
//	@Override
//	public void messageReceived(IoSession session, Object msg) throws IOException{
//		
//	}
//	
//	@Override
//	public void sessionOpened(IoSession session) throws IOException{}
//	
//	@Override
//	public void sessionCreated(IoSession session) throws IOException{
//		System.out.println("Session created...");
//	}
//	
//	@Override
//	public void sessionClosed(IoSession session) throws IOException{
//		System.out.println("Session closed...");
//		//nbRcvd.set(0);
//	}
//	
//	@Override
//	public void exceptionCaught(IoSession session, Throwable cause) throws IOException{
//		cause.printStackTrace();
//		session.close(true);
//		System.out.println("Exception caught...");
//	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new MinaTCPServer();
	}

@Override
public void sessionCreated(IoSession session) throws Exception {
	// TODO Auto-generated method stub
	System.out.println(session.getRemoteAddress().toString() + " has created! session id: " + session.getId());
}

@Override
public void sessionOpened(IoSession session) throws Exception {
	// TODO Auto-generated method stub
	System.out.println(session.getRemoteAddress().toString() + " has opened!");
}

@Override
public void sessionClosed(IoSession session) throws Exception {
	// TODO Auto-generated method stub
	String hostName = ((InetSocketAddress)(session.getRemoteAddress())).getAddress().getHostAddress() ;
	int hostPort = ((InetSocketAddress)(session.getRemoteAddress())).getPort();
	System.out.println(hostName+ "|" + hostPort + " has left!");
	
}

@Override
public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	// TODO Auto-generated method stub
	
}

@Override
public void exceptionCaught(IoSession session, Throwable cause)
		throws Exception {
	// TODO Auto-generated method stub
	System.out.println("EXCEPTION..." + cause.getMessage());
	
}

@Override
public void messageReceived(IoSession session, Object message) throws Exception {
	// TODO Auto-generated method stub
	System.out.println("Received msg..." + message.toString());
	int nb = nbRcvd.incrementAndGet();
	if(nb == 1){
		t0 = System.currentTimeMillis();
	}
	//System.out.println("Received msg 1...");
	if(nb % 1000 == 0){
		System.out.println("Received " + nb + " messages.");
	}
	
	if(nb == MAX_RCVD){
		long t1 = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (t1 - t0) + " miliseconds.");
	}
	
	session.write(message);
	//System.out.println("Ack msg...");
}

@Override
public void messageSent(IoSession session, Object message) throws Exception {
	// TODO Auto-generated method stub
	
}

}
