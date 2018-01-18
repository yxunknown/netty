/**
 * TimeClient.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/14/18 15:02
 */
package com.mevur.timserver.aio.client;

public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-TIME-CLIENT-001").start();
    }
}

