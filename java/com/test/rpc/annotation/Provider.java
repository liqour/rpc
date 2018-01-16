package com.test.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)// 运行时
@Target({ElementType.TYPE})// 类注释
public @interface Provider {

}
