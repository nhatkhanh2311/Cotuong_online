package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Room extends AppCompatActivity
{
    public Socket soc;
    private String me_name, competitor_name;
    private GridView board;
    private Button ready;
    private TextView me, competitor, me_time, competitor_time, chat;
    private EditText enter_chat;
    private ImageButton send;
    private ArrayList<Piece> arrayPiece = new ArrayList<>();
    private PieceAdapter adapter;
    private int[] piece = new int[90];
    private int plr, choose;
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
        send = findViewById(R.id.send);
        startBoard();
        adapter = new PieceAdapter(Room.this, R.layout.piece, arrayPiece);
        board.setAdapter(adapter);
        new Receive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ready.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Ready().execute();
            }
        });
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String inf = enter_chat.getText().toString().trim();
                enter_chat.setText("");
                if (!inf.equals(""))  new Chat().execute(inf);
            }
        });
        board.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (choose == -1)
                {
                    if (piece[position] != 0)
                    {
                        board.getChildAt(position).setBackgroundColor(Color.BLUE);
                        choose = position;
                    }
                }   else
                {
                    board.getChildAt(choose).setBackgroundColor(View.INVISIBLE);
                    new Play().execute(choose + "", position + "");
                    choose = -1;
                }
            }
        });
    }

    //Thread nhận thông tin server
    class Receive extends AsyncTask<Void, String, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                while (true)
                {
                    String st = dis.readUTF();
                    publishProgress(st);
                }
            }   catch (Exception e) {e.printStackTrace();}
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);
            String[] st = values[0].split("#");
            //start
            if (st[0].equals("start"))
            {
                choose = -1;
                me.setText(st[1]);
                competitor.setText(st[2]);
                if (st[3].equals("p1")) plr = 1;
                else plr = 2;
            }
            //set board
            else if (st[0].equals("board"))
            {
                for (int i = 0; i < 90; i++) piece[i] = Integer.parseInt(st[i + 1]);
                setBoard();
                if (time != null) time.cancel();
                me_time.setText("");
                competitor_time.setText("");
                if (plr == 1)
                {
                    time = new CountDownTimer(45000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            me_time.setText(millisUntilFinished/1000 + "");
                        }
                        @Override
                        public void onFinish()
                        {
                            new TimeOut().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }.start();
                    plr = 2;
                }   else
                {
                    time = new CountDownTimer(45000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            competitor_time.setText(millisUntilFinished/1000 + "");
                        }
                        @Override
                        public void onFinish()
                        {
                        }
                    }.start();
                    plr = 1;
                }
            }
            //win
            else if (st[0].equals("win") || st[0].equals("lose"))
            {
                Result(st[0]);
                time.cancel();
                time = null;
                ready.setVisibility(View.VISIBLE);
                choose = -1;
            }
            //chat
            else if (st[0].equals("chat"))
            {
                chat.append(st[2] + ": " + st[1] + "\n");
            }
        }
    }

    //Thread ready
    class Ready extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            ready.setVisibility(View.INVISIBLE);
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("ready");
            }   catch (Exception e) {e.printStackTrace();}
            return null;
        }
    }

    //Thread time out
    class TimeOut extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("timeout");
            }   catch (Exception e) {e.printStackTrace();}
            return null;
        }
    }

    //Thread gửi thông tin nước cờ
    class Play extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings)
        {
            try
            {
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("play#" + strings[0] + "#" + strings[1]);
            }   catch (Exception e) {e.printStackTrace();}
            return null;
        }
    }

    //Thread gửi thông tin chat
    class Chat extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings)
        {
            try
            {
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("chat#" + strings[0]);
            }   catch (Exception e) {e.printStackTrace();}
            return null;
        }
    }

    //Tạo bàn cờ rỗng
    private void startBoard()
    {
        for (int i = 0; i < 90; i++) arrayPiece.add(new Piece(R.drawable.empty));
    }

    //Vẽ lại bàn cờ
    private void setBoard()
    {
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
        adapter.notifyDataSetChanged();
    }
    private void Result(String st)
    {
        final Dialog dialog = new Dialog(Room.this);
        dialog.setContentView(R.layout.result);
        TextView result = dialog.findViewById(R.id.result);
        Button ok = dialog.findViewById(R.id.ok);
        result.setText("You " + st + "!");
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}