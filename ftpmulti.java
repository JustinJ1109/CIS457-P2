import java.net.*;
import java.io.*;

public class ftpmulti {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        boolean listening = true;
	    ftpserver w;

        try {
            serverSocket = new ServerSocket(1333);

        } catch (IOException e) {
            System.err.println("Could not listen on port: 1370.");
            e.printStackTrace();
            System.exit(-1);
        }

        while (listening)
        {
            
            try {
                w = new ftpserver(serverSocket.accept());
                System.out.println("\nnew thread");
                Thread t = new Thread(w);
                t.start();
            }
            catch (Exception e) {
                System.out.println("Could not start thread");
                e.printStackTrace();
            }
            
        }
    }
}

