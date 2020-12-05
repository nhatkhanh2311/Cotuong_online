import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String[] args)
    {
        System.out.println("Server is started...");
        new Server();
    }
    public Server()
    {
        try
        {
            ServerSocket server = new ServerSocket(9911);
            while (true)
            {
                Socket soc = server.accept();
                new Client(this, soc).start();
            }
        }   catch (Exception e) {}
    }
}
class Client extends Thread
{
    Socket soc;
    Server sv;
    public Client(Server sv, Socket soc)
    {
        this.sv = sv;
        this.soc = soc;
    }
    public void run()
    {
        try
        {
            String username = null;
            DataInputStream dis = new DataInputStream(soc.getInputStream());
            DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
            while (true)
            {
                String[] code = dis.readUTF().split("#");
                if (code[0].equals("dangnhap"))
                {
                    username = Account.Signin(code[1], code[2]);
                    if (username == null) dos.writeUTF("fail");
                    else
                    {
                        String result = Game.Game(sv, soc, username);
                        dos.writeUTF(result);
                        if (result.equals("ok2")) Game.Xuly();
                    }
                }   else if (code[0].equals("dangky"))
                {
                    String result = Account.Signup(code[1], code[2], code[3], code[4]);
                    dos.writeUTF(result);
                }
            }
        }   catch (Exception e) {}
    }
}