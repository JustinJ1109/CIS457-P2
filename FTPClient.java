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

	private Socket ControlSocket;

	private static boolean isConnected = false;

	//TODO: remove main from this class. Create separate functions
    public static void main(String argv[]) throws Exception 
    { 
		String sentence; 
		String modifiedSentence; 
		boolean isOpen = true;
		boolean notEnd = true;
		int port1= 1371;
		int port;
		boolean clientgo = true;
			
		System.out.println("Welcome to the simple FTP App   \n     Commands  \nconnect servername port# connects to a specified server \nlist: lists files on server \nget: fileName.txt downloads that text file to your current directory \nstor: fileName.txt Stores the file on the server \nclose terminates the connection to the server");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
			sentence = inFromUser.readLine();
			StringTokenizer tokens = new StringTokenizer(sentence);


		if(sentence.startsWith("connect")){
		String serverName = tokens.nextToken(); // pass the connect command
		serverName = tokens.nextToken();
		port1 = Integer.parseInt(tokens.nextToken());
			System.out.println("You are connected to " + serverName);
			Socket ControlSocket= new Socket(serverName, port1);
			while(isOpen && clientgo)
			{      
			DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
			DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
			sentence = inFromUser.readLine();
		
			port = port1;

			
			if(sentence.equals("list:")) {
				
			port = port +2;
		// System.out.println(port);
			ServerSocket welcomeData = new ServerSocket(port);


			System.out.println("\n \n \nThe files on this server are:");
		outToServer.writeBytes (port + " " + sentence + " " + '\n');

			Socket dataSocket =welcomeData.accept(); 
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
				while(notEnd) {


					modifiedSentence = inData.readUTF();
					if(modifiedSentence.equals("eof"))
						break; 
				System.out.println("	" + modifiedSentence);
				}

			welcomeData.close();
			dataSocket.close();
			System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");

			}
			//if retr command
			else if(sentence.startsWith("get: "))
			{
				StringTokenizer tok = new StringTokenizer(sentence);
				tok.nextToken();
				String fileName = tok.nextToken();

				port += 2;

				outToServer.writeBytes(port + " " + sentence + '\n');
				ServerSocket welcomeData = new ServerSocket(port);
				Socket dataSocket = welcomeData.accept();
				BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

				String read = inData.readLine();
				if (read.equals("550")){
					System.out.println("550 Cannot find file");
				}
				if (read.equals("200 OK")){
					System.out.println("200 OK");
					File file = new File(fileName);
					OutputStream out = new FileOutputStream(file);
					out.write(inData.read());
					System.out.println("downloaded: " + file);
					out.close();
				}
				inData.close();
				welcomeData.close();
				dataSocket.close();

				System.out.println("\nWhat would you like to do next: \n get: file.txt ||stor: file.txt  || close");
			}
			//
			else if(sentence.startsWith("stor: "))
			{
				StringTokenizer tok = new StringTokenizer(sentence);
				tok.nextToken();
				String fileName = tok.nextToken();
				port += 2;

				outToServer.writeBytes(port + " " + sentence + '\n');
				ServerSocket socket = new ServerSocket(port);
				Socket dataSocket = socket.accept();

				BufferedReader inData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));


				String read = inData.readLine();
				if (read.equals("200 OK")) {

					File folder = new File(System.getProperty("user.dir"));
					String[] files = folder.list();

					boolean found = false;
					for (String file : files) {
						if (file.equals(fileName)) {
							found = true;
							System.out.println("200 OK");
							outToServer.writeBytes("200 OK");
							outToServer.writeBytes("\n");
							FileInputStream fis = new FileInputStream(fileName);

							byte[] buffer = new byte[1024];
							int bytes = 0;

							while(( bytes = fis.read(buffer)) != -1) {
								outToServer.write(buffer, 0, bytes);
							}

							fis.close();
						}
					}
					if (!found) {
						System.out.println("Could not find file in client directory");
						outToServer.writeBytes("550");
						outToServer.writeBytes("\n");
					}
				}
				//if file exists on server
				if (read.equals("550")){
					System.out.println("550 File already exists on server");
				}
				socket.close();
				dataSocket.close();
				System.out.println("\nWhat would you like to do next: \n get: file.txt ||stor: file.txt  || close");
			}

		else{
			if(sentence.equals("close"))
			{
			clientgo = false;
			return;
				}
			System.out.print("No server exists with that name or server not listening on that port try again");
				}
			}  
		} 
	}

	public void connectToServer() {
		//TODO: set port1
		int port1 = port + 1;

		try {
			ControlSocket = new Socket(serverHostName, port1);
		}
		catch (Exception e) {
			System.out.println("Unable to connect to " + serverHostName);
		}
		System.out.println("You are connected to " + serverHostName);

		isConnected = true;
	}

	public void disconnectFromServer() {
		try {
			ControlSocket.close();
		}
		catch (Exception e) {
			System.out.println("Unable to disconnect from server:\n");
			e.printStackTrace();
		}
	}

	public void searchFor(String keyword) {
		System.out.println("ServerHostName: " + serverHostName);
		System.out.println("Port: " + port);
		System.out.println("UserName: " + userName);
		System.out.println("HostName: " + hostName);
	}

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
}
