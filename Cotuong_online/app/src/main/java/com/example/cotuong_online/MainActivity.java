package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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


public class MainActivity extends AppCompatActivity {
    private ImageButton tologin, playoff;
    private TextView text;
    private String ip;
    private int ok_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tologin = findViewById(R.id.playonl);
        playoff = findViewById(R.id.playoff);
        text = findViewById(R.id.text);
        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);*/
                DialogIP();
            }
        });
        playoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //new ClientThread().start();
                    //while (st.length() == 0)
                    //{
                    //    continue;
                    //}
                    //text.setText(st);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }
    private void DialogIP() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.input_ip);
        final EditText get_ip = dialog.findViewById(R.id.ip);
        Button connect = dialog.findViewById(R.id.connect);
        Button cancel = dialog.findViewById(R.id.cancel);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok_ip = 0;
                ip = get_ip.getText().toString().trim();
                new ClientThread(ip).start();
                while (ok_ip == 0) {
                    continue;
                }
                if (ok_ip == 1) {
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Kết nối server thành công!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(MainActivity.this, "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    class ClientThread extends Thread {
        String ip;
        public ClientThread(String ip) {
            this.ip = ip;
        }
        @Override
        public void run() {
            try {
                Socket soc = new Socket(ip, 9911);
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("Thanh cong");
                ok_ip = 1;
            } catch (Exception e) { ok_ip = 2; }
        }
    }
}