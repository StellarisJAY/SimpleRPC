package com.jay.rpc;

import com.jay.rpc.annotation.RpcService;
import com.jay.rpc.discovery.ServiceMapper;
import com.jay.rpc.discovery.ZookeeperServiceDiscovery;
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
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  RPC 服务器
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcServer implements ApplicationContextAware {
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${rpc.service.port}")
    private String port;

    @Value("${spring.application.name}")
    private String applicationName;


    @Autowired
    private ZookeeperServiceDiscovery serviceDiscovery;

    @Autowired
    private ServiceMapper serviceMapper;

    private ApplicationContext context;

    /**
     * 初始化Netty服务器
     * @return ServerBootstrap
     */
    private ServerBootstrap init(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel){
                        /*
                            处理器管线
                         */
                        ChannelPipeline pipeline = channel.pipeline();
                        // Rpc解码器
                        pipeline.addLast(new RpcDecoder());
                        // Rpc请求处理器
                        pipeline.addLast(new RpcRequestHandler(context));
                        // Rpc编码器
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
        // 初始化服务器属性
        ServerBootstrap serverBootstrap = init();

        try {
            logger.info("RPC服务启动中...");
            // 获取服务地址
            InetAddress localHost = InetAddress.getLocalHost();
            String host = localHost.getHostAddress() + ":" + port;
            // 注册到Zookeeper
            serviceDiscovery.registerService(applicationName, host);
            logger.info("服务注册成功，服务名称：{}", applicationName);
            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(Integer.parseInt(port)).sync();
            if(channelFuture.isSuccess()){
               System.out.println("RPC服务启动成功，服务地址:"+host);
            }
            else{
                System.out.println("RPC服务启动失败");
            }
        }catch (KeeperException e){
            logger.error("服务注册异常", e);
        } catch (InterruptedException | IOException e) {
            logger.error("服务器启动出现异常", e);
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
            Map<String, Object> serviceImpls = context.getBeansWithAnnotation(RpcService.class);
            logger.info("是否扫描到RpcService:{}", serviceImpls.isEmpty());
            Set<Map.Entry<String, Object>> entries = serviceImpls.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                Object bean = entry.getValue();
                Class<?>[] interfaces = bean.getClass().getInterfaces();
                serviceMapper.put(interfaces[0], bean);
            }
        }
    }

    public void setPort(String port) {
        this.port = port;
    }
}
