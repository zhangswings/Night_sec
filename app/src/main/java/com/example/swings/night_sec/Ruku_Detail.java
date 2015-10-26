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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Ruku_Detail extends AppCompatActivity {

    @InjectView(R.id.out_editText)
    ClearEditText outEditText;
    @InjectView(R.id.out_gongyingshang_list)
    ListView outGongyingshangList;
    @InjectView(R.id.out_btn_cancle)
    Button outBtnCancle;
    @InjectView(R.id.out_btn_ok)
    Button outBtnOk;
    private SimpleAdapter adapter;
    private List<Map<String, String>> lists;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_out_second);
        ButterKnife.inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("已扫描产品条码信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        outBtnOk.setVisibility(View.GONE);
//        outEditText.setText(ghs + ck);
        outBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lists = new ArrayList<Map<String, String>>();
        lists = (List<Map<String, String>>) getIntent().getSerializableExtra("ruku_list");
        adapter = new SimpleAdapter(this, lists, R.layout.item_paper, new String[]{"name", "kezhong", "fukuan", "weight"}, new int[]{R.id.text_name, R.id.text_kezhong, R.id.text_fukuan, R.id.text_weight});
        outGongyingshangList.setAdapter(adapter);
        outGongyingshangList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Ruku_Detail.this, ActivityOut_detail.class);
                intent.putExtra("bianma", lists.get(position).get("bianma"));
                intent.putExtra("tiaoma", lists.get(position).get("tiaoma"));
                intent.putExtra("name", lists.get(position).get("name"));
                intent.putExtra("kezhong", lists.get(position).get("kezhong"));
                intent.putExtra("fukuan", lists.get(position).get("fukuan"));
                intent.putExtra("weight", lists.get(position).get("weight"));
                intent.putExtra("length", lists.get(position).get("length"));
                startActivity(intent);
            }
        });
        outGongyingshangList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Ruku_Detail.this);
                builder.setIcon(R.mipmap.circle);
                builder.setTitle("提示信息");
                builder.setMessage("是否删除该条码" + lists.get(position).get("tiaoma") + "?");
                builder.setNegativeButton("否", null);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lists.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.create().show();

                return false;


            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Ruku_Detail.this);
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
