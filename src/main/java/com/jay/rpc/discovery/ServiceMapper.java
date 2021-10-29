package com.jay.rpc.discovery;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/29
 **/
@Component
public class ServiceMapper {
    /**
     * 记录 接口和实现类的映射
     * HashMap初始大小设为256来避免扩容
     */
    private HashMap<Class<?>, Object> map = new HashMap<>(256);

    /**
     * 获取服务接口的实现Bean
     * @param service 接口
     * @param bean bean
     */
    public void put(Class<?> service, Object bean){
        if(map.containsKey(service)){
            throw new RuntimeException("RPC接口重复实现，接口：" + service);
        }
        map.put(service, bean);
    }

    /**
     * 获取服务实现类
     * @param service 服务接口
     * @return bean
     */
    public Object getServiceImpl(Class<?> service){
        return map.get(service);
    }
}
