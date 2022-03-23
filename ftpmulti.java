import java.net.*;
import java.io.*;

public class ftpmulti {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
	ftpserver w;

        try {
            serverSocket = new ServerSocket(1370);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1370.");
            System.exit(-1);
        }

        while (listening)
        {
            w = new ftpserver(serverSocket.accept());
            Thread t = new Thread(w);
            t.start();
        }
    }
}
