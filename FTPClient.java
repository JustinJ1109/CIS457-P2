import java.io.*; 
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/***********************************************
 * this a TCP Client side that can list and get files from the server directory, it can not stor
 * files to the serer directory
 *
 * @authors Cole Blunt, Noah Myers, Prakash Lingden, Brennan Luttrell, Justin Jahlas
 * @version 3.15.2022
 */
class FTPClient {

	private String serverHostName, userName, hostName, speed;
	private int port;
	private final int controlPort = 1370;
	private Socket ControlSocket;

	private ArrayList<String> tableData;


	private DataOutputStream toServer;

	/* Controller calls this to initiate the connection */
	public boolean doConnection() {
		port = controlPort;

		// TODO: need error checking to make sure hostname and port are valid?
		try {
			System.out.println("Attemping to connect to host: " + serverHostName + " at port " + controlPort);
			ControlSocket = new Socket(serverHostName, controlPort);
			System.out.println("You are connected to " + serverHostName);

			toServer = new DataOutputStream(ControlSocket.getOutputStream());
			toServer.writeUTF(hostName + " " + port + " " + userName + " " + speed);
			System.out.println("Sending " + hostName + " " + port + " " + userName + " " + speed + " to server");
		
			System.out.println("Sending bytes to server");
			FileInputStream file = new FileInputStream("filelist.xml");
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while ((bytes = file.read(buffer)) != -1) {
				System.out.println(bytes + " bytes sent");
				toServer.write(buffer, 0, bytes);
			}
			file.close();
			toServer.flush();
			System.out.println("File sent");
		}
		catch (Exception e) {
			System.out.println("Unable to connect to host: " + serverHostName + " on port " + controlPort);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/* Call me on close (maybe not needed) */
	public void disconnectFromServer() {
		try {
			System.out.println("Control Socket port : " + ControlSocket.getPort());
			String sentence = "close:";
			System.out.println("at client side");
			port += 2; 
			System.out.println("Data port " + port);
			ServerSocket welcomeData = new ServerSocket(port);

			toServer.writeUTF(port + " " + sentence + " " + hostName);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			try {
				String file = inData.readUTF();
				System.out.println("Closing server. Code: " + file);
				
			}
			catch (Exception e) {
				System.out.println("Could not close server");
				e.printStackTrace();
			}
			inData.close();
			toServer.close();
			dataSocket.close();
			welcomeData.close();
			ControlSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendBytes(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		int bytes = 0;

		while((bytes = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

  /* Get new data table from somewhere based on keyword (maybe doesnt belong here) */
	public void searchFor(String keyword) {
		try {
			System.out.println("Control Socket port : " + ControlSocket.getPort());
			String sentence = "search:";
			System.out.println("at client side");
			port += 2; 
			System.out.println("Data port " + port);
			ServerSocket welcomeData = new ServerSocket(port);

			toServer.writeUTF(port + " " + sentence + " " + keyword);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			try {
				tableData = new ArrayList<>();
				while(true) {
					String file = inData.readUTF();
					System.out.println("received " + file);

					tableData.add(file);
				}
			}
			catch (Exception e) {
				inData.close();
				dataSocket.close();
				welcomeData.close();
			}

		
			System.out.println("All receieved:" + tableData.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Move store function here
		Controller calls this and provides a filename
	 */
	public boolean doStore(String filename) {
		System.out.println("Storing " + filename);
		try {
			// int port1 = port + 2;
			String curDir = System.getProperty("user.dir");
			String filePath = curDir + "/" + filename;
			FileInputStream fileInput = null;
			boolean fileExists = true;

			ServerSocket welcomeData = new ServerSocket(port);
			// outToServer.writeBytes(port + " " + sentence + " " + '\n');
			Socket dataSocket = welcomeData.accept();
			DataOutputStream dataStream = new DataOutputStream(dataSocket.getOutputStream());
			try {
				fileInput = new FileInputStream(filePath);
			} 
			catch (FileNotFoundException e) {
				fileExists = false;
			}

			if(!fileExists) {
				System.out.println("file not found at " + filePath);
			}
			else {
				System.out.println("\nUploading file from server: ");
				sendBytes(fileInput, dataStream);
				fileInput.close();
			}

			System.out.println("\n");
			welcomeData.close();
			dataSocket.close();
		}
		catch(Exception e) {
			System.out.println("Unable to store: " + filename + " on server: " + serverHostName);
			e.printStackTrace();
		}
		return true;
	}

	public boolean doList() {
		System.out.println("Listing");
		String modifiedSentence;
		try{
			DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
			String sentence = "list:";
			port = port + 2;
		// System.out.println(port);
			ServerSocket welcomeData = new ServerSocket(port);
			Boolean notEnd = true;
			System.out.println("\n \n \nThe files on this server are:");
			outToServer.writeBytes (sentence + " " + port);
			System.out.print(welcomeData);

			Socket dataSocket = welcomeData.accept();
			System.out.print("here 1");
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			
			tableData = new ArrayList<>(0);
			while(notEnd) {
				modifiedSentence = inData.readUTF();
				tableData.add(modifiedSentence);
				if(modifiedSentence.equals("eof"))
					break; 
				System.out.println("	" + modifiedSentence);
			}
			System.out.println("\nWhat would you like to do next: \n get: file.txt ||stor: file.txt  || close");
			welcomeData.close();
			dataSocket.close();
		}
		catch (Exception e) {
			System.out.println("Unable to list files in server: " + serverHostName + " on port " + port);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean doGet(String[] fileToGet) {
		System.out.println("Getting " + fileToGet[2]);
		try {
			System.out.println("Control Socket port : " + ControlSocket.getPort());
			String sentence = "get:";
			System.out.println("at client side");
			port += 2; 
			System.out.println("Data port " + port);
			ServerSocket welcomeData = new ServerSocket(port);

			toServer.writeUTF(port + " " + sentence + " " + fileToGet[0] + " " + fileToGet[1] + fileToGet[2]);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			
			//TODO: read in file from server and download (or put in xml?)
			File xmlFile = new File("C:\\filelist.xml");
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);
			Element documentElement = document.getDocumentElement();
			Element textNode = document.createElement("name");
			textNode.setTextContent("Rose");
			Element textNode1 = document.createElement("description");
			textNode1.setTextContent("Delhi");
			Element nodeElement = document.createElement("file");
			nodeElement.appendChild(textNode);
			nodeElement.appendChild(textNode1);
			documentElement.appendChild(nodeElement);
			document.replaceChild(documentElement, documentElement);
			Transformer tFormer = TransformerFactory.newInstance().newTransformer();
			tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
			Source source = new DOMSource(document);
			Result result = new StreamResult(xmlFile);
			tFormer.transform(source, result);


			inData.close();
			dataSocket.close();
			welcomeData.close();
			System.out.println("All receieved:" + tableData.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	} 

	

	/* getters and setters as needed */ 
	public void setServerHostName(String name) {
		serverHostName = name;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getServerHostName() {
		return serverHostName;
	}

	public int getPort() {
		return port;
	}

	public ArrayList<String> getTableData() {
		return tableData;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}
}
