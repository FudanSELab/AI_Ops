package hello.util;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketServer {

    public static void main(String[] args) throws IOException {
        InetAddress addr = InetAddress.getByName("10.141.212.140");
        int PORT = 9999;
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(addr, PORT), 30000);
            socket.setSendBufferSize(100);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            int i = 0;
            while (true){
                System.out.println("clinet sent -- hello *** " + i++);
                out.write("weewewew"+ i+ "\n");
                out.write( "\n");
//
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                pw.println("dfdfdfdf");
                os.close();
                pw.close();

                out.flush();
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            socket.close();
        }
    }
}
