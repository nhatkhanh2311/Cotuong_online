package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SignUp extends AppCompatActivity
{
    public Socket soc;
    private EditText user, pass, repass, email;
    private ImageButton signup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        soc = MainActivity.instance.getSocket();
        user = findViewById(R.id.user_signup);
        pass = findViewById(R.id.pass_signup);
        repass = findViewById(R.id.repass_signup);
        email = findViewById(R.id.email_signup);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new ClientThread().execute();
            }
        });
    }
    class ClientThread extends AsyncTask<Void, Void, Void>
    {
        String username = "", password = "", repassword = "", emailadd = "", st;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            username = user.getText().toString().trim();
            password = pass.getText().toString().trim();
            repassword = repass.getText().toString().trim();
            emailadd = email.getText().toString().trim();
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                if (!username.equals("") && !password.equals("") && !repassword.equals("") && !emailadd.equals(""))
                {
                    DataInputStream dis = new DataInputStream(soc.getInputStream());
                    DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                    dos.writeUTF("dangky#" + username + "#" + password + "#" + repassword + "#" + emailadd);
                    st = dis.readUTF();
                }
            }   catch (Exception e) {}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (username.equals("") || password.equals("") || repassword.equals("") || emailadd.equals(""))
            {
                Toast.makeText(SignUp.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }   else if (st.equals("ok"))
            {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                Toast.makeText(SignUp.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            }   else if (st.equals("notsame"))
            {
                Toast.makeText(SignUp.this, "Mật khẩu nhập lại không đúng!", Toast.LENGTH_SHORT).show();
            }   else
            {
                Toast.makeText(SignUp.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}