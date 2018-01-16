package com.test.rpc.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.test.rpc.annotation.Provider;
import com.test.rpc.entity.Transfer;
import com.test.rpc.socket.Client;
import com.test.rpc.socket.Server;

public class RpcProxyServer<T> {
	public static final Map<Class<?>, Class<?>> factoryServer = new HashMap<>();
	
	/**
	 * 
	 * @Description: 启动socket客户端 
	 * @Title: serverStart 
	 * @param @param url    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 * @author liquor
	 */
	public static void serverStart(String url){
		int port = 61676;
		Server server = new Server(port);
		server.start();
		
		Set<Class<?>> classesList = null;
		classesList = AnnotationUtil.getAnnotation(url,Provider.class);
		for(Class<?> clazz:classesList){
			for(Class<?> interf: clazz.getInterfaces()){
				RpcProxyServer.factoryServer.put(interf, clazz);
			}
		}
	}
	
	/**
	 * 
	 * @Description: 代理
	 * @Title: getProxy 
	 * @param @param clazz
	 * @param @return
	 * @param @throws IllegalArgumentException
	 * @param @throws ClassNotFoundException    设定文件 
	 * @return T    返回类型 
	 * @throws 
	 * @author liquor
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T getProxy(Class clazz) throws IllegalArgumentException, ClassNotFoundException{
		return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String serverIp = "127.0.0.1";
				Client client = new Client(serverIp, 61676);
				Transfer t = new Transfer();
				t.setClazz(clazz);
				t.setMethodName(method.getName());
				t.setParamType(method.getParameterTypes());
				t.setParams(args);
				byte[] content = SerializeUtil.serialize(t);
				byte[] buffer = ByteUtil.unitByteArray(NumberUtil.unsignedShortToByte2(content.length), content);
				return client.start(buffer);
			}
		});
	}
	
}
