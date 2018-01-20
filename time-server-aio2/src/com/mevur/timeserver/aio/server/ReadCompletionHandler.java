/**
 * ReadCompletionHandler.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/13/18 15:04
 */
package com.mevur.timeserver.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel channel;
    public static final String CHARSET = "UTF-8";

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        if (null == this.channel) {
            this.channel = channel;
        }
    }
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String reg = new String(body, CHARSET);
            System.out.println("The time section1.server receive order:" + reg);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(reg) ? getTime() :
                                 "BAD ORDER";
            doWrite(currentTime);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String currentTime) {
        if (null != currentTime && 0 < currentTime.trim().length()) {
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();

            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    //出现写半包问题，继续完成剩下内容
                    if (attachment.hasRemaining()) {
                        channel.write(attachment, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getTime() {
        return new Date(System.currentTimeMillis()).toString();
    }
}
