package com.example.swings.night_sec;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityOut_detail extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.edit_text_ru_bianma)
    EditText editTextRuBianma;
    @InjectView(R.id.edit_text_ru_code)
    EditText editTextRuCode;
    @InjectView(R.id.edit_text_ru_name)
    EditText editTextRuName;
    @InjectView(R.id.edit_text_ru_fukuan)
    EditText editTextRuFukuan;
    @InjectView(R.id.edit_text_ru_kezhong)
    EditText editTextRuKezhong;
    @InjectView(R.id.edit_text_ru_weight)
    EditText editTextRuWeight;
    @InjectView(R.id.edit_text_ru_length)
    EditText editTextRuLength;
    @InjectView(R.id.edit_text_ru_cjbz)
    EditText editTextRuCjbz;
    @InjectView(R.id.edit_text_ru_shijian)
    EditText editTextRuShijian;
    @InjectView(R.id.edit_text_ru_yonghu)
    EditText editTextRuYonghu;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;
    @InjectView(R.id.ru_btn_cancle)
    Button ruBtnCancle;
    @InjectView(R.id.ru_btn_ok)
    Button ruBtnOk;
    @InjectView(R.id.ru_ll_bottom)
    LinearLayout ruLlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_activity_ru);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("产品详情:" + getIntent().getStringExtra("bianma"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setEditText();
        ruBtnCancle.setVisibility(View.GONE);
        ruBtnOk.setText("返  回");
        ruBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setEditText() {

        editTextRuBianma.setText(getIntent().getStringExtra("bianma"));
        editTextRuCode.setText(getIntent().getStringExtra("tiaoma"));
        editTextRuName.setText(getIntent().getStringExtra("name"));
        editTextRuFukuan.setText(getIntent().getStringExtra("fukuan"));
        editTextRuKezhong.setText(getIntent().getStringExtra("kezhong"));
        editTextRuWeight.setText(getIntent().getStringExtra("weight"));
        editTextRuLength.setText(getIntent().getStringExtra("length"));
        editTextRuCjbz.setVisibility(View.GONE);
        editTextRuShijian.setVisibility(View.GONE);
        editTextRuYonghu.setVisibility(View.GONE);
    }

    private void clearEditText() {
        editTextRuBianma.setText("");
        editTextRuCode.setText("");
        editTextRuName.setText("");
        editTextRuFukuan.setText("");
        editTextRuKezhong.setText("");
        editTextRuWeight.setText("");
        editTextRuLength.setText("");
        editTextRuCjbz.setText("");
        editTextRuShijian.setText("");
        editTextRuYonghu.setText("");
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
