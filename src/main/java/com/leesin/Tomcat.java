package com.leesin;

import com.leesin.http.Request;
import com.leesin.http.Response;
import com.leesin.http.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @description:
 * @author: Leesin.Dong
 * @date: Created in 2020/3/31 17:15
 * @version: ${VERSION}
 * @modified By:
 */
//netty就像是一个同事支持多写一个网络通信框架
public class Tomcat {
    private int port = 8080;
    private Map<String, Servlet> servletMapping = new HashMap<>();

    private Properties webxml = new Properties();

    private void init(){

        //加载web.xml文件,同时初始化 ServletMapping对象
        try{
            String WEB_INF= this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);
            for (Object k : webxml.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    Servlet obj = (Servlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void  start() {
        init();
        //Netty封装了NIO，Reactor模型，boss worker
        //boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //netty服务
            ServerBootstrap server = new ServerBootstrap();
            //链路式编程
            server.group(bossGroup, workerGroup)
                    //主线程处理类，看到这样的写法底层就是用反射
                    .channel(NioServerSocketChannel.class)
                    //子线程处理类，Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端初始化处理
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            //无锁化串行编程
                            //netty对http协议的封装，顺序有关
                            //HttpResponseEncoder编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //HttpRequestDecoder 解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            client.pipeline().addLast(new TomcatHandler());
                        }
                    })
                    //针对主线程的配置，分配线程最大数量128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //针对子线程的配置，保持长连接
                    .option(ChannelOption.SO_KEEPALIVE, true);

            //启动服务
            ChannelFuture f = server.bind(port).sync();
            System.out.println("tomcat 服务已启动，监听的端口是："+port);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public class TomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                //转交给自己的request实现
                HttpRequest req= (HttpRequest) msg;
                //转交给自己的response实现
                Request request = new Request(ctx, req);
                Response response = new Response(ctx, req);
                //实际业务逻辑
                String uri = request.getUri();
                if (servletMapping.containsKey(uri)) {
                    servletMapping.get(uri).service(request, response);
                } else {
                    response.write("404-Not Found");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

    public static void main(String[] args) {
        new Tomcat().start();
    }
}
