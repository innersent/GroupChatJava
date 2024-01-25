package network;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client{
	
	// colors
	 public static final String ANSI_RESET = "\u001B[0m";
	 public static final String ANSI_BLACK = "\u001B[30m";
	 public static final String ANSI_RED = "\u001B[31m";
	 public static final String ANSI_GREEN = "\u001B[32m";
	 public static final String ANSI_YELLOW = "\u001B[33m";
	 public static final String ANSI_BLUE = "\u001B[34m";
	 public static final String ANSI_PURPLE = "\u001B[35m";
	 public static final String ANSI_CYAN = "\u001B[36m";
	 public static final String ANSI_WHITE = "\u001B[37m";
	
	private Socket socket; 
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.username = username;
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendMessage() {
		try {
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner sc = new Scanner(System.in);
			while(socket.isConnected()) {
				String messageToSend = sc.nextLine();
				bufferedWriter.write(username + ": " + messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String msgFromGroupChat;
				
				while(socket.isConnected()) {
					try {
						msgFromGroupChat = bufferedReader.readLine();
						System.out.println(msgFromGroupChat);
					} catch (IOException e) {
						closeEverything(socket, bufferedReader, bufferedWriter);
					}
				}
			}
		});
	}
	
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
			}
			if(bufferedWriter != null) {
				bufferedWriter.close();
			}
			if(socket != null) {
				socket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter your username for the group chat>>> ");
		String username = scanner.nextLine();
		Socket socket = new Socket("localhost", 1234); // порты пока закрыты(ебучий МТС firewall)
		Client client = new Client(socket, username);
		client.listenForMessage();
		client.sendMessage();
		
	}
}
