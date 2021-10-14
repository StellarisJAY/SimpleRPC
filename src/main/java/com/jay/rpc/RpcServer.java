package com.jay.rpc;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.handler.RpcDecoder;
import com.jay.rpc.handler.RpcEncoder;
import com.jay.rpc.handler.RpcRequestHandler;
import com.jay.rpc.util.ServiceScanner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcServer {
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    private int port;
    private String servicePackage;

    public RpcServer(int port, String servicePackage) {
        this.port = port;
        this.servicePackage = servicePackage;
    }

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
                        pipeline.addLast(new RpcRequestHandler());
                        pipeline.addLast(new RpcEncoder());
                    }
                });
        // 扫描RPC Service
        ServiceScanner.doScan(servicePackage);
        return serverBootstrap;
    }

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
}
