package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
{
    public static MainActivity instance = null;
    private Socket soc;
    private ImageButton tologin, playoff;
    private EditText get_ip;
    private int ok;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tologin = findViewById(R.id.playonl);
        playoff = findViewById(R.id.playoff);
        tologin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogIP();
            }
        });
    }
    private void DialogIP()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.input_ip);
        get_ip = dialog.findViewById(R.id.ip);
        Button connect = dialog.findViewById(R.id.connect);
        Button cancel = dialog.findViewById(R.id.cancel);
        connect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new ClientThread().execute();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    class ClientThread extends AsyncTask<Void, Void, Void>
    {
        private String ip;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            ok = 0;
            ip = get_ip.getText().toString().trim();
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                soc = new Socket(ip, 9911);
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                ok = 1;
            }   catch (Exception e) { ok = 2; }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (ok == 1)
            {
                instance = MainActivity.this;
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Kết nối server thành công!", Toast.LENGTH_SHORT).show();
            }   else Toast.makeText(MainActivity.this, "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
        }
    }
    public Socket getSocket()
    {
        return this.soc;
    }
}