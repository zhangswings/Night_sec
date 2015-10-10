package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityRu_before extends AppCompatActivity {

    @InjectView(R.id.before_btn_cancle)
    Button beforeBtnCancle;
    @InjectView(R.id.before_btn_ok)
    Button beforeBtnOk;
    @InjectView(R.id.before_ll_bottom)
    LinearLayout beforeLlBottom;
    @InjectView(R.id.cangku_spinner)
    Spinner cangkuSpinner;
    @InjectView(R.id.banzu_spinner)
    Spinner banzuSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_ru_before);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_ruku);
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
                showToast("选择了仓库 " + cangkuSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast("你还未选择!");
            }
        });
        banzuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showToast("选择了班组 " + banzuSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast("你还未选择!");
            }
        });
        //取消
        beforeBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //确认
        beforeBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataSupport.where("paper_status='0' and paper_chejian='" + cangkuSpinner.getSelectedItem().toString().substring(0, 2) + "'").find(Papers.class).isEmpty()) {
                    //跳转下个页面出库
                    Intent intent = new Intent(ActivityRu_before.this, Ruku.class);
                    //传入供货商信息
                    intent.putExtra("ruku", cangkuSpinner.getSelectedItem().toString().substring(0, 2));
                    intent.putExtra("banzu", banzuSpinner.getSelectedItem().toString().substring(0, 1));
                    startActivity(intent);
                    //清空
                } else {
                    AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityRu_before.this);
                    exitbuilder.setTitle("系统提示");
                    exitbuilder.setMessage("该仓库还有未上传的离线数据，是否进入查看并上传?");
                    exitbuilder.setIcon(R.mipmap.right);
                    exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ActivityRu_before.this, Activity_Ru_off_list.class);
                            intent.putExtra("ruku", cangkuSpinner.getSelectedItem().toString().substring(0, 2));
                            intent.putExtra("banzu", banzuSpinner.getSelectedItem().toString().substring(0, 1));
                            startActivity(intent);
                        }
                    });
                    exitbuilder.setNegativeButton("取消", null);
                    // exitbuilder.create();
                    exitbuilder.show();
                }}
        });

//        String[] banzus = new String[]{"A", "B", "C", "D","E"};
//        ArrayAdapter<String> adapter_banzu = new ArrayAdapter<String>(this, R.layout.item_cangku, cangkus);
//        banzuRuku.setAdapter(adapter_banzu);
//        banzuRuku.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//                    beforeBtnOk.requestFocus();
//                    showToast("班组" + banzuRuku.getText().toString());
//                }
////                Log.d("imess", actionId + "_" + event.toString());
//                return false;
//            }
//        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityRu_before.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("您是否要退出吗?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        exitbuilder.setNegativeButton("取消", null);
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
