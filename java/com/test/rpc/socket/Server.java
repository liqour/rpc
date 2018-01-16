package com.test.rpc.socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import com.test.rpc.entity.Transfer;
import com.test.rpc.util.ByteUtil;
import com.test.rpc.util.NumberUtil;
import com.test.rpc.util.RpcProxyServer;
import com.test.rpc.util.SerializeUtil;

public class Server {

	private int port;// 端口号
	static volatile boolean running=false;// 是否是运行状态
	private long receiveTimeDelay=30000;// 超时时间
	private Thread connWatchDog;
	
	/**
	 * 有参构造
	 */
	public Server(int port) {
		this.port = port;
	}
	
	/**
	 * 启动服务端
	 */
	public void start(){
		if(running){
			return;
		}
		running=true;
		connWatchDog = new Thread(new ConnWatchDog());
		connWatchDog.start();
	}
	
	/**
	 * @Description: 关闭连接 
	 */
	@SuppressWarnings("deprecation")
	public void stop(){
		if(running)running=false;
		if(connWatchDog!=null)connWatchDog.stop();
	}
	
	/**
	 * @Description: 线程处理类 
	 * @ClassName: ConnWatchDog 
	 * @author liquor
	 * @date 2017年9月2日 下午3:01:47 
	 *
	 */
	class ConnWatchDog implements Runnable{
		@SuppressWarnings("resource")
		public void run(){
			try {
				ServerSocket ss = new ServerSocket(port,5);
				while(running){
					Socket s = ss.accept();// 取出连接
					new Thread(new SocketAction(s)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				Server.this.stop();
			}
			
		}
	}
	
	/**
	 * @Description: 连接处理类 
	 * @ClassName: SocketAction 
	 * @author liquor
	 * @date 2017年9月2日 下午3:03:37 
	 *
	 */
	class SocketAction implements Runnable{
		Socket s;
		boolean run=true;
		// 记录开始连接时间
		long lastReceiveTime = System.currentTimeMillis();
		
		public SocketAction(Socket s) {
			this.s = s;
		}
		
		public void run() {
			while(running && run){
				if(System.currentTimeMillis()-lastReceiveTime>receiveTimeDelay){
					overThis();
				}else{
					try {
						byte[] b = new byte[1024];
						InputStream in = s.getInputStream();
						if(in.available()>0){
							InputStream ois = in;
							ois.read(b,0,2);
							int length = NumberUtil.byte2ToUnsignedShort(Arrays.copyOfRange(b, 0, 2));
							ois.read(b, 0, length);
							Transfer t = (Transfer) SerializeUtil.unSerialize(b);

							Class<?> clazz = RpcProxyServer.factoryServer.get(t.getClazz());
							Method method = clazz.getDeclaredMethod(t.getMethodName(),t.getParamType());
							Object obj = method.invoke(clazz.newInstance(), t.getParams());
							
							byte[] content = SerializeUtil.serialize(obj);
							byte[] buffer = ByteUtil.unitByteArray(NumberUtil.unsignedShortToByte2(content.length), content);
							OutputStream oos = this.s.getOutputStream();
							oos.write(buffer);
							oos.flush();
						}else{
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
						overThis();
					} 
				}
			}
		}
		
		/**
		 * 
		 * @Description: 关闭连接 
		 * @Title: overThis 
		 * @param     设定文件 
		 * @return void    返回类型 
		 * @throws 
		 * @author liquor
		 */
		private void overThis() {
			if(run){
				run=false;
			}
			if(s!=null){
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("关闭："+s.getRemoteSocketAddress());
		}
		
	}
}
