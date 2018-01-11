/**
 * TimeClientHandler.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/11/18 21:26
 */
package com.mevur.timeserver.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandler(String host, int port) {
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;
        try {
            //打开多路复用器
            selector = Selector.open();
            //打开socket通道
            socketChannel = SocketChannel.open();
            //配置socket通道为非阻塞工作方式
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            //TODO doConnect
            //开始连接工作
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop) {
            try {
                //多路复用器轮询就绪key的时间间隔，单位ms
                selector.select(1000);
                //就绪key的集合
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        //TODO handleInput
                        //处理I/O事件
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
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        //多路复用器关闭后，会自动关闭其chanel和pipe上的资源
        if (null != selector) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        //判断是否连接成功
        if (key.isValid()) {
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    //连接成功，注册到多路复用器
                    sc.register(selector, SelectionKey.OP_READ);
                    //发送请求信息
                    doWrite(sc);
                } else {
                    //连接失败
                    System.exit(1);
                }
            }
            if (key.isReadable()) {
                //分配1MB缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //读取数据
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    //读到非空数据
                    //置limit为position，position为0，方便后续读取
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //复制缓冲区数据到bytes数组
                    readBuffer.get(bytes);
                    //将字节数据转码成字符串
                    String body = new String(bytes, "UTF-8");
                    System.out.println("Now is " + body);
                } else if (readBytes < 0) {
                    //关闭链路
                    key.cancel();
                    sc.close();
                } else {
                    //读到0字节，忽略
                }
            }
        }
    }

    private void doConnect() throws IOException {
        //如果直接连接成功，则注册到多路复用器上，发送请求消息，读应答
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            //连接失败
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            System.out.println("Send order 2 server connected");
        }
    }
}
