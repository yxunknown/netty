/**
 * TimeClient.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/11/18 21:22
 */
package com.mevur.timeserver.nio;

public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (null != args && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                //port = default value
            }
        }
        new Thread(new TimeClientHandler("127.0.0.1", port), "NIO-TIME-CLIENT-001").start();
    }
}
