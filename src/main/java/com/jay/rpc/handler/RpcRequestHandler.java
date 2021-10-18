package com.jay.rpc.handler;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.ApplicationContext;



/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ApplicationContext applicationContext;

    public RpcRequestHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, RpcRequest rpcRequest) throws Exception {
        // 从Spring容器获取RPC业务Bean
        RpcResponse response = new RpcResponse();
        try{
        }catch (Exception e){
            response.setError(e);
        }finally {
            // 发送response
            context.channel().writeAndFlush(response);
        }
    }
}
