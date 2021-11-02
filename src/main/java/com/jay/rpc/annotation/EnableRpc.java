package com.jay.rpc.annotation;

import com.jay.rpc.RpcServer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC启动注解
 * 将自动导入RPC服务器、Zookeeper服务注册等Bean
 * @author Jay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RpcServer.class})
public @interface EnableRpc {
}
