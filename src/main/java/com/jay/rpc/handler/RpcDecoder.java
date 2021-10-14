package com.jay.rpc.handler;

import com.jay.rpc.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> objectClass;
    private static final int MIN_BUFFER_SIZE = 4;

    public RpcDecoder(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 报文大小是否有4字节
        if(byteBuf.readableBytes() < MIN_BUFFER_SIZE){
            return ;
        }

        byteBuf.markReaderIndex();
        // 读前四个字节的int，作为数据部分长度
        int dataLength = byteBuf.readInt();
        // buf剩余部分大于等于数据长度
        if(dataLength > 0 && byteBuf.readableBytes() >= dataLength){
            byte[] bytes = new byte[dataLength];
            byteBuf.readBytes(bytes);

            // 反序列化数据部分
            Object request = SerializationUtil.deserialize(bytes, objectClass);
            list.add(request);
        }
    }
}
