package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Room extends AppCompatActivity
{
    public Socket soc;
    private String st = "", me_name, competitor_name;
    private GridView board;
    private Button ready;
    private TextView me, competitor, me_time, competitor_time, chat;
    private EditText enter_chat;
    private ArrayList<Piece> arrayPiece = new ArrayList<>();
    private PieceAdapter adapter;
    private int[] piece = {-1, -2, -3, -4, -5, -4, -3, -2, -1,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, -6, 0, 0, 0, 0, 0, -6, 0,
            -7, 0, -7, 0, -7, 0, -7, 0, -7,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            7, 0, 7, 0, 7, 0, 7, 0, 7,
            0, 6, 0, 0, 0, 0, 0, 6, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 2, 3, 4, 5, 4, 3, 2, 1};
    private boolean ok = false, readyok = false;
    private CountDownTimer time;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        soc = MainActivity.instance.getSocket();
        board = findViewById(R.id.board);
        ready = findViewById(R.id.ready);
        me = findViewById(R.id.me);
        competitor = findViewById(R.id.competitor);
        me_time = findViewById(R.id.me_time);
        competitor_time = findViewById(R.id.competitor_time);
        chat = findViewById(R.id.chat);
        enter_chat = findViewById(R.id.enter_chat);
        startBoard();
        adapter = new PieceAdapter(this, R.layout.piece, arrayPiece);
        board.setAdapter(adapter);
        ready.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //new Ready().start();
                //while (!readyok) continue;
                new ClientThread().start();
                while (!ok) continue;
                setBoard();
                adapter.notifyDataSetChanged();
                me.setText(me_name);
                competitor.setText(competitor_name);
                time = new CountDownTimer(30000, 1000)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        me_time.setText(millisUntilFinished/1000 + "");
                    }
                    @Override
                    public void onFinish()
                    {

                    }
                }.start();
                chat.setText(me_name + ": Ready?\n" + competitor_name + ": Ok goooo");
            }
        });
        /*new ClientThread().start();
        while (!ok) continue;
        setBoard();
        adapter.notifyDataSetChanged();*/
    }
    class ClientThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                String[] st = dis.readUTF().split("#");
                me_name = st[0];
                competitor_name = st[1];
                for (int i = 0; i < 90; i++) piece[i] = Integer.parseInt(st[i + 2]);
                ok = true;
            }   catch (Exception e) {e.printStackTrace();}
        }
    }
    class Ready extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("ready");
                st = dis.readUTF();
                readyok = true;
            }   catch (Exception e) {e.printStackTrace();}
        }
    }
    private void startBoard()
    {
        for (int i = 0; i < 90; i++) arrayPiece.add(new Piece(R.drawable.empty));
    }
    private void setBoard()
    {
        //arrayPiece = new ArrayList<>();
        for (int i = 0; i < 90; i++)
        {
            switch (piece[i])
            {
                case -7: arrayPiece.set(i, new Piece(R.drawable.tot2)); break;
                case -6: arrayPiece.set(i, new Piece(R.drawable.phao2)); break;
                case -5: arrayPiece.set(i, new Piece(R.drawable.tuong2)); break;
                case -4: arrayPiece.set(i, new Piece(R.drawable.si2)); break;
                case -3: arrayPiece.set(i, new Piece(R.drawable.tuongj2)); break;
                case -2: arrayPiece.set(i, new Piece(R.drawable.ma2)); break;
                case -1: arrayPiece.set(i, new Piece(R.drawable.xa2)); break;
                case 0: arrayPiece.set(i, new Piece(R.drawable.empty)); break;
                case 1: arrayPiece.set(i, new Piece(R.drawable.xa1)); break;
                case 2: arrayPiece.set(i, new Piece(R.drawable.ma1)); break;
                case 3: arrayPiece.set(i, new Piece(R.drawable.tuongj1)); break;
                case 4: arrayPiece.set(i, new Piece(R.drawable.si1)); break;
                case 5: arrayPiece.set(i, new Piece(R.drawable.tuong1)); break;
                case 6: arrayPiece.set(i, new Piece(R.drawable.phao1)); break;
                case 7: arrayPiece.set(i, new Piece(R.drawable.tot1)); break;
            }
        }
    }
}