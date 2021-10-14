package com.jay.rpc.handler;

import com.jay.rpc.entity.RpcRequest;
import com.jay.rpc.entity.RpcResponse;
import com.jay.rpc.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/10/13
 **/
public class RpcEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object out, ByteBuf byteBuf) throws Exception {
        if(out instanceof RpcResponse){
            RpcResponse response = (RpcResponse)out;
            // 序列化
            byte[] bytes = SerializationUtil.serialize(response);
            // 写入数据部分大小
            byteBuf.writeInt(bytes.length);
            // 写入数据部分
            byteBuf.writeBytes(bytes);
        }
        else if(out instanceof RpcRequest){
            RpcRequest request = (RpcRequest)out;
            // 序列化
            byte[] bytes = SerializationUtil.serialize(request);
            // 写入数据部分大小
            byteBuf.writeInt(bytes.length);
            // 写入数据部分
            byteBuf.writeBytes(bytes);
        }
    }
}
