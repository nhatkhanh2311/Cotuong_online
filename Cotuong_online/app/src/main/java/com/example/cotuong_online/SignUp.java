package com.example.cotuong_online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private String st;
    private EditText user, pass, repass, email;
    private ImageButton signup;
    private int ok;

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
                String username = user.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String repassword = repass.getText().toString().trim();
                String emailadd = email.getText().toString().trim();
                ok = 0;
                new ClientThread(username, password, repassword, emailadd).start();
                while (ok == 0)
                {
                    continue;
                }
                if (st.equals("ok"))
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
        });
    }
    class ClientThread extends Thread
    {
        String username, password, repassword, emailadd;
        public ClientThread(String username, String password, String repassword, String emailadd)
        {
            this.username = username;
            this.password = password;
            this.repassword = repassword;
            this.emailadd = emailadd;
        }
        @Override
        public void run()
        {
            try
            {
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                dos.writeUTF("dangky#" + username + "#" + password + "#" + repassword + "#" + emailadd);
                st = dis.readUTF();
                ok = 1;
            }   catch (Exception e) {}
        }
    }
}