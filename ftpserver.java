import java.io.*; 
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ftpserver extends Thread{ 
    
    private Socket connectionSocket;
    private Socket dataSocket;

    int port;
    InetAddress clientName;
    int count=1;

    protected static Vector<FileData> fileList = new Vector<FileData>();
    protected static Vector<UserData> userList = new Vector <UserData>();

    private DataOutputStream outToClient;
    private DataInputStream inFromClient;

    private DataInputStream dataInFromClient;

    boolean welcome;
    private boolean running;
    
    public ftpserver(Socket connectionSocket) throws Exception {
        this.connectionSocket = connectionSocket;
        outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
        inFromClient = new DataInputStream(this.connectionSocket.getInputStream());

        welcome = true;
        running = true;
        System.out.println("TID: " + this.getId() +" Connection created " + connectionSocket.getInetAddress() + " on port " + connectionSocket.getLocalPort() + " to port " + connectionSocket.getPort());
    }

    public void run() 
    {
        try {
            if (welcome) {
                System.out.println("TID: " + this.getId() +" welcoming user");
                connectUser(inFromClient.readUTF());
            }
            else {
                System.out.println("TID: " + this.getId() +" processing request");
                waitForRequest();
            }       
              
        } catch (Exception e) {
            System.out.println(e);
        }
	}

    /* First time user connects */
    private void connectUser(String userInfo) throws Exception {
        welcome = false;
        System.out.println("TID: " + this.getId() + " in connectUser");
        // get username, host, and speed from user stream
        // add user to list of users and get their files
        StringTokenizer tokenizer = new StringTokenizer(userInfo);

        String hostName = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        String userName = tokenizer.nextToken();
        String speed = tokenizer.nextToken();

        UserData user = new UserData(userInfo, hostName, speed);


        System.out.println("TID: " + this.getId() +" data receieved: " + hostName + " " + port + " " + userName + " " + speed);
        addUser(user);

        inFromClient = new DataInputStream(new BufferedInputStream(this.connectionSocket.getInputStream()));

        File file = getFile();
        ArrayList<FileData> files = parseData(file, user);
        addContent(files);

        System.out.println("Closing inFromClient stream and socket on port " + connectionSocket.getPort());
        inFromClient.close();
        dataSocket.close();
    }
    private File getFile() throws Exception{
        System.out.println("TID: " + this.getId() +" getting file");

        FileOutputStream fos = new FileOutputStream("filelist.xml");
        byte[] fileData = new byte[1024];
        int bytes = 0;
        System.out.println("TID: " + this.getId() +" reading bytes");

        while ((bytes = inFromClient.read(fileData)) != -1) {
            System.out.println("Bytes received: " + bytes);
            fos.write(fileData, 0, bytes);
        }
        System.out.println("done reading");
        fos.close();
        File file = new File("filelist.xml");
        return file;
    }

    private ArrayList<FileData> parseData(File file, UserData user) throws Exception{
        System.out.println("Parsing data");
        ArrayList<FileData> fileList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("file");
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            System.out.println("\nCurrent Element :" + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eTemp = (Element) node;
                System.out.println(eTemp.getElementsByTagName("name").item(0).getTextContent());
                System.out.println(eTemp.getElementsByTagName("description").item(0).getTextContent());
                fileList.add(new FileData(user, eTemp.getElementsByTagName("name").item(0).getTextContent(), eTemp.getElementsByTagName("description").item(0).getTextContent()));
            }
        }
        file.delete();
        return fileList;
    }

    private void addUser(UserData newUser) {
        synchronized (userList) {
            userList.addElement(newUser);
        }
    }

    private void addContent(ArrayList<FileData> newData) {
        synchronized (fileList) {
            fileList.addAll(newData);
        }
    }

    private void waitForRequest() throws Exception {
        String fromClient = this.inFromClient.readUTF();
        processRequest(fromClient);

    }
	
	private void processRequest(String clientCommand) throws Exception {
            String fromClient;
            String frstln;
            System.out.println("in process request");
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
                    DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
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

	

