package com.jay.rpc.handler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * RpcService类映射
 * </p>
 *
 * @author Jay
 * @date 2021/10/14
 **/
public class ServiceMapper {
    private static final ConcurrentHashMap<String, Class<?>> serviceMap = new ConcurrentHashMap<>();


    public static Class<?> getServiceClass(String className){
        return serviceMap.get(className);
    }

    public static void putServiceClass(String className, Class<?> clazz){
        synchronized (serviceMap){
            if(!serviceMap.containsKey(className)){
                serviceMap.put(className, clazz);
            }
        }
    }

    public static void listServices(){
        ConcurrentHashMap.KeySetView<String, Class<?>> keySet = serviceMap.keySet();
        for(String key : keySet){
            System.out.println(key);
        }
    }
}
