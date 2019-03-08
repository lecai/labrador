package com.threathunter.labrador.rpc.client;

import com.threathunter.labrador.rpc.serialize.IRpcSerialize;
import com.threathunter.labrador.rpc.serialize.RpcDecoder;
import com.threathunter.labrador.rpc.serialize.RpcEncoder;
import com.threathunter.labrador.rpc.server.RpcRequest;
import com.threathunter.labrador.rpc.server.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC 客户端（用于发送 RPC 请求）
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final String host;
    private final int port;
    private final IRpcSerialize rpcSerialize;

    private RpcResponse response;

    public RpcClient(String host, int port, IRpcSerialize rpcSerialize) {
        this.host = host;
        this.port = port;
        this.rpcSerialize = rpcSerialize;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("api caught exception", cause);
        ctx.close();
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class, rpcSerialize));// 编码 RPC 请求
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                    pipeline.addLast(new RpcDecoder(RpcResponse.class, rpcSerialize));// 解码 RPC 响应
                    pipeline.addLast(RpcClient.this); // 处理 RPC 响应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

}
