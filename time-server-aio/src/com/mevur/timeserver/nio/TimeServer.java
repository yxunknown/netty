/**
 * TimeServer.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/10/18 19:49
 */
package com.mevur.timeserver.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (null != args && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //port = default value
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(timestamp.toString() + "The time server is running in port: " + port);
            Socket socket = null;
            TimeServerHandlerExecutePool threadPool = new TimeServerHandlerExecutePool(50, 1000);
            while (true) {
                socket = server.accept();
                threadPool.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (null != server) {
                System.out.println("The time server stopped");
                server.close();
            }
            server = null;
        }
    }
}
