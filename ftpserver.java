import java.io.*; 
import java.net.*;
import java.util.*;

public class ftpserver extends Thread{ 
    private Socket connectionSocket;

    int port;
    InetAddress clientName;
    int count=1;

    private ArrayList<File> fileList = new ArrayList<>(0);
    private ArrayList<UserData> userList = new ArrayList<>(0);

    private DataOutputStream outToClient;
    private DataInputStream inFromClient;

    boolean welcome;
    
    public ftpserver(Socket connectionSocket) throws Exception {
        this.connectionSocket = connectionSocket;
        outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
        inFromClient = new DataInputStream(this.connectionSocket.getInputStream());
        welcome = true;
    }

    /* First time user connects */
    private void connectUser(String userInfo) {
        // get username, host, and speed from user stream
        // add user to list of users and get their files
        StringTokenizer tokenizer = new StringTokenizer(userInfo);


        String hostName = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        String userName = tokenizer.nextToken();
        String speed = tokenizer.nextToken();

        UserData user = new UserData(userInfo, hostName, speed);

        userList.add(user);

        

    }

    public void run() 
    {
        if(count==1)
            System.out.println("User connected" + connectionSocket.getInetAddress());
        count++;

        try {
            if (welcome) {
                connectUser(this.inFromClient.readUTF());
            }
            else {
                processRequest();
            }            
        } catch (Exception e) {
            System.out.println(e);
        }
	}
	
	private void processRequest() throws Exception {
            String fromClient;
            String clientCommand;
            String frstln;
                    
            while(true)
            {        
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
                    String fileName = tokens.nextToken();
                    String filePath = curDir + "/" + fileName;
                    File server = new File(filePath);
                    server.createNewFile();
                    OutputStream outStream = new FileOutputStream(filePath);
                    boolean fileExists = false;

                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    try {
                        byte[] buffer = new byte[1024];
                        int bytes = 0;

                        bytes = inData.read(buffer);
                        do {
                            outStream.write(buffer, 0, bytes);
                        }
                        while((bytes = inData.read(buffer)) != -1);
                        fileExists = true;
                    }
                    catch(Exception E){
                        System.out.println("Failed to receive file.");
                    }
                    if(!fileExists) {
                        //File does not exist, do not store
                    }
                    else{
                        outStream.close();
                    }
                    dataSocket.close();
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

                if(clientCommand.equals("search:")) {

                }
            }
        }
    }

	

