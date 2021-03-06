package com.jay.rpc.util;

import com.jay.rpc.config.ZooKeeperCondition;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 *  Zookeeper 工具类
 * </p>
 *
 * @author Jay
 * @date 2021/10/28
 **/
public class ZookeeperUtil {
    /**
     * Zookeeper 实例
     */
    private ZooKeeper zooKeeper;
    private String zkHosts;
    private int sessionTimeout = 4000;
    private Logger LOGGER = LoggerFactory.getLogger(ZookeeperUtil.class);


    public ZookeeperUtil(String zkHosts, int sessionTimeout) throws IOException, InterruptedException {
        this.zkHosts = zkHosts;
        this.sessionTimeout = sessionTimeout;
        this.zooKeeper =  connect();
    }

    /**
     * 获取Zookeeper连接
     * @return Zookeeper
     * @throws IOException IOException
     */
    @PostConstruct
    public ZooKeeper connect() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long startTime = System.currentTimeMillis();
        ZooKeeper zooKeeper = new ZooKeeper(zkHosts, sessionTimeout, (event)->{
            if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                countDownLatch.countDown();
            }
        });
        LOGGER.info("等待ZooKeeper连接建立");
        countDownLatch.await();
        LOGGER.info("ZooKeeper连接成功，用时：{}ms", System.currentTimeMillis() - startTime);
        return zooKeeper;
    }


    /**
     * 判断节点是否存在
     * @param path 路径
     * @return boolean
     * @throws KeeperException ZookeeperException
     * @throws InterruptedException future
     */
    public boolean exists(String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, false) != null;
    }

    /**
     * 创建临时节点
     * @param path 节点路径
     * @param data 节点data
     * @param acls 访问权限
     * @return 节点绝对路径
     * @throws KeeperException zkException
     * @throws InterruptedException createException
     */
    public String createEphemeral(String path, String data, List<ACL> acls) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data.getBytes(), acls, CreateMode.EPHEMERAL);
    }

    /**
     * 创建持久节点
     * @param path 节点路径
     * @param data 节点data
     * @param acls 访问权限
     * @return 节点绝对路径
     * @throws KeeperException zkException
     * @throws InterruptedException createException
     */
    public String createPersistent(String path, String data, List<ACL> acls) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data.getBytes(), acls, CreateMode.PERSISTENT);
    }

    /**
     * 获取连接状态
     * @return boolean
     */
    public boolean checkConnection(){
        return zooKeeper != null && zooKeeper.getState() == ZooKeeper.States.CONNECTED;
    }

    /**
     * 列出某路径下的子节点
     * @param path path
     * @return List
     * @throws KeeperException ZookeeperException
     * @throws InterruptedException Interrupted
     */
    public List<String> listChildren(String path) throws KeeperException, InterruptedException {
        if(!exists(path)){
            return null;
        }
        return zooKeeper.getChildren(path, false);
    }

    /**
     * 获取节点数据
     * @param path 路径
     * @return String
     * @throws KeeperException KeeperException
     * @throws InterruptedException InterruptedException
     */
    public String getData(String path) throws KeeperException, InterruptedException {
        // 节点状态，不返回
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData(path, false, stat);
        return new String(data, StandardCharsets.UTF_8);
    }
}
