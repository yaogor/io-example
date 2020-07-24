package com.example.ioexample.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));

        //设置为非阻塞状态
        serverSocketChannel.configureBlocking(false);
        //将其注册到 Selector中监听 OP_ACCEPT 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            int readyChannels  = selector.select();
            if(readyChannels == 0 ){
                continue;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable()){
                    //有已经接受的新的到服务端的连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    //有新的连接不代表这个通道有数据
                    //这里将这个新的SocketChannel 注册到Selector 监听OP_READ 事件，等待数据
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    //有数据可读
                    //上面一个if分支注册了 OP_READ事件的SocketChannel
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int num = socketChannel.read(buffer);

                    if (num > 0) {
                        // 处理进来的数据...
                        System.out.println("收到数据：" + new String(buffer.array()).trim());
                        ByteBuffer writeBuffer = ByteBuffer.wrap("返回给客户端的数据...".getBytes());
                        socketChannel.write(writeBuffer);
                    } else if (num == -1) {
                        // -1 代表连接已经关闭
                        socketChannel.close();
                    }

                }
            }
        }
    }
}
