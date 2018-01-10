/**
 * MultiplexerTimeServer.class
 * Created in Intelij IDEA
 * <p>
 * 多路转接器时间服务器
 *
 * @author Mevur
 * @date 01/10/18 21:25
 */
package com.mevur.timeserver.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            //打开多路复用器
            selector = Selector.open();
            //打开服务器socket通道
            serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //绑定侦听端口
            //backlog 参数为最大队列数目
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            //注册到Reactor线程的多路复用器上，监听ACCEPT事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is running in port:" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //多路服务器轮询就绪key的时间间隔
                selector.select(1000);
                //多路复用器轮询就绪的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        //处理 I/O 事件
                        handleInput(key);
                    } catch (Exception e) {
                        if (null != key) {
                            key.cancel();
                            if (null != key.channel()) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        //多路复用器关闭后，注册在chanel和pipe上的资源会自动去注册和关闭
        if (null != selector) {
             try {
                 selector.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                //处理新的连接
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                //配置非阻塞方式
                sc.configureBlocking(false);
                //添加新的连接到多路服务器的chanel上
                sc.register(selector, SelectionKey.OP_ACCEPT);
            }
            if (key.isReadable()) {
                //读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                //分配1MB缓存
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //读取请求码流
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    //设置当前的limit为position，position设置为0，方便后续读取
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //将readBuffer的可读字节数组复制到bytes中去
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order:" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                                         new Date(System.currentTimeMillis()).toString() :
                                         "BAD ORDER";
                    //回写数据
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                } else {
                    //读取0数据，忽略
                }

            }
        }
    }
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (null != response && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            //将字节数组复制到缓冲区中
            writeBuffer.put(bytes);
            writeBuffer.flip();
            //将缓冲区中的数据发送出去
            channel.write(writeBuffer);
            //由于socketChanel是异步非阻塞的，因此无法保证write操作能一次将全部数据发送出去，这里会出现半包问题
            //需要进一步解决“写半包”问题
        }
    }
}
