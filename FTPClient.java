import java.io.*; 
import java.net.*;
import java.util.*;

/***********************************************
 * this a TCP Client side that can list and get files from the server directory, it can not stor
 * files to the serer directory
 *
 * @authors Cole Blunt, Noah Meyers, Prakash Lingden, Brennan Luttrell, Justin Jahlas
 * @version 3.15.2022
 */
class FTPClient {

	private String serverHostName, userName, hostName, speed;
	private int port;
	private final int controlPort = 1370;
	private Socket ControlSocket;

	private ArrayList<String> tableData;

	private static boolean isConnected = false;

	private DataOutputStream toServer;

	/* Controller calls this to initiate the connection */
	public boolean doConnection() {
		port = controlPort;

		// TODO: need error checking to make sure hostname and port are valid?
		try {
			System.out.println("Attemping to connect to host: " + serverHostName + " at port " + controlPort);
			ControlSocket = new Socket(serverHostName, controlPort);
			System.out.println("You are connected to " + serverHostName);
			isConnected = true;

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
			ControlSocket.close();
		}
		catch (Exception e) {
			System.out.println("Unable to disconnect from server:\n");
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
			port += 2; 
			System.out.println("Data port " + port);
			ServerSocket welcomeData = new ServerSocket(port);
			boolean notEnd = true;

			toServer.writeUTF(port + " " + sentence);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			System.out.println("received " + inData.readUTF());
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

	public boolean doGet(String filename) {
		System.out.println("Getting " + filename);
		try{
				ServerSocket welcomeData = new ServerSocket(port);
				Socket dataSocket = welcomeData.accept();
				BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

				String read = inData.readLine();
				if (read.equals("550")){
					System.out.println("550 Cannot find file");
				}
				if (read.equals("200 OK")){
					System.out.println("200 OK");
					File file = new File(filename);
					OutputStream out = new FileOutputStream(file);
					out.write(inData.read());
					System.out.println("downloaded: " + file);
					out.close();
					inData.close();
					welcomeData.close();
					dataSocket.close();
				}
			}
			catch (Exception e) {
				System.out.println("Unable to get file: " + filename + " on port " + port);
				e.printStackTrace();
			}

		System.out.println("\nWhat would you like to do next: \n get: file.txt ||stor: file.txt  || close");
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

	public boolean getIsConnected() {
		return isConnected;
	}

	public ArrayList<String> getTableData() {
		return tableData;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}
}
