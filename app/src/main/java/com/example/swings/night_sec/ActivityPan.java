package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.swings.night_sec.module.Pan;

import org.litepal.crud.DataSupport;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityPan extends AppCompatActivity {

    @InjectView(R.id.pan_editText)
    ClearEditText panEditText;
    @InjectView(R.id.pan_btn_back)
    Button panBtnBack;
    @InjectView(R.id.pan_btn_ok)
    Button panBtnOk;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.cangku_spinner)
    Spinner cangkuSpinner;
    @InjectView(R.id.out_ll_content)
    LinearLayout outLlContent;
    @InjectView(R.id.pan_ll_bottom)
    LinearLayout panLlBottom;
    Button panButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_pan);
        ButterKnife.inject(this);
        panButton= (Button) findViewById(R.id.pan_button);
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
        cangkuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showToast("您选择了 " + cangkuSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        panButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Activity_pan_load.class);
                startActivity(intent);
            }
        });
        panBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        panBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(panEditText.getText())) {
                    panEditText.setShakeAnimation();
                    showToast("单号不能为空!");
                } else {
                    if (DataSupport.where("pan_id = "+panEditText.getText().toString()).find(Pan.class).isEmpty()) {
                    //跳转下一个页面
                    Intent intent = new Intent(ActivityPan.this, Activity_pan_detail.class);
                    intent.putExtra("pandian", panEditText.getText().toString());
                    intent.putExtra("ck", cangkuSpinner.getSelectedItem().toString().substring(0, 2));
                    startActivity(intent);
                        panEditText.setText("");
                    } else {
                        panEditText.setShakeAnimation();
                        showToast("该盘点单号已存在，请重新输入!");
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityPan.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("是否继续退出?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        exitbuilder.setNegativeButton("否", null);
        exitbuilder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//       if( DataSupport.where("pid = "+getIntent().getStringExtra("pan")).find(Tiaoma.class).isEmpty()){
//           DataSupport.deleteAll(Pan.class,"pan_id = "+getIntent().getStringExtra("pan"));
//       }
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
