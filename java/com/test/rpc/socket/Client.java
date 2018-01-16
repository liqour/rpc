package com.test.rpc.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.test.rpc.util.NumberUtil;
import com.test.rpc.util.SerializeUtil;

public class Client {

	private String serverIp;
	private int port;
	private Socket socket;
	private boolean running = false;

	public Client(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}

	public Object start(byte[] buffer) throws UnknownHostException, IOException, ClassNotFoundException {
		if (running) {
			return null;
		}
		socket = new Socket(serverIp, port);
		running = true;
		Object obj = Client.this.sendObject(buffer);
		Client.this.stop();
		return obj;
	}

	public void stop() {
		if (running)
			running = false;
	}

	/**
	 * 
	 * @Description: 消息发送
	 * @Title: sendObject 
	 * @param @param obj
	 * @param @throws IOException    设定文件 
	 * @return void    返回类型 
	 * @throws ClassNotFoundException 
	 * @throws 
	 * @author liquor
	 */
	public Object sendObject(byte[] buffer) throws IOException, ClassNotFoundException {
		OutputStream os = socket.getOutputStream();
		os.write(buffer);
		os.flush();
		
		byte[] b = new byte[1024];
		InputStream ois = socket.getInputStream();
		ois.read(b,0,2);
		int length = NumberUtil.byte2ToUnsignedShort(Arrays.copyOfRange(b, 0, 2));
		ois.read(b,0,length);
		Object obj = SerializeUtil.unSerialize(b);
		return obj;
	}

}
