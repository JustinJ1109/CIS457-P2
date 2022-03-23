import java.io.*; 
import java.net.*;
import java.util.*;

public class ftpserver extends Thread{ 
    private Socket connectionSocket;
    int port;
    InetAddress clientName;
    int count=1;
    
    public ftpserver(Socket connectionSocket)  {
        this.connectionSocket = connectionSocket;
    }


      public void run() 
        {
                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;

	try {
		processRequest();
		
	} catch (Exception e) {
		System.out.println(e);
	}
	 
	}
	
	
	private void processRequest() throws Exception {
            String fromClient;
            String clientCommand;
            byte[] data;
            String frstln;
                    
            while(true)
            {

                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;
        
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                fromClient = inFromClient.readLine();
            
      		    System.out.println("fromClient: " + fromClient);
                StringTokenizer tokens = new StringTokenizer(fromClient);
            
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();


                  if(clientCommand.equals("list:"))
                  { 
                      String curDir = System.getProperty("user.dir");
       
                      Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                      DataOutputStream  dataOutToClient = 
                      new DataOutputStream(dataSocket.getOutputStream());
                      File dir = new File(curDir);
    
                      String[] children = dir.list();
                      if (children == null) 
                      {

                          System.out.print("List Does Not Exist");
                      } else {
                          for (int i=0; i<children.length; i++)
                          {

                              String filename = children[i];
                              System.out.print(filename);

                              if(filename.endsWith(".txt"))
                                dataOutToClient.writeUTF(children[i]);
                             System.out.println(filename);
                             if(i-1==children.length-2)
                             {
                                 dataOutToClient.writeUTF("eof");
                                 System.out.println("eof");
                             }

                        }

                           dataSocket.close();

                     }

                }

                if(clientCommand.equals("stor: "))
                {
                    String curDir = System.getProperty("user.dir");
                   Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());


                    String fileName = tokens.nextToken();
                    File file = new File(curDir);
                    BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

                    File folder = new File("user.dir");
                    String[] files = folder.list();
                    boolean found = false;
                    //check if file is found on server
                    for (String find: files){
                        if (find.equals(fileName)){
                            found = true;
                            dataOutToClient.writeBytes("550");
                            dataOutToClient.writeBytes("\n");
                        }
                    }

                    if (!found) {
                        dataOutToClient.writeBytes("200 OK");
                        dataOutToClient.writeBytes("\n");
                    }
                    String check = dataIn.readLine();

                        if (check.equals("200 OK") && !found) {
                            OutputStream byteWriter = new FileOutputStream(file);
                            byteWriter.write(dataIn.read());
                            byteWriter.close();
                        }


                    dataOutToClient.close();
                    dataSocket.close();
                    System.out.println("Data Socket closed");
                }


                //if get command
                if(clientCommand.equals("get:"))
                {
                    String curDir = System.getProperty("user.dir");
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

                    String fileName = tokens.nextToken();
                    File folder = new File(curDir);
                    String[] files = folder.list();

                    boolean found = false;
                    for (String file: files){
                        if (file.equals(fileName)){
                            found = true;
                            dataOutToClient.writeBytes("200 OK");
                            dataOutToClient.writeBytes("\n");
                            FileInputStream fis = new FileInputStream(fileName);
                            byte[] buffer = new byte[1024];
                            int bytes = 0;

                            while ((bytes = fis.read(buffer)) != -1){
                                dataOutToClient.write(buffer, 0, bytes);
                            }
                            System.out.println("file was sent");
                            fis.close();
                        }
                    }
                    //if file is not found send 550
                    if (!found){
                        dataOutToClient.writeBytes("550");
                        dataOutToClient.writeBytes("\n");
                    }
                    dataOutToClient.close();
                    dataSocket.close();
                    System.out.println("Data Socket closed");
                }
            }
        }
    }

	

