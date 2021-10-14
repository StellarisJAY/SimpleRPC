package com.jay.rpc.util;

import com.jay.rpc.annotation.RpcService;
import com.jay.rpc.handler.ServiceMapper;

import java.io.File;
import java.net.URL;

/**
 * <p>
 * RPC服务实现类扫描
 * </p>
 *
 * @author Jay
 * @date 2021/10/14
 **/
public class ServiceScanner {

    public static void doScan(String basePackage){
        ClassLoader classLoader = ServiceMapper.class.getClassLoader();
        // 转换成classpath路径
        String path = basePackage.replace(".", "/");
        // 获取路径下所有资源
        URL resource = classLoader.getResource(path);

        File file;
        // 资源存在且是可以转换成File
        if(resource != null && (file = new File(resource.getFile())).exists()){
            // 列出路径下所有文件
            File[] files = file.listFiles();
            if(files == null || files.length == 0){
                return;
            }
            for(File f : files){
                // 遍历到目录，递归扫描目录内文件
                if(f.isDirectory()){
                    doScan(basePackage + "." + f.getName());
                }
                // 类文件
                else if(f.getName().endsWith(".class")){
                    // 获取全限类名
                   String filename = f.getName();
                   String className = filename.substring(0, filename.lastIndexOf(".class"));
                   className = basePackage + "." + className;

                    try {
                        // 获取类
                        Class<?> clazz = classLoader.loadClass(className);
                        // 获取RpcService注解
                        RpcService annotation = clazz.getDeclaredAnnotation(RpcService.class);
                        // 注解存在
                        if(annotation != null){
                            Class<?> implInterface = clazz.getInterfaces()[0];
                            ServiceMapper.putServiceClass(implInterface.getName(), clazz);
                        }
                    } catch (ClassNotFoundException ignored) {
                        // 类不存在
                    }
                }
            }
        }
    }
}
