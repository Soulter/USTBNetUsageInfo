package com.soulter.ustbnetusageinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogNetActivity extends AppCompatActivity {

    private EditText LogNetUserName;
    private EditText LogNetUserPsw;
    private Button saveUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_net);

        final SharedPreferences spfs = this.getSharedPreferences("spfs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = this.getSharedPreferences("spfs", Context.MODE_PRIVATE).edit();

        LogNetUserName = findViewById(R.id.login_net_user_name);
        LogNetUserPsw = findViewById(R.id.login_net_psw);
        saveUserInfo = findViewById(R.id.save_user_info);

        saveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!LogNetUserName.getText().toString().equals("") && !LogNetUserPsw.getText().toString().equals("")){
                    editor.putString("log_net_user_name", LogNetUserName.getText().toString());
                    editor.putString("log_net_user_psw", LogNetUserPsw.getText().toString());
                    editor.apply();
                    finish();
                }

            }
        });


    }
}