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
}
class Xuly extends Thread
{
    Server server;
    Socket soc1, soc2;
    String user1, user2;
    String piece1, piece2;
    boolean ready = false;
    int turn = 1;
    int[][] piece_start = {{-1, -2, -3, -4, -5, -4, -3, -2, -1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, -6, 0, 0, 0, 0, 0, -6, 0},
            {-7, 0, -7, 0, -7, 0, -7, 0, -7},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {7, 0, 7, 0, 7, 0, 7, 0, 7},
            {0, 6, 0, 0, 0, 0, 0, 6, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 2, 3, 4, 5, 4, 3, 2, 1}};
    int[][] piece = new int[10][9];
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
            dos1.writeUTF("start#" + user1 + "#" + user2 + "#p1");
            dos2.writeUTF("start#" + user2 + "#" + user1 + "#p2");
            System.out.println("ok");
            new Plr1().start();
            new Plr2().start();
        }   catch (IOException e) { e.printStackTrace(); }
    }
    class Plr1 extends Thread
    {
        public void run()
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc1.getInputStream());
                DataOutputStream dos1 = new DataOutputStream(soc1.getOutputStream());
                DataOutputStream dos2 = new DataOutputStream(soc2.getOutputStream());
                while (true)
                {
                    String[] code = dis.readUTF().split("#");
                    System.out.println("1");
                    if (code[0].equals("ready"))
                        if (!ready) ready = true;
                        else
                        {
                            for (int i = 0; i < 10; i++)
                                for (int j = 0; j < 9; j++) piece[i][j] = piece_start[i][j];
                            sendPiece();
                            ready = false;
                        }
                    else if (code[0].equals("play"))
                    {
                        if (turn == 1)
                        {
                            int x1 = Integer.parseInt(code[1]) / 9;
                            int y1 = Integer.parseInt(code[1]) % 9;
                            int x2 = Integer.parseInt(code[2]) / 9;
                            int y2 = Integer.parseInt(code[2]) % 9;
                            if (Rule.Rule1(x1, y1, x2, y2, piece))
                            {
                                piece[x2][y2] = piece[x1][y1];
                                piece[x1][y1] = 0;
                                sendPiece();
                                turn = 2;
                                new Chieuhet(piece, "1").start();
                            }
                        }
                    }   else if (code[0].equals("chat"))
                    {
                        sendChat(code[1], user1);
                    }   else if (code[0].equals("timeout"))
                    {
                        turn = 2;
                        dos1.writeUTF("lose");
                        dos2.writeUTF("win");
                    }
                }
            }   catch (IOException e) { e.printStackTrace(); }
        }
    }
    class Plr2 extends Thread
    {
        public void run()
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc2.getInputStream());
                DataOutputStream dos1 = new DataOutputStream(soc1.getOutputStream());
                DataOutputStream dos2 = new DataOutputStream(soc2.getOutputStream());
                while (true)
                {
                    String[] code = dis.readUTF().split("#");
                    System.out.println("2");
                    if (code[0].equals("ready"))
                        if (!ready) ready = true;
                        else
                        {
                            for (int i = 0; i < 10; i++)
                                for (int j = 0; j < 9; j++) piece[i][j] = piece_start[i][j];
                            sendPiece();
                            ready = false;
                        }
                    else if (code[0].equals("play"))
                    {
                        if (turn == 2)
                        {
                            int x1 = 9 - Integer.parseInt(code[1]) / 9;
                            int y1 = Integer.parseInt(code[1]) % 9;
                            int x2 = 9 - Integer.parseInt(code[2]) / 9;
                            int y2 = Integer.parseInt(code[2]) % 9;
                            if (Rule.Rule2(x1, y1, x2, y2, piece))
                            {
                                piece[x2][y2] = piece[x1][y1];
                                piece[x1][y1] = 0;
                                sendPiece();
                                turn = 1;
                                new Chieuhet(piece, "2").start();
                            }
                        }
                    }   else if (code[0].equals("chat"))
                    {
                        sendChat(code[1], user2);
                    }   else if (code[0].equals("timeout"))
                    {
                        turn = 1;
                        dos1.writeUTF("win");
                        dos2.writeUTF("lose");
                    }
                }
            }   catch (IOException e) { e.printStackTrace(); }
        }
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
    public void sendPiece()
    {
        try
        {
            DataOutputStream dos1 = new DataOutputStream(soc1.getOutputStream());
            DataOutputStream dos2 = new DataOutputStream(soc2.getOutputStream());
            piece1 = makePiece1(piece);
            piece2 = makePiece2(piece);
            dos1.writeUTF("board" + piece1);
            dos2.writeUTF("board" + piece2);
        }   catch (IOException e) {}
    }
    public void sendChat(String chat, String username)
    {
        try
        {
            DataOutputStream dos1 = new DataOutputStream(soc1.getOutputStream());
            DataOutputStream dos2 = new DataOutputStream(soc2.getOutputStream());
            dos1.writeUTF("chat#" + chat + "#" + username);
            dos2.writeUTF("chat#" + chat + "#" + username);
        }   catch (IOException e) {}
    }
    class Chieuhet extends Thread
    {
        int[][] piece;
        String plr;
        public Chieuhet(int[][] piece, String plr)
        {
            this.piece = piece;
            this.plr = plr;
        }
        @Override
        public void run()
        {
            super.run();
            try
            {
                DataOutputStream dos1 = new DataOutputStream(soc1.getOutputStream());
                DataOutputStream dos2 = new DataOutputStream(soc2.getOutputStream());
                if (Rule.Chieuhet(piece, plr))
                {
                    System.out.println("end");
                    if (plr.equals("1"))
                    {
                        turn = 1;
                        dos1.writeUTF("win");
                        dos2.writeUTF("lose");
                    }   else
                    {
                        turn = 2;
                        dos1.writeUTF("lose");
                        dos2.writeUTF("win");
                    }
                }
            }   catch (IOException e) { e.printStackTrace(); }
        }
    }
}
