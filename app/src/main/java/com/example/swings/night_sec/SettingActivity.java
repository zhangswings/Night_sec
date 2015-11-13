package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingActivity extends AppCompatActivity {

    @InjectView(R.id.setting_view_1)
    RelativeLayout settingView1;
    @InjectView(R.id.setting_view_4)
    RelativeLayout settingView4;
    @InjectView(R.id.setting_view_10)
    RelativeLayout settingView10;
    @InjectView(R.id.setting_view_12)
    RelativeLayout settingView12;
    @InjectView(R.id.text_isSave)
    TextView text_isSave;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = preferences.edit();
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//ip设置
        settingView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ActivityIp.class);
                startActivity(intent);
                finish();
            }
        });
        boolean isSaved = text_isSave.getText().toString().equals("是") ? true : false;
        if (isSaved) {
            text_isSave.setTextColor(getResources().getColor(R.color.error_color));
            text_isSave.setText("否");
            edit.putBoolean("isSave", false);
        } else {
            text_isSave.setTextColor(getResources().getColor(R.color.background_material_dark));
            text_isSave.setText("是");
            edit.putBoolean("isSave", true);
        }
        //保存登录信息
        settingView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSave = text_isSave.getText().toString().equals("是") ? true : false;
                if (isSave) {
                    text_isSave.setTextColor(getResources().getColor(R.color.error_color));
                    text_isSave.setText("否");
                    edit.putBoolean("isSave", false);
                } else {
                    text_isSave.setTextColor(getResources().getColor(R.color.background_material_dark));
                    text_isSave.setText("是");
                    edit.putBoolean("isSave", true);
                }
                if (edit.commit()) {
                    showToast("操作成功");
                }

            }
        });
        //版本更新
        settingView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "已是最新版本！", Toast.LENGTH_SHORT).show();
            }
        });
        //版本信息
        settingView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
            }
        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(SettingActivity.this);
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
