package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
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
    private String st;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watting);
        soc = MainActivity.instance.getSocket();
        text = findViewById(R.id.waiting);
        new ClientThread().execute();
    }
    class ClientThread extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                st = dis.readUTF();
            }   catch (Exception e) {}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(Watting.this, Room.class);
            startActivity(intent);
        }
    }
}