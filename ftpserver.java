import java.io.*; 
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ftpserver extends Thread{ 
    
    private Socket connectionSocket;

    int port;
    InetAddress clientName;
    int count=1;

    protected static Vector<FileData> fileList = new Vector<FileData>();
    protected static Vector<UserData> userList = new Vector <UserData>();

    ArrayList<FileData> files;

    private static DataOutputStream outToClient;
    private DataInputStream inFromClient;

    private DataOutputStream dataOutToClient;

    boolean welcome;
    private boolean running;
    
    public ftpserver(Socket connectionSocket) throws Exception {
        this.connectionSocket = connectionSocket;
        outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
        inFromClient = new DataInputStream(this.connectionSocket.getInputStream());

        welcome = true;
        running = true;
        System.out.println("Connection created " + connectionSocket.getInetAddress() + " on port " + connectionSocket.getLocalPort() + " to port " + connectionSocket.getPort());
    }

    public void run() 
    {
        try {
            while(running) {
                if (welcome) {
                    System.out.println("welcoming user");
                    connectUser(inFromClient.readUTF());
                }
                else {
                    System.out.println("processing request");
                    waitForRequest();
                }       
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    /* First time user connects */
    private void connectUser(String userInfo) throws Exception {
        welcome = false;
        // get username, host, and speed from user stream
        // add user to list of users and get their files
        StringTokenizer tokenizer = new StringTokenizer(userInfo);

        String hostName = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        String userName = tokenizer.nextToken();
        String speed = tokenizer.nextToken();

        UserData user = new UserData(userInfo, hostName, speed);

        System.out.println("Data receieved: " + hostName + " " + port + " " + userName + " " + speed);
        addUser(user);

        inFromClient = new DataInputStream(new BufferedInputStream(this.connectionSocket.getInputStream()));

        File file = getFile();
        files = parseData(file, user);
        addContent(files);

        System.out.println("Done connecting");
    }
    private File getFile() throws Exception{

        FileOutputStream fos = new FileOutputStream("temp.xml");
        byte[] fileData = new byte[1024];
        int bytes = 0;

        System.out.println("Bytes received: " + bytes);
        fos.write(fileData, 0, inFromClient.read(fileData));
        
        System.out.println("done reading");
        fos.close();
        File file = new File("temp.xml");
        return file;
    }

    private ArrayList<FileData> parseData(File file, UserData user) throws Exception{
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
        System.out.println("Waiting for req");
        String fromClient = inFromClient.readUTF();
        System.out.println("req received");
        processRequest(fromClient);
    }
	
	private void processRequest(String clientCommand) throws Exception {
        String frstln;    
        System.out.println("fromClient: " + clientCommand);
        StringTokenizer tokens = new StringTokenizer(clientCommand);
    
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

        if(clientCommand.equals("stor:"))
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
            String speed = tokens.nextToken();
            System.out.println(speed);
            String hostName = tokens.nextToken();
            System.out.println(hostName);
            String fileName = tokens.nextToken();
            System.out.println(fileName);

            Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
            dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            File openFile = new File(fileName);

            for (FileData f: fileList) {
                if (f.getFileName().equals(fileName) && f.getUser().getHostName().equals(hostName) && f.getUser().getSpeed().equals(speed)) {
                    //TODO: get file from master file list and send it to client //
                    if (openFile.exists()) {
                        byte[] buffer = new byte[8192];
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
                        int count;
                        while((count = in.read(buffer)) > 0) {
                            dataOutToClient.write(buffer, 0, count);
                        }
                        in.close();
                    }
		        }
            }

            //Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
            //dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            
            try {
                dataOutToClient.close();
                dataSocket.close();
            }
            catch (Exception e) {
                System.out.println("Broke...");
                e.printStackTrace();
            }

            System.out.println("Searching");
        }

        if(clientCommand.equals("search:")) {
            String keyword = tokens.nextToken();
            System.out.println("keyword: " + keyword);

            Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
            dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            
            try {
                giveFiles(keyword, dataOutToClient);
                dataOutToClient.close();
                dataSocket.close();
            }

            catch (Exception e) {
                System.out.println("Broke...");
                e.printStackTrace();
            }

            System.out.println("Searching");
        }

        if(clientCommand.equals("close:")) {
            String hostName = tokens.nextToken();
            Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
            dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            
            for (FileData f: files) {
                System.out.println("Removing " + f.getFileName());
                fileList.remove(f);
            }
            
            try {
                dataOutToClient.writeUTF("SUCCESS");
                inFromClient.close();
                outToClient.close();
                dataOutToClient.close();
                dataSocket.close();
                System.out.println("Disconnected from user " + connectionSocket.getInetAddress());
                connectionSocket.close();
                running = false;
            }
            catch (Exception e) {
                System.out.println("Could not disconnect");
                e.printStackTrace();
            }
        }
    }

    public Vector<FileData> giveFiles(String keyword, DataOutputStream dos) throws Exception{

        Vector<FileData> tempfileList = new Vector<>();

        String output = "";
        for (int i = 0; i < fileList.size(); i++) {
            FileData fileEntry = (FileData) fileList.get(i);
            String description = fileEntry.getDescription();
            
            if (description.contains(keyword)) {
                
                UserData user = fileEntry.getUser();
                output = user.getSpeed() + " " + user.getHostName() + " " + fileEntry.getFileName();
                System.out.println("Sending back: " + output);
                dos.writeUTF(output);
                dos.flush();
            }
        }

        return tempfileList;
    }

}

	

