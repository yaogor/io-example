package com.example.ioexample.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 监听 8080 端口进来的 TCP 链接
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        while(true){
            //这里会阻塞，直到有一个请求的连接进来
            SocketChannel accept = serverSocketChannel.accept();

            //开启一个新的线程来处理这个请求，然后再while中继续监听这个端口
            SocketHandler socketHandler = new SocketHandler(accept);
            new Thread(socketHandler).start();
        }
    }
}
