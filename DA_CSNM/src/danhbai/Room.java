package danhbai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Room extends Thread{
	static int countid = 1;
	ServerCard server;
	ArrayList<Socket> PlayerSocket = new ArrayList<Socket>();
	String name;
	int ID;
	String currentPlay;
	ArrayList<ProPlayer> Players = new ArrayList<ProPlayer>();
	Desk bb = new Desk();
	ArrayList<String> playerCurrentRounds = new ArrayList<String>();
	int nextplayer = 0;
	ArrayList<String> FinishedPlayer = new ArrayList<String>();
	boolean flag = true;
	
	public Room(ServerCard server, Socket soc, ProPlayer pl, String name){
		this.server = server;
		PlayerSocket.add(soc);
		Players.add(pl);
		this.name = name+"'s Room";
		this.ID = countid++;
		Client cli = new Client(server, this, soc, pl);
		cli.start();
	}
	public void joinRoom(Socket soc, ProPlayer pl) {
		if(PlayerSocket.size()<4) {
			PlayerSocket.add(soc);
			Players.add(pl);
			Client cli = new Client(server, this, soc, pl);
			cli.start();
		}
	}
	public void ChiaBai(){
		this.bb.Shuffle();
		for(int i = 0;i<this.Players.size();i++){
			this.Players.get(i).myDesk = Arrays.copyOfRange(this.bb.Desk, 13*i, 13*i+13);
		}
	}
	
	int ktraWin(){
		int i;
		for(i=0;i<Players.size();i++)
			if(Players.get(i).myDesk.length==0) return i;
		i=5;
		return i;
	}
	@Override
	public void run() {
		while(flag) {
		}
		server.Rooms.remove(this);
	}
}

class Client extends Thread{
	ServerCard server;
	Socket socket;
	Room room;
	ProPlayer player;
	boolean flag;
	
	public Client(ServerCard server, Room r, Socket soc, ProPlayer pl){
		this.server = server;
		this.socket = soc;
		this.room = r;
		this.player = pl;
		flag = true;
	}
	
	public void run() {
		while(flag) {
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String msg = dis.readUTF();
				String[] part = msg.split(" ");
				if(part[0].equals("message"))
					sendClient(msg.substring(8), socket);
				else if(part[0].equals("card")) {
					if (msg.contains("Finish")){
						room.FinishedPlayer.add(room.playerCurrentRounds.get(room.nextplayer));
		    			room.playerCurrentRounds.remove(room.nextplayer);
		    			msg = msg.substring(0, msg.length()-6);
		    			System.out.println("Finish"+room.FinishedPlayer);
		    			System.out.println("Current"+room.playerCurrentRounds);
		    			if(room.nextplayer>=room.playerCurrentRounds.size()) room.nextplayer=0;
		    			room.currentPlay = room.playerCurrentRounds.get(room.nextplayer);
					} else {
						room.currentPlay = room.playerCurrentRounds.get(room.nextplayer);
						room.nextplayer++;
		    			if(room.nextplayer>=room.playerCurrentRounds.size()) room.nextplayer=0;
					}
					playCard(msg+room.playerCurrentRounds.get(room.nextplayer), socket);					
				} else if(part[0].equals("Leave")){
					socket.close();
	    			System.out.println(player.Name+" leaved");
	    			room.PlayerSocket.remove(socket);
	    			room.Players.remove(player);
	    			if(room.Players.size()==0) {
		    			server.Rooms.remove(room);
		    			System.out.println(room.name+" removed");
	    				room.flag = false;
	    			}
	    			flag = false;
	    		} else if(part[0].equals("Play")) {
	    			if(room.Players.size()>1) {
		    			room.ChiaBai();
		    			for(int i = 0;i<room.Players.size();i++){
		    				room.Players.get(i).SortCard();
		    				room.playerCurrentRounds.add(i+"");
		    				room.nextplayer = 0;
		    				DataOutputStream dos = new DataOutputStream(room.PlayerSocket.get(i).getOutputStream());
		    				String s = "duel "+room.Players.size()+" "+i+" "+room.Players.get(i).Display();
		    				dos.writeUTF(s);
		    			}
	    			}
	    		} else if(part[0].equals("drop")) {
	    			room.nextplayer++;
					if(room.nextplayer>=room.playerCurrentRounds.size()) room.nextplayer=0;
					if(room.currentPlay.equals(room.playerCurrentRounds.get(room.nextplayer)))
						playCard("refresh "+room.playerCurrentRounds.get(room.nextplayer), null);
					else
						playCard("drop "+room.playerCurrentRounds.get(room.nextplayer),socket);
	    		}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void endGame() {
		String end = "end ";
		for(int i=0;i<room.FinishedPlayer.size();i++) {
			end+=room.FinishedPlayer.get(i)+" ";
		}
		end+=room.playerCurrentRounds.get(0);
		for(Socket i : room.PlayerSocket) {
			try{
				DataOutputStream dos = new DataOutputStream(i.getOutputStream());
				dos.writeUTF(end);
				System.out.println(end);
			}catch(Exception e) {
				System.err.println("Error3!");
			}
		}
		room.FinishedPlayer.clear();
		room.playerCurrentRounds.clear();
		room.currentPlay = null;
		room.nextplayer = 0;
	}

	private void playCard(String c, Socket soc) {
		for(Socket i : room.PlayerSocket) {
			try{
				if(!(i.equals(soc))) {
					DataOutputStream dos = new DataOutputStream(i.getOutputStream());
					dos.writeUTF(c);
					System.out.println(c);
				}
			}catch(Exception e) {
				System.err.println("Error3!");
			}
		}
		if(room.playerCurrentRounds.size()==1) {
			endGame();
		}
	}
	
	private void sendClient(String str, Socket soc) {
		for(Socket i : room.PlayerSocket) {
			try{
				if(!(i.equals(soc))) {
					DataOutputStream dos = new DataOutputStream(i.getOutputStream());
					dos.writeUTF("message "+player.Name+" : "+str);
				}
			}catch(Exception e) {
				System.err.println("Error3!");
			}
		}
	}
}