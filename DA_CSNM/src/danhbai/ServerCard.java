package danhbai;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerCard{
	ServerSocket server;
	ArrayList<Room> Rooms = new ArrayList<Room>();
	public static void main(String[] args) {
		System.out.println("server is running.....");
		new ServerCard();
	}
	public ServerCard(){
		try {
			server = new ServerSocket(3000);
			while(true) {
				Socket soc = server.accept();
				Thread lobby = new Lobby(this, soc);
				lobby.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//tìm phòng
	public String getRoomsName(String find) {
		if(Rooms.size()==0)
			return "-";
		else {
			String rooms="-";
			for(Room r : Rooms)
				if(r.name.contains(find))
					rooms+= "-"+r.name;
			if(rooms.length() >2)
				rooms=rooms.substring(2);
			
			return rooms;
		}
	}
}
class Lobby extends Thread{
	Socket soc;
	ServerCard sv;
	public Lobby(ServerCard sv, Socket soc) throws IOException {
		this.sv = sv;
		this.soc = soc;
	}
	@Override
	public void run() {
		
		try {
			String username = null;
			ProPlayer pl = null;
			DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
			dos.writeUTF(sv.getRoomsName(""));
			
			DataInputStream dis = new DataInputStream(soc.getInputStream());
			String z = dis.readUTF();
			String[] z_split = z.split("-");
			
			if(z_split[0].equals("login")) {
				username = z_split[1];
				pl = new ProPlayer(username);
				System.out.println(pl.Name+" logged in");
			}
			
			boolean flag = true;
			while(flag) {
				z = dis.readUTF();
				z_split = z.split("-");
				if(z_split[0].equals("new")) {
					
					Room t = new Room(sv, soc, pl, z_split[1]);
					sv.Rooms.add(t);
					System.out.println("Create "+t.name);
					
					flag = false;
					
					t.start();
				}
				else if(z_split[0].equals("join")) {
					String rname = z_split[1];
					for(Room r : sv.Rooms) {
						if(r.name.equals(rname)) {
							r.joinRoom(soc, pl);
							System.out.println(pl.Name+" join "+r.name);
						}
					}
					flag = false;
				}
				else if(z_split[0].equals("find")) {
					String fname = z_split[1];
					dos.writeUTF(sv.getRoomsName(fname));
				}
				else if(z_split[0].equals("refresh")) {
					dos.writeUTF(sv.getRoomsName(""));
				}
				else if(z_split[0].equals("Leave")) {
					System.out.println(username+" leaved");
					flag = false;
				}
			}
		} catch(Exception e) { e.printStackTrace();	}
	}
}