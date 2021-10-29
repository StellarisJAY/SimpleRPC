package com.jay.rpc.client;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcProxy {

    private static Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz){
        // 动态代理，为方法添加RPC
        Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            // 创建RPC客户端
            RpcClient client = new RpcClient("192.168.154.1", 8000);
            // 创建RPC请求
            RpcRequest request = new RpcRequest();
            request.setTargetClass(clazz);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            LOGGER.info("发送RPC请求中，请求报文：{}", request);
            RpcResponse response = client.send(request);
            LOGGER.info("接收到RPC回复，返回：{}", response);
            if(response.getError() != null){
                throw response.getError();
            }
            return response.getResult();
        });
        // 返回接口类型的RPC实例
        return (T)proxyInstance;
    }
}
