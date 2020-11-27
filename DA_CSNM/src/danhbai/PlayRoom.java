package danhbai;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class PlayRoom extends JFrame implements Runnable{

	public Player player = new Player("Thinh") ;
	String filePath = "D:\\java-oxygen\\GoUp\\png\\";
	Socket soc;
	String name;
	int playerInGame;
	int check = 0;
	int ID=0;
	Card[] currentMove = null;
	Card[] cardChoosing = null;
	int kc = 30;
	boolean isCreator, isStart = false;
	Image hidden;
	Image ban;
	boolean status, isFinish = false;
	Dimension screenSize;
	int cardw, cardh;
	Button btnDanhBai, btnBoLuot, btnReady;
	Panel pn1,pn2,pn;
	Button btn_send;
	TextField tf, timeText;
	TextArea result;
	Timer timer;
	int interval = 15;	
	
	/**
	 * Launch the application.
	 */

	public PlayRoom(Socket soc, String name, boolean start) {
		this.name = name;
		this.soc = soc;
		isCreator = start; //create room
		status =  start; //is your turn ?
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addComponent();
		GUI();
		loadImage();
		addListener();
		repaint();
	}

	private void GUI() {
		// TODO Auto-generated method stub
		pn2=new Panel(new FlowLayout());
		pn1 = new Panel(new GridLayout(1, 1));
		pn = new Panel(new FlowLayout());
		tf = new TextField();
		tf.setPreferredSize(new Dimension(450,25));
		btn_send = new Button("Send");
		result = new TextArea();
		result.setEnabled(false);
		result.setPreferredSize(new Dimension(500,210));
		pn.setBounds(screenSize.width*7/10, screenSize.height*7/10, 500, 250);
		pn2.add(tf);
		pn2.add(btn_send);
		pn1.add(result);
		pn.add(pn1);
		pn.add(pn2);
		add(pn);
	
	}
	
	private void addComponent() {
		setLayout(null);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		btnDanhBai = new Button("Danh bai");
		btnDanhBai.setBounds(screenSize.width*6/10, screenSize.height*14/20, 100, 40);
		btnBoLuot = new Button("Bo luot");
		btnBoLuot.setBounds(screenSize.width*6/10, screenSize.height*15/20, 100, 40);
		btnReady = new Button("Start");
		btnReady.setBounds(this.getWidth()*7/20+kc*3, this.getHeight()*8/20, 200, 150);
		if(isCreator) add(btnReady);
		timeText = new TextField();
		timeText.setBounds(screenSize.width*6/10, screenSize.height*16/20, 100, 100);
		timeText.setFont(new Font("Tahoma", Font.PLAIN, 80));
		timeText.setEditable(false);
		timer = new Timer(1000, new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(status){
					timeText.setText(interval--+"");
					if(interval<0){
						interval = 15;
						try {
							DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
							dos.writeUTF("drop");
						} catch (Exception e1) {
						}
						status = false;
						remove(timeText);
						btnDanhBai.setEnabled(false);
						btnBoLuot.setEnabled(false);
			    		repaint();
					}			
				}
			}
			});
	}

	protected void NextPlayer(int next) {
		// TODO Auto-generated method stub
		if(next==ID) {
			status = true;
			interval = 15;
			add(timeText);
		}
		else {
			status = false;
			remove(timeText);
		}
		btnDanhBai.setEnabled(status);
		btnBoLuot.setEnabled(status);
}

	private void addListener() {
		btnBoLuot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
					dos.writeUTF("drop");
				} catch (Exception e1) {
				}
				status = false;
				remove(timeText);
				btnDanhBai.setEnabled(false);
				btnBoLuot.setEnabled(false);
			}
		});
		
		btnDanhBai.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int count = 0;
				Card[] tmp = new Card[13];
				for(int i=0;i<player.myDesk.length;i++)
				{
					if(player.myDesk[i].isChoosing){
						tmp[count++]=player.myDesk[i];
					}
				}
				if(count>0){
					cardChoosing=Arrays.copyOfRange(tmp, 0, count);
				}
				if(currentMove == null || player.PlayCard(currentMove, cardChoosing)) {
					currentMove = cardChoosing;
					player.Pop(cardChoosing);
					if(player.myDesk.length == 0) isFinish = true;
					try {
						DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
						String s  = "card ";
						for(int k=0;k<cardChoosing.length;k++)
							s+=cardChoosing[k].ID+" ";
						if(isFinish) s+="Finish";
						dos.writeUTF(s);
					} catch (Exception e1) {
					}
					status = false;
					remove(timeText);
					btnDanhBai.setEnabled(false);
					btnBoLuot.setEnabled(false);
				}
				else {
					for(Card c : player.myDesk) {
						c.isChoosing = false;
					}
					cardChoosing = null;
				}
				repaint();
			}
		});
		PlayRoom r = this;
		r.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if(isStart) {
					if(y>r.getHeight()*19/20-screenSize.height/4 && (y<r.getHeight()*19/20-screenSize.height/4+cardh)
							&& x>r.getWidth()*1/4 && x<r.getWidth()*1/4 + (kc*5/4)*12 + cardw)
					{
						int ind = (x-r.getWidth()*1/4)/(kc*5/4);
						if(ind>12)
							ind=12;
						System.out.println(ind);
						if(player.myDesk[ind]!=null) {
							player.myDesk[ind].isChoosing = !player.myDesk[ind].isChoosing;
							r.repaint();
						}
					}
				}
			}
		});
		this.addWindowListener(new WindowAdapter(){  
            public void windowClosing(WindowEvent e) {  
            	try{
            		DataOutputStream dos =  new DataOutputStream(soc.getOutputStream());
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
		
		btnReady.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DataOutputStream dos;
				try {
					dos = new DataOutputStream(soc.getOutputStream());
	        		dos.writeUTF("Play");
				} catch (IOException e) {
					e.printStackTrace();
				}
				repaint();
			}
		});
		
		btn_send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = tf.getText();
				try{
					DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
					dos.writeUTF("message " + str);
					result.append("You : "+str+"\n");
					tf.setText("");
				}catch(Exception ex) {
					System.err.println(ex);
			}				
			}
		});
	}

	private void loadImage() {
		try{
			cardw = screenSize.width/10;
			cardh = screenSize.height/4;
			File f = new File(filePath+"hide.jpg");
			hidden = new BufferedImage(cardw, cardh, BufferedImage.TYPE_INT_ARGB);
			hidden = ImageIO.read(f);
		    hidden = hidden.getScaledInstance(cardw, cardh, Image.SCALE_SMOOTH);
		}
		catch(IOException e){
			System.out.println("Load hide error!");
		}
		try{
			File f = new File(filePath+"bg.png");
			ban = ImageIO.read(f);
		}
		catch(IOException e){
			System.out.println("Load table error! ");
		}
	}

	public void paint(Graphics g) {		
		g = this.getGraphics();
		this.setFocusable(true);
		setTitle("Welcome " + this.name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//table
		g.drawImage(ban, 0, 0, this);

		if(isStart) {
			//left
			if(playerInGame>=2)
				for(int k=0;k<13;k++)			
					g.drawImage(hidden, this.getWidth()*1/20, this.getHeight()*1/9+2*kc*k/2, this);
			//right
			if(playerInGame>=3)
				for(int k=0;k<13;k++)
					g.drawImage(hidden, this.getWidth()*17/20-screenSize.width/10, this.getHeight()*1/9+2*kc*k/2, this);
			//opposite
			if(playerInGame==4)
				for(int k=0;k<13;k++)
					g.drawImage(hidden, this.getWidth()*1/4+kc*k*5/4, this.getHeight()*1/20, this);
			//your
			for(int k=0;k<player.myDesk.length;k++) {
				if(player.myDesk[k]!=null) {
					if(player.myDesk[k].isChoosing)
						g.drawImage(player.myDesk[k].img, this.getWidth()*1/4+kc*k*5/4, this.getHeight()*19/20-screenSize.height/4-40, this);
					else
						g.drawImage(player.myDesk[k].img, this.getWidth()*1/4+kc*k*5/4, this.getHeight()*19/20-screenSize.height/4, this);
				}
			}
			
			//played card
			if(currentMove !=null) {
				for(int k=0;k<currentMove.length;k++)	
					g.drawImage(currentMove[k].img, this.getWidth()*6/20+kc*k*5/4, this.getHeight()*7/20, this);
			}
		}

	}
	@Override
	public void run() {
		while(true){
			try {
				DataInputStream dis = new DataInputStream(soc.getInputStream());
				String msg = dis.readUTF();
				String[] card = msg.split(" ");
				if(card[0].equals("duel")) {
					remove(btnReady);
					playerInGame = Integer.parseInt(card[1]);
					ID = Integer.parseInt(card[2]);
					int[] result = new int[card.length-3];
					for(int i=0;i<card.length-3;i++){
						result[i] = Integer.parseInt(card[i+3]);
					}
					Card[] tmp1 = new Card[result.length];
					for(int k=0;k<result.length;k++){
						tmp1[k] = new Card(result[k]);
					}
					player.myDesk = tmp1;
					startGame();
				} else if(card[0].equals("message")) {
					result.append(msg.substring(8) + "\n");
				} else if(card[0].equals("card")) {
					int[] result = new int[card.length-2];
					for(int i=0;i<card.length-2;i++){
						result[i] = Integer.parseInt(card[i+1]);
					}
					Card[] tmp2 = new Card[result.length];
					for(int k=0;k<result.length;k++){
						tmp2[k] = new Card(result[k]);
					}
					currentMove=tmp2;
					int next = Integer.parseInt(card[card.length-1]);
					System.out.println(msg);
					NextPlayer(next);
					repaint();
				} else if(card[0].equals("drop")) {
					int next = Integer.parseInt(card[card.length-1]);
					NextPlayer(next);
				} else if(card[0].equals("refresh")) {
					currentMove = null;
					int next = Integer.parseInt(card[card.length-1]);
					NextPlayer(next);
				} else if(card[0].equals("end")) {
					int[] result = new int[card.length-1];
					for(int i=0;i<card.length-1;i++){
						result[i] = Integer.parseInt(card[i+1]);
					}
					for(int k=0;k<result.length;k++){
						if(result[k] == ID)
							JOptionPane.showMessageDialog(new JFrame(), this.name + " top "+(k+1));
					}
					endGame();
				}
				repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startGame() {
		// TODO Auto-generated method stub
		isStart=true;
		isFinish = false;
		add(btnDanhBai);
		add(btnBoLuot);
		if(ID != 0) status = false;
		else status = true;
		btnDanhBai.setEnabled(status);
		btnBoLuot.setEnabled(status);
		if(status) add(timeText);
		timer.start();
	}
	
	private void endGame() {
		ID = 4;
		playerInGame = 0;
		player.myDesk = new Card[13];
		currentMove = null;
		status = false;
		interval = 15;
		remove(timeText);
		remove(btnDanhBai);
		remove(btnBoLuot);
		if(isCreator) add(btnReady);
		repaint();
	}
	
}
