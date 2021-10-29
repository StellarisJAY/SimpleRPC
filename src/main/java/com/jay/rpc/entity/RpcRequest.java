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
     * provider class名称
     */
    private Class<?> targetClass;

    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
