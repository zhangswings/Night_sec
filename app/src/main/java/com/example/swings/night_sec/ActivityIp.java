package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityIp extends AppCompatActivity {

    @InjectView(R.id.ip_editText)
    ClearEditText ipEditText;
    @InjectView(R.id.ip_btn_back)
    Button ipBtnBack;
    @InjectView(R.id.ip_btn_ok)
    Button ipBtnOk;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    String ip_str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_ip);
        ButterKnife.inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = preferences.edit();
        ip_str = preferences.getString("ip", "192.168.0.187");
        ipEditText.setText(ip_str);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("服务器设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ipBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ipEditText.getText())) {
                    ipEditText.setShakeAnimation();
                    //设置提示
                    showToast("查询信息不能为空!");
                } else {
                    edit.putString("ip", ipEditText.getText().toString().trim());
                    if (edit.commit()) {
                        showToast("ip保存成功!");
                        finish();
                    } else {
                        ipEditText.setShakeAnimation();
                        //设置提示
                        showToast("ip保存失败!");
                    }
                }
            }
        });
        ipBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityIp.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("是否继续退出?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        exitbuilder.setNegativeButton("否", null);
        // exitbuilder.create();
        exitbuilder.show();
    }

    /**
     * 显示Toast消息
     *
     * @param msg
     */
    private Toast mToast;

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
