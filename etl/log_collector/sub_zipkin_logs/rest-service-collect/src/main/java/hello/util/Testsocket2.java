package hello.util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Testsocket2 {

    public static void sendTCP(String sendStr){
        int port = 8919;
        try {
            ServerSocket server = new ServerSocket(port);
            Socket client = server.accept();

            System.out.println(client.getInetAddress() + "已建立连接！");
            // 输入流
            InputStream is = client.getInputStream();
            BufferedReader bri = new BufferedReader(new InputStreamReader(is));
            // 输出流
            OutputStream os = client.getOutputStream();

            PrintWriter pw = new PrintWriter(os);
            // PrintWriter把数据写到目的地
            pw.print(sendStr);
            //关闭资源
            is.close();
            bri.close();
            os.close();
            pw.close();
            client.close();
            server.close();
            System.out.println("send success! The length:" + sendStr.length());
        } catch (Exception e) {
            System.out.println("connection exit!");
            System.out.println();
        } finally {

        }
    }

    public static void main(String args[]) {
        sendTCP("Hi,hello world! How about you?");
    }

}
