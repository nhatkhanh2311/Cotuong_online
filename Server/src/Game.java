import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Game
{
    private static int d = 1;
    private static String user1, user2;
    private static Socket soc1, soc2;
    private static Server server;
    public static String Game(Server sv, Socket soc, String username)
    {
        if (d == 1)
        {
            server = sv;
            soc1 = soc;
            user1 = username;
            d++;
            return "ok1";
        }
        soc2 = soc;
        user2 = username;
        d--;
        return "ok2";
    }
    public static void Xuly()
    {
        new Xuly(soc1, soc2, user1, user2, server).start();
    }
    public static String makePiece1(int[][] piece)
    {
        String piece1 = "";
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 9; j++)
                piece1 += "#" + piece[i][j];
            return piece1;
    }
    public static String makePiece2(int[][] piece)
    {
        String piece2 = "";
        for (int i = 9; i > -1; i--)
            for (int j = 0; j < 9; j++)
                piece2 += "#" + piece[i][j];
        return piece2;
    }
}
class Xuly extends Thread
{
    Server server;
    Socket soc1, soc2;
    String user1, user2;
    String piece1, piece2;
    int[][] piece = {{-1, -2, -3, -4, -5, -4, -3, -2, -1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, -6, 0, 0, 0, 0, 0, -6, 0},
            {-7, 0, -7, 0, -7, 0, -7, 0, -7},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {7, 0, 7, 0, 7, 0, 7, 0, 7},
            {0, 6, 0, 0, 0, 0, 0, 6, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 2, 3, 4, 5, 4, 3, 2, 1}};
    public Xuly(Socket soc1, Socket soc2, String user1, String user2, Server server)
    {
        this.soc1 = soc1;
        this.soc2 = soc2;
        this.user1 = user1;
        this.user2 = user2;
        this.server = server;
    }
    public void run()
    {
        try
        {
            DataInputStream dis1 = new DataInputStream(soc1.getInputStream());
            DataOutputStream dos1 = new DataOutputStream(soc1.getOutputStream());
            DataInputStream dis2 = new DataInputStream(soc2.getInputStream());
            DataOutputStream dos2 = new DataOutputStream(soc2.getOutputStream());
            dos1.writeUTF("ok");
            System.out.println("ok");
            /*String st = dis1.readUTF();
            System.out.println(st);
            st = dis2.readUTF();
            System.out.println(st);*/
            piece1 = Game.makePiece1(piece);
            piece2 = Game.makePiece2(piece);
            System.out.println("ok");
            dos1.writeUTF(user1 + "#" + user2 + piece1);
            dos2.writeUTF(user2 + "#" + user1 + piece2);
            System.out.println("ok");
        }   catch (IOException e) { e.printStackTrace(); }
    }
}
