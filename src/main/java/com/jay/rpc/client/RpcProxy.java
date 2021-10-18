package com.jay.rpc.client;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcProxy {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz){
        // 动态代理，为方法添加RPC
        Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 创建RPC客户端
                RpcClient client = new RpcClient("127.0.0.1", 8000);
                // 创建RPC请求
                RpcRequest request = new RpcRequest();

                // 发送RPC请求，返回future对象
                RpcResponse response = client.send(request);
                if(response.getError() != null){
                    throw response.getError();
                }
                return response.getResult();
            }
        });
        // 返回接口类型的RPC实例
        return (T)proxyInstance;
    }
}
