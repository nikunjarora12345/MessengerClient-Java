package com.kaminari.messengerclient;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client(String host) {
		super("Messenger");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		
		setSize(800, 600);
		setVisible(true);
	}
	
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n Client ended the connection!!!");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			close();
		}
	}
	
	//connect to the server
	private void connectToServer() throws IOException{
		showMessage("Connecting to the server... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to : " + connection.getInetAddress().getHostName());
	}
	
	//setting up the streams to send and receive messages
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
	}
	
	//while you are chatting
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n idk wtf the server sent!!!");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//close all connections after chatting
		private void close(){
			showMessage("\n Closing connections... \n");
			ableToType(false);
			try{
				output.close();
				input.close();
				connection.close();
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
		//sends a message to the server
		private void sendMessage(String message){
			try{
				output.writeObject("CLIENT - " + message);
				output.flush();
				showMessage("\n CLIENT - " + message);
			}catch(IOException ioException){
				chatWindow.append("\n ERROR : wtf did you type dude!!!");
			}
		}
		
		//updates chat window
		private void showMessage(final String text){
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					chatWindow.append(text);
				}
			});
		}
		
		//gives the user permission to type
		private void ableToType(final boolean tof){
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					userText.setEditable(tof);
				}
			});
		}
}
