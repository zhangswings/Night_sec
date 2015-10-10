package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityPan extends AppCompatActivity {

    @InjectView(R.id.pan_editText)
    ClearEditText panEditText;
    @InjectView(R.id.pan_gongyingshang_list)
    ListView panGongyingshangList;
    @InjectView(R.id.pan_btn_back)
    Button panBtnBack;
    @InjectView(R.id.pan_btn_ok)
    Button panBtnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_pan);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_pan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        panBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(panEditText.getText())) {
                    panEditText.setShakeAnimation();
                    showToast("单号不能为空!");
            }else{

                    //跳转下一个页面
                    Intent intent=new Intent(ActivityPan.this,Activity_pan_detail.class);
                    intent.putExtra("pandian",panEditText.getText().toString());
                    startActivity(intent);
                    panEditText.setText("");
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityPan.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("您是否要退出吗?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        exitbuilder.setNegativeButton("取消", null);
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
