package com.jay.rpc.handler;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;


/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, RpcRequest rpcRequest) throws Exception {
        // 获取RpcService实现类
        Class<?> serviceClass = ServiceMapper.getServiceClass(rpcRequest.getClassName());
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try{
            // 获取目标方法
            Method method = serviceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            // 创建一个执行用实例
            Object instance = serviceClass.newInstance();
            // 执行方法
            Object result = method.invoke(instance, rpcRequest.getParameters());
            // 设置response的result
            response.setResult(method.getReturnType());
            response.setResult(result);
        }catch (Exception e){
            response.setError(e);
        }finally {
            // 发送response
            context.channel().writeAndFlush(response);
        }
    }
}
