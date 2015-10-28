package com.example.swings.night_sec;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.swings.night_sec.module.Pan;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Activity_pan_load extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.list_off_listview)
    ListView listOffListview;
    @InjectView(R.id.list_btn_cancle)
    Button listBtnCancle;
    @InjectView(R.id.list_ll_buttom)
    LinearLayout listLlButtom;
    List<Map<String, String>> lists;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_pan_load);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("未上传盘点信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        lists = new ArrayList<Map<String, String>>();
        List<Pan> pans = DataSupport.where("status = 2").find(Pan.class);
        for (Pan pan : pans
                ) {
            Map<String, String> map=new HashMap<String, String>();
            //盘点单号
            map.put("id",pan.getPan_id());
            //盘点单状态
            map.put("status",pan.getStatus().equals("2")?"未上传":"已上传");
            //盘点单仓库
            map.put("ck",pan.getCangku());
            //日期
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("date",format.format(pan.getDate()));
            lists.add(map);
        }
        adapter=new SimpleAdapter(getApplicationContext(),lists,R.layout.item_pandian,new String[]{"id","ck","status","date"},new int[]{R.id.text_pandian,R.id.text_ck,R.id.text_status,R.id.text_date});
        listOffListview.setAdapter(adapter);
        listOffListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean temp=lists.get(position).get("status").equals("0");
//                String status= temp?"未上传":"已上传";
                showToast("订单号:" + lists.get(position).get("id"));
                Intent intent=new Intent(Activity_pan_load.this,Activity_pan_load_list.class);
                intent.putExtra("pan",lists.get(position).get("id"));
                startActivity(intent);
            }
        });

        listBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
