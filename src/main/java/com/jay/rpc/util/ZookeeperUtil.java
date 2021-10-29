package com.jay.rpc.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/28
 **/
@Component
public class ZookeeperUtil {
    /**
     * Zookeeper 实例
     */
    private ZooKeeper zooKeeper;

    private static final String PATH_PREFIX = "/rpc/services/";

    @Value("${rpc.service.discovery.zk.hosts}")
    private String zkHosts;
    @Value("${rpc.service.discovery.zk.session-timeout}")
    private int sessionTimeout = 4000;

    /**
     * 获取Zookeeper连接
     * @return Zookeeper
     * @throws IOException IOException
     * @throws InterruptedException InterruptedException
     */
    public ZooKeeper connect() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(zkHosts, sessionTimeout, (event)->{
            if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        this.zooKeeper = zooKeeper;
        return this.zooKeeper;
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
}
