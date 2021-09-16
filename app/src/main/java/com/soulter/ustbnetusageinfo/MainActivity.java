package com.soulter.ustbnetusageinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button idSubmitBtn;
    EditText idInput;
    TextView netUsageDp;
    CardView refreshBtn;
    TextView changeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        idSubmitBtn = findViewById(R.id.submit_id);
        idInput = findViewById(R.id.id_input);
        netUsageDp = findViewById(R.id.net_usage_tv);
        refreshBtn = findViewById(R.id.btnbtn);
        changeID = findViewById(R.id.change_id);
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
                try {


                    String data = response.body().string();
                    data = data.replace("jsonpReturn(", "").replace(");", "");
                    JSONObject jsonObject1 = JSONObject.parseObject(data);
                    String data1 = jsonObject1.get("data").toString();
                    JSONObject jsonObject = JSONObject.parseObject(data1);
                    Log.v("lwl", data);
                    netUsageDp.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("lwl", jsonObject.get("v4").toString());
                            float data1 = Float.parseFloat(jsonObject.get("v4").toString()) + Float.parseFloat(jsonObject.get("v6").toString());
                            String usageTag = "MB";
                            if (data1 > 1024) {
                                data1 = data1 / 1024;
                                data1 = (float) (Math.round(data1 * 100)) / 100;
                                usageTag = "GB";
                            }
                            if( Float.parseFloat(jsonObject.get("v6").toString()) == 0){
                                netUsageDp.setText(data1 + usageTag + "" + "\n(v4:" + jsonObject.get("v4").toString() + "MB)");
                            }else {
                                netUsageDp.setText(data1 + usageTag + "" + "\n(v4:" + jsonObject.get("v4").toString() + "MB\n" + "v6:" + jsonObject.get("v6").toString() + "MB)");

                            }

                        }
                    });
                }catch (Exception e){
                    netUsageDp.post(new Runnable() {
                        @Override
                        public void run() {
                            netUsageDp.setText("读取失败。。。");
                        }
                    });

                    e.printStackTrace();
                }

            }
        });
    }
}