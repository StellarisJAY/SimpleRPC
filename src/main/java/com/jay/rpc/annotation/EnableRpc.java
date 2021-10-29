package com.jay.rpc.annotation;

import com.jay.rpc.RpcServer;
import com.jay.rpc.discovery.ServiceMapper;
import com.jay.rpc.discovery.ZookeeperServiceDiscovery;
import com.jay.rpc.util.ZookeeperUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RpcServer.class, ZookeeperServiceDiscovery.class, ZookeeperUtil.class, ServiceMapper.class})
public @interface EnableRpc {
}
