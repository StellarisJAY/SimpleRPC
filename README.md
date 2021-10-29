# 简易RPC框架
## 目标
1、完成Spring整合   √

2、完善协议格式

3、完善序列化过程

4.1、整合Zookeeper作为服务注册中心 √

4.2、Zookeeper做服务发现

## 使用说明   

### 服务提供方（服务端）

#### 导入Maven依赖

```xml
		<dependency>
            <groupId>com.jay</groupId>
            <artifactId>rpc</artifactId>
            <version>1.0</version>
        </dependency>
```

#### 启动Zookeeper并在配置文件中添加

```properties
# RPC服务器地址
rpc.service.port=8000
# Zookeeper地址
rpc.service.discovery.zk.hosts=192.168.154.128:2181
# Zookeeper Session 断开超时时间
rpc.service.discovery.zk.session-timeout=5000
# 服务名称（必要）
spring.application.name=rpcService
```

#### 添加@EnableRpc注解

```java
@EnableRpc
@SpringBootApplication
public class TestApplication{
    public static void main(String[] args){
        SpringApplication.run(TestApplication.class, args);
    }
}
```

#### 声明服务实现类

在服务实现类上用@RpcService替代@Service或@Component

```java
@RpcService
public class HelloServiceImpl implements HelloService{
	...
}
```

### 服务调用方（客户端）



