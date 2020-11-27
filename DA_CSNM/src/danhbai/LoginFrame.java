package danhbai;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.*;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {

	private JPanel contentPane;
	private JTextField host_t, name_t;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(350, 250);
		setLocationRelativeTo(null);
		contentPane = new JPanel(new GridBagLayout());
		contentPane.setBounds(0, 0, 350, 220);
		setContentPane(contentPane);

		Panel p1=new Panel(new GridLayout(2,2,0,20));
		Panel p2=new Panel(new GridLayout(2,1,0,30));
		Panel p3=new Panel(new FlowLayout());
		JLabel host = new JLabel("Server IP");		
		JLabel name = new JLabel("Username");		
		host_t = new JTextField("localhost");
		host_t.setColumns(10);
		name_t = new JTextField();
		name_t.setColumns(10);
		JButton login_btn = new JButton("Log in");
		this.getRootPane().setDefaultButton(login_btn);
		login_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = name_t.getText();
				String host = host_t.getText();
				if(!host.equals("") && !name.equals("")){
					try{
						Socket soc  = new Socket(host,3000);
						DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
						dos.writeUTF("login-"+name);
						Thread t;
						t = new Thread(new LobbyFrame(soc,name));
						t.start();
						dispose();
					}
					catch(Exception e){
						JOptionPane.showMessageDialog(
								new JFrame(), "Wrong IP address!!");
					}
				}
				else {
					JOptionPane.showMessageDialog(
							new JFrame(), "Please enter host and username");					
				}
				
			}
		});
		p1.add(host);
		p1.add(host_t);
		p1.add(name);
		p1.add(name_t);
		p2.add(p1);
		p3.add(login_btn);
		p2.add(p3);
		add(p2, new GridBagConstraints());
		setVisible(true);
	}
}
