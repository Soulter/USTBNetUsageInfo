package com.soulter.ustbnetusageinfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button idSubmitBtn;
    private TextView loginNet;
    EditText idInput;
    TextView netUsageDp;
    CardView refreshBtn;
    TextView changeID;
    TextView netUsageDW;
    TextView netUsageDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loginNet = findViewById(R.id.auto_login_btn);
        idSubmitBtn = findViewById(R.id.submit_id);
        idInput = findViewById(R.id.id_input);
        netUsageDp = findViewById(R.id.net_usage_tv);
        refreshBtn = findViewById(R.id.btnbtn);
        changeID = findViewById(R.id.change_id);
        netUsageDW = findViewById(R.id.net_usage_danwei);
        netUsageDetails = findViewById(R.id.net_usage_details);
        final SharedPreferences spfs = this.getSharedPreferences("spfs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = this.getSharedPreferences("spfs", Context.MODE_PRIVATE).edit();

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsage(spfs.getString("stu_id","failed"));
            }
        });
        changeID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshBtn.setVisibility(View.GONE);
                netUsageDp.setVisibility(View.GONE);
                idInput.setVisibility(View.VISIBLE);
                idSubmitBtn.setVisibility(View.VISIBLE);
                changeID.setVisibility(View.GONE);
            }
        });
        idSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!idInput.getText().toString().equals("")) {
                    editor.putString("stu_id", idInput.getText().toString());
                    editor.apply();
                    editor.putBoolean("stu_id_ok", true);
                    editor.apply();

                    idInput.setVisibility(View.GONE);
                    idSubmitBtn.setVisibility(View.GONE);
                    netUsageDp.setVisibility(View.VISIBLE);
                    refreshBtn.setVisibility(View.VISIBLE);
                    changeID.setVisibility(View.VISIBLE);

                    getUsage(spfs.getString("stu_id", "failed"));
                }else{
                    idInput.setError("不能为空！！！");
                }
            }
        });

        loginNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spfs.getString("log_net_user_name","null").equals("null")){
                    Intent intent = new Intent(MainActivity.this, LogNetActivity.class);
                    startActivity(intent);
                }else{
                    String url = "http://222.199.222.14:801/eportal/portal/login?callback=dr1004&login_method=1&user_account="+spfs.getString("log_net_user_name","null")+"&user_password="+spfs.getString("log_net_user_psw","null")+"&wlan_ac_ip=10.0.124.68&wlan_ac_name=WX5560H&jsVersion=4.1&terminal_type=1&lang=zh-cn&v=5755&lang=zh";
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    OkHttpClient okHttpClient =  new OkHttpClient();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.body().string().contains("协议认证成功")){
]                                Toast.makeText(MainActivity.this,"一键连接成功！",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });

        if (spfs.getBoolean("stu_id_ok",false)){
            idInput.setVisibility(View.GONE);
            idSubmitBtn.setVisibility(View.GONE);
            netUsageDp.setVisibility(View.VISIBLE);
            refreshBtn.setVisibility(View.VISIBLE);
            changeID.setVisibility(View.VISIBLE);
            getUsage(spfs.getString("stu_id","failed"));

        }else {
            idInput.setVisibility(View.VISIBLE);
            idSubmitBtn.setVisibility(View.VISIBLE);
            netUsageDp.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.GONE);
            changeID.setVisibility(View.GONE);
        }


    }

    public void getUsage(String stuid){
        String url = "http://202.204.48.82:801/eportal/portal/visitor/loadUserFlow?&account="+stuid+"&jsVersion=4.1&v=10478&lang=zh";
        Log.v("lwl","url:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient okHttpClient =  new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                netUsageDp.post(new Runnable() {
                    @Override
                    public void run() {
                        netUsageDp.setText("错误，也许没连校园网？");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = "null";
                try {
                    data = response.body().string();
                    data = data.replace("jsonpReturn(", "").replace(");", "");
                    JSONObject jsonObject1 = JSONObject.parseObject(data);
                    String data1 = jsonObject1.get("data").toString();

                    JSONObject jsonObject = JSONObject.parseObject(data1);
                    Log.v("lwl", data);
                    netUsageDp.post(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                Log.v("lwl", jsonObject.get("v4").toString());
                                float data1 = Float.parseFloat(jsonObject.get("v4").toString()) + Float.parseFloat(jsonObject.get("v6").toString());
                                String usageTag = "MB";
                                if (data1 > 1024) {
                                    data1 = data1 / 1024;
                                    data1 = (float) (Math.round(data1 * 100)) / 100;
                                    usageTag = "GB";
                                }
                                if( Float.parseFloat(jsonObject.get("v6").toString()) == 0){
                                    netUsageDp.setText(data1+"");
                                    netUsageDW.setText(usageTag);
                                    netUsageDetails.setText("v4:" + jsonObject.get("v4").toString() + "MB");
                                }else {
                                    netUsageDp.setText(data1+ "");
                                    netUsageDW.setText(usageTag);
                                    netUsageDetails.setText("v4:" + jsonObject.get("v4").toString() + "MB | " + "v6:" + jsonObject.get("v6").toString() + "MB");

                                }
                            }catch(NumberFormatException e){
                                e.printStackTrace();
                                netUsageDp.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        netUsageDp.setText("转换出错。下面显示原数据以供临时观看:\n"+data1);
                                    }
                                });

                            }


                        }


                    });
                }catch (Exception e){
                    netUsageDp.post(new Runnable() {
                        @Override
                        public void run() {
                            netUsageDp.setText("读取失败。。。\n");

                        }
                    });

                    e.printStackTrace();
                }

            }
        });
    }
}