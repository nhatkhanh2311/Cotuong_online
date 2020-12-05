package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.net.Socket;

public class Watting extends AppCompatActivity
{
    public Socket soc;
    public TextView text;
    private String st = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watting);
        soc = MainActivity.instance.getSocket();
        text = findViewById(R.id.waiting);
        new ClientThread().start();
        text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (st.equals("ok"))
                {
                    Intent intent = new Intent(Watting.this, Room.class);
                    startActivity(intent);
                }
            }
        });
    }
    class ClientThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                st = dis.readUTF();
            }   catch (Exception e) {}
        }
    }
}