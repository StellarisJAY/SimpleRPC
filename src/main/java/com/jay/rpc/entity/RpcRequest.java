package com.jay.rpc.entity;

import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcRequest {
    /**
     * provider 组
     */
    private String group;
    /**
     * provider 名称
     */
    private String providerName;
    /**
     * provider 版本
     */
    private String version;

    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;


}
