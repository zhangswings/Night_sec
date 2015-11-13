package com.example.swings.night_sec;

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

import com.example.swings.night_sec.module.Tiaoma;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Activity_pan_load_list extends AppCompatActivity {

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
    String pan="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_pan_load);
        ButterKnife.inject(this);
        pan=getIntent().getStringExtra("pan");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("盘点单："+pan );
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
        List<Tiaoma> tiaomas = DataSupport.where("pid = "+getIntent().getStringExtra("pan")).find(Tiaoma.class);
        for (Tiaoma tiaoma : tiaomas
                ) {
            Map<String, String> map=new HashMap<String, String>();
            //条码号
            map.put("id", tiaoma.getTiaoma_id());
            //重量
            map.put("ck",tiaoma.getLength());
            //长度
//            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("date",tiaoma.getWeight());
            lists.add(map);
        }
        adapter=new SimpleAdapter(getApplicationContext(),lists,R.layout.item_detail,new String[]{"id","ck","date"},new int[]{R.id.text_name,R.id.text_fukuan,R.id.text_weight});
        listOffListview.setAdapter(adapter);
        listOffListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast("条码号:" + lists.get(position).get("id") + "\n长度:" + lists.get(position).get("ck")+ " 重量:" + lists.get(position).get("date"));
//                Intent intent=new Intent(Activity_pan_load.this,)
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
