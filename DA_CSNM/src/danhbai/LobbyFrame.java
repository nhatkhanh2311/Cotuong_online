package danhbai;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LobbyFrame extends JFrame implements Runnable{
	
	ServerCard server;
	Socket soc;
	private String name;
	ArrayList<Room> listrooms = new ArrayList<Room>();
	DataInputStream dis = null;
	DataOutputStream dos = null;
	String[] part = null;
	
	private JPanel contentPane;
	private JTextField find_txt;
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					lobbyFrame frame = new lobbyFrame("localhost", "username");
//					frame.setVisible(true);
//				} catch (Exception e) {
//					System.out.println(e);
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public LobbyFrame(Socket soc, String name) {
		this.name = name;
		this.soc = soc;
		try{
			dis = new DataInputStream(this.soc.getInputStream());
			String receive = dis.readUTF();
			part = receive.split("-");
			dos =  new DataOutputStream(this.soc.getOutputStream());
		}
		catch(Exception e){		}
		this.setFocusable(true);
		setTitle("Welcome "+this.name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(550, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		JList list = new JList(part);
		list.setPreferredSize(new Dimension(380,230));
		JButton btnNewRoom = new JButton("New Room");
		btnNewRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					dos.writeUTF("new-"+name);
					Thread t;
					t = new Thread(new PlayRoom(soc,name,true));
					t.start();
					dispose();
				}catch(Exception e){e.printStackTrace();}
			}
		});
		
		JButton btnJoinRoom = new JButton("Join room");
		btnJoinRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String jr = list.getSelectedValue().toString();
				try{
					dos.writeUTF("join-"+jr);
					Thread t;
					t = new Thread(new PlayRoom(soc,name,false));
					t.start();
					dispose();
				}catch(Exception e){e.printStackTrace();}
//				JOptionPane.showMessageDialog(
//						new JFrame(), jr);
			}
		});
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					dos.writeUTF("Leave");
				} catch (IOException e) {
					e.printStackTrace();
				}
				dispose();				
			}
		});
		
		find_txt = new JTextField();
		find_txt.setColumns(10);
		
		JButton btnFindRoom = new JButton("Find room");
		btnFindRoom.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent arg0) {
				try {
					String fr = find_txt.getText();
					if(fr.isEmpty())
						dos.writeUTF("refresh");
					else
						dos.writeUTF("find-"+fr);
					String receive = dis.readUTF();
					part = receive.split("-");
					List<String> result = new ArrayList<String>();
					String r = find_txt.getText();
					for(String room : part)
						if(room.contains(r))
							result.add(room);
					list.setListData(result.toArray());
				}catch(Exception e) {}
					
			}
		});
		Panel p2=new Panel(new GridLayout(5,1,0,20));
		p2.add(btnNewRoom);
		p2.add(btnJoinRoom);
		p2.add(find_txt);
		p2.add(btnFindRoom);
		p2.add(btnExit);
		
		contentPane.add(list);
		contentPane.add(p2);
	    this.setVisible(true);
	    this.addWindowListener(new WindowAdapter(){  
            public void windowClosing(WindowEvent e) {  
            	try{
            		dos.writeUTF("Leave");
            		dos.flush();
    				dos.close();
    				soc.close();
    			}
    			catch (Exception err) {
    				System.out.println("Err");
    			}
            }
		});
	}
	@Override
	public void run() {
		
	}
}
