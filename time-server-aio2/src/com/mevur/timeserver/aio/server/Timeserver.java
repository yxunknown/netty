/**
 * Timeserver.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/13/18 14:30
 */
package com.mevur.timeserver.aio.server;

public class Timeserver {
    public static void main(String[] args) {
        int port = 8080;
        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(timeServerHandler, "AIO-TIME-SERVER-001").start();
    }
}
