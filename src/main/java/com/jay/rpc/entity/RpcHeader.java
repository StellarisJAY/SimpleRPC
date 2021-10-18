package com.jay.rpc.entity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * Rpc协议头
 * </p>
 *
 * @author Jay
 * @date 2021/10/18
 **/
public class RpcHeader {
    /**
     * 2字节，魔数BABE
     */
    public static final short MAGIC = (short)0xBABE;

    private static final AtomicLong idProvider = new AtomicLong(1L);

    public static final byte TYPE_REQUEST = 0;
    public static final byte TYPE_RESPONSE = 1;
    public static final byte TYPE_HEARTBEAT = 2;

    public static final byte STATUS_SEND = 0;
    public static final byte STATUS_RECV = 1;

    public static final int HEADER_SIZE = 16;

    public static long nextId(){
        return idProvider.getAndIncrement();
    }

}
