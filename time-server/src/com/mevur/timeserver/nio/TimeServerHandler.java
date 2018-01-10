/**
 * TimeServerHandler.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/09/18 21:20
 */
package com.mevur.timeserver.nio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable {
    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;
            while (true) {
                body = in.readLine();
                if (null == body) {
                    break;
                } else {
                    System.out.println("The time server receive order:" + body);
                    currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                                  new Date(System.currentTimeMillis()).toString() :
                                  "BAD ORDER";
                    out.println(currentTime);
                }
            }
        } catch (Exception e) {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (null != out) {
                out.close();
                out = null;
            }
            if (null != this.socket) {
                try {
                    this.socket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                socket = null;
            }
        }
    }
}
