package com.info.sperber.gerstner.chat.clients;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame {
	
	private final JFrame frame;
	private JLabel emailLabel;
	
	public LoginFrame(final HttpClient httpClient){
    	frame = new JFrame("Bitte gib deine Logindaten ein");
    	JPanel pane = new JPanel(new BorderLayout());
    	
    	JPanel userDataPanel = new JPanel(new GridLayout(2, 2));
    	emailLabel = new JLabel("Email:");
    	JLabel passwordLabel = new JLabel("Password:");
    	final JTextField emailField = new JTextField();
    	final JPasswordField passwordField = new JPasswordField(20);
    	userDataPanel.add(emailLabel);
    	userDataPanel.add(emailField);
    	userDataPanel.add(passwordLabel);
    	userDataPanel.add(passwordField);
    	
    	JPanel buttonPanel = new JPanel();
		JButton loginButton = new JButton("Anmelden");
		loginButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String email = emailField.getText();
				char[] password = passwordField.getPassword();
				httpClient.authenticateUser(email, password);
				Arrays.fill(password, '0');
			}
		});
		JButton registerButton = new JButton("Registrieren");
		registerButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO show registration form 
			}
		});
		buttonPanel.add(loginButton);
		buttonPanel.add(registerButton);
		
		pane.add(buttonPanel, BorderLayout.SOUTH);
		pane.add(userDataPanel, BorderLayout.CENTER);
		pane.add(new JLabel("Melde dich an oder registriere dich"), BorderLayout.NORTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.getRootPane().setDefaultButton(loginButton);
		frame.pack();
	}
	
	public void show(){
		frame.setVisible(true);
		emailLabel.requestFocus();
	}
	
	public void hide(){
		frame.setVisible(false);
	}
	
	public void showIncorrectEmailPasswordCombinationPopup() {
		JOptionPane.showMessageDialog(null,
			    "Die eingegebene Emailadresse und das Passwort passen nicht zusammen. Bitte versuche es erneut.",
			    "Fehler beim Login",
			    JOptionPane.ERROR_MESSAGE);
	}
}
