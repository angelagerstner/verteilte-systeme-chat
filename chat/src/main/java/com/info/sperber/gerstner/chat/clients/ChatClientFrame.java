package com.info.sperber.gerstner.chat.clients;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.info.sperber.gerstner.chat.Message;

public class ChatClientFrame {

	private JTextArea chatText = null;
	private JTextField chatLine = null;
	private JFrame mainframe = null;
	private JButton sendButton = null;
	
	public ChatClientFrame(final HttpClient client){
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(20, 40);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		JScrollPane chatTextPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel sendBar = new JPanel(new FlowLayout());
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String message = chatLine.getText();
				if (!"".equals(message)){
					boolean sent = client.sendMessage(message);
					if (sent)
						chatLine.setText("");
				}
			}
		});
		chatLine = new JTextField(40);
		final JComboBox<ChatMessagePeriod> fromDateComboBox = new JComboBox<ChatMessagePeriod>();
		for (ChatMessagePeriod period : ChatMessagePeriod.values()){
			fromDateComboBox.addItem(period);
		}
		fromDateComboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ChatMessagePeriod period = (ChatMessagePeriod) fromDateComboBox.getSelectedItem();
				client.setChatMessagePeriod(period);
			}
		});
		sendBar.add(chatLine);
		sendBar.add(sendButton);
		sendBar.add(fromDateComboBox);
		chatPane.add(sendBar, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		
		mainframe = new JFrame("Angemeldet als: " + client.getUser().getUserName() + " auf " + client.getServer().getHost() + " und Port " + client.getServer().getPort());
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setContentPane(chatPane);
		mainframe.getRootPane().setDefaultButton(sendButton);
		mainframe.pack();
		mainframe.setVisible(true);
		chatLine.requestFocus();
	}
	
	public synchronized void showMessage(Message message){
		String output = String.format("%s: %s\n", message.getSender().getUserName(), message.getMessage());
		chatText.append(output);
	}

	public void showMessages(LinkedList<Message> messages) {
		chatText.setText("");
		for (Message msg : messages){
			showMessage(msg);
		}
	}
	
}
