package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SignIn extends AppCompatActivity
{
    public Socket soc;
    private EditText user, pass;
    private ImageButton signin, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        soc = MainActivity.instance.getSocket();
        user = findViewById(R.id.user_signin);
        pass = findViewById(R.id.pass_signin);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.to_signup);
        signin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new ClientThread().execute();
            }
        });
        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
    class ClientThread extends AsyncTask<Void, Void, Void>
    {
        private String username = "", password = "", st;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            username = user.getText().toString().trim();
            password = pass.getText().toString().trim();
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                if (!username.equals("") && !password.equals(""))
                {
                    DataInputStream dis = new DataInputStream(soc.getInputStream());
                    DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                    dos.writeUTF("dangnhap#" + username + "#" + password);
                    st = dis.readUTF();
                }
            }   catch (Exception e) {}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (username.equals("") || password.equals(""))
            {
                Toast.makeText(SignIn.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }   else if (st.equals("ok2"))
            {
                Intent intent = new Intent(SignIn.this, Room.class);
                startActivity(intent);
                Toast.makeText(SignIn.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            }   else if (st.equals("ok1"))
            {
                Intent intent = new Intent(SignIn.this, Watting.class);
                startActivity(intent);
                Toast.makeText(SignIn.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            }   else
            {
                Toast.makeText(SignIn.this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}