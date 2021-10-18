package com.jay.rpc;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.handler.RpcDecoder;
import com.jay.rpc.handler.RpcEncoder;
import com.jay.rpc.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <p>
 *  RPC 服务器
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
@Component
public class RpcServer implements ApplicationContextAware {
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    private int port = 8001;
    private ApplicationContext context;

    private ServerBootstrap init(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new RpcDecoder(RpcRequest.class));
                        pipeline.addLast(new RpcRequestHandler(context));
                        pipeline.addLast(new RpcEncoder());
                    }
                });
        return serverBootstrap;
    }

    /**
     * 构造方法后自动启动服务器
     */
    @PostConstruct
    public void start(){
        ServerBootstrap serverBootstrap = init();

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if(channelFuture.isSuccess()){
               System.out.println("rpc服务器已启动");
            }
            else{
                System.out.println("failed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提供Spring Bean容器
     * @param applicationContext appContext
     * @throws BeansException BeanException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(applicationContext != null){
            this.context = applicationContext;
        }
    }

    public void setPort(int port) {
        this.port = port;
    }
}
