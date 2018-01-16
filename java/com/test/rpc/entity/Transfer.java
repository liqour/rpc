package com.test.rpc.entity;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class Transfer implements Serializable {

	private static final long serialVersionUID = 6327566150486640970L;

	private Class clazz;
	
	private String methodName;
	
	private Class[] paramType;
	
	private Object[] params;

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class[] getParamType() {
		return paramType;
	}

	public void setParamType(Class[] paramType) {
		this.paramType = paramType;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}


}
