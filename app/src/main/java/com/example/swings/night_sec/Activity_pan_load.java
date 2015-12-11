package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.swings.night_sec.module.Bianma;
import com.example.swings.night_sec.module.Pan;
import com.example.swings.night_sec.module.Tiaoma;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * 查询未上传盘点信息列表
 * 1.点击查看盘点单详情
 * 2.长按选择是否上传该盘点单信息
 * 3.返回上级页面
 */
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
//    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    private String chejian_str;
    private String pandian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_pan_load);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
//        client = new AsyncHttpClient();
        //设置重复请求次数，间隔
//        client.setMaxRetriesAndTimeout(3, 2000);
        //设置超时时间，默认10s
//        client.setTimeout(2 * 1000);
        //设置连接超时时间为2秒（连接初始化时间）
//        chejian_str = getIntent().getStringExtra("ck");
//        pandian = getIntent().getStringExtra("pandian");

        progressDialog = new ProgressDialog(Activity_pan_load.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                client.cancelRequests(Activity_pan_load.this, true);
                MyClient.cancleClient(Activity_pan_load.this);
                showToast("已取消上传");
            }
        });
        List<Pan> pans = DataSupport.where("status = 2").find(Pan.class);
        for (Pan pan : pans
                ) {
            Map<String, String> map = new HashMap<String, String>();
            //盘点单号
            map.put("id", pan.getPan_id());
            //盘点单状态
            map.put("status", pan.getStatus().equals("2") ? "未上传" : "已上传");
            //盘点单仓库
            map.put("ck", pan.getCangku());
            //日期
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("date", format.format(pan.getDate()));
            lists.add(map);
        }
        adapter = new SimpleAdapter(getApplicationContext(), lists, R.layout.item_pandian, new String[]{"id", "ck", "status", "date"}, new int[]{R.id.text_pandian, R.id.text_ck, R.id.text_status, R.id.text_date});
        listOffListview.setAdapter(adapter);
        listOffListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                boolean temp = lists.get(position).get("status").equals("0");
//                String status= temp?"未上传":"已上传";
                showToast("订单号:" + lists.get(position).get("id"));
                Intent intent = new Intent(Activity_pan_load.this, Activity_pan_load_list.class);
                intent.putExtra("pan", lists.get(position).get("id"));
                startActivity(intent);
//                return true;
            }
        });
        listOffListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String pan_id = lists.get(position).get("id");
                final String ck_id = lists.get(position).get("ck");
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pan_load.this);
                builder.setIcon(R.mipmap.right);
                builder.setTitle("系统提示");
                builder.setMessage("请选择上传/删除该盘点单！");
                builder.setNegativeButton("上传", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pan_load.this);
                        builder.setIcon(R.mipmap.right);
                        builder.setTitle("全部上传");
                        builder.setMessage("请确认是否全部上次服务器?");
                        builder.setNegativeButton("否", null);
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //获取产品信息，全部上次
                                //Todo
                                if (!DataSupport.where("pid = " + pan_id).find(Tiaoma.class).isEmpty()) {
                                    progressDialog.setTitle("上传中...");
                                    progressDialog.setMessage("正在上传数据，请稍后...");
                                    progressDialog.setCancelable(true);
                                    progressDialog.show();
                                    String builder_Detail = "";
                                    if (DataSupport.where("pid = " + pan_id).find(Tiaoma.class).size() == 1) {
                                        builder_Detail = DataSupport.where("pid = " + pan_id).find(Tiaoma.class).get(0).getTiaoma_id();
                                    } else {
                                        String detail_str = "";
                                        for (int i = 0; i < DataSupport.where("pid = " + pan_id).find(Tiaoma.class).size(); i++) {
                                            detail_str += DataSupport.where("pid = " + pan_id).find(Tiaoma.class).get(i).getTiaoma_id() + ",";

                                        }
                                        builder_Detail = detail_str.substring(0, detail_str.length() - 1);

                                    }
                                    final RequestParams params = new RequestParams();

                                    //传递参数

                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Log.d("detail", builder_Detail);
                                    Log.d("store", ck_id);
//                                //Detail:(bianma,tiaoma,weight,lenght) 编码、条码、重量、长度
                                    params.put("store", ck_id);
                                    params.put("detail", builder_Detail);
                                    MyClient.post(Activity_pan_load.this,"http://" + preferences.getString("ip", "192.168.0.187") + ":8092/JsonHandler.ashx?doc=GetPD_Info", params, new AsyncHttpResponseHandler() {


                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            progressDialog.dismiss();
                                            if (statusCode == 200) {
                                                String info = "";
                                                String text = new String(responseBody);
                                                Log.d("zhang", text + ">>>>>zhang");
//                                                try {
//                                                    Document document = DocumentHelper.parseText(text);
//                                                    Element element = document.getRootElement();
//                                                    info = element.getText();
//
//                                                } catch (DocumentException de) {
//                                                    Log.e("de", de.toString());
//                                                }

                                                if ("true".equals(text)) {
//                                            clearEditText();
                                                    showToast("上传成功!");
                                                    DataSupport.deleteAll(Pan.class, "pan_id = " + pan_id);
//                                            DataSupport.where("pid = "+getIntent().getStringExtra("pandian")).de
                                                    DataSupport.deleteAll(Tiaoma.class, "pid = " + pan_id);
                                                    finish();
                                                } else {

                                                    showToast(info);
                                                }


                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            progressDialog.dismiss();

                                            AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_pan_load.this);
                                            exitbuilder.setTitle("系统提示");
                                            exitbuilder.setMessage("未上传成功!\n请检查网络后重新上传.");
                                            exitbuilder.setIcon(R.mipmap.circle);
                                            exitbuilder.setCancelable(false);
                                            exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = null;
                                                    //判断手机系统的版本  即API大于10 就是3.0或以上版本
                                                    if (android.os.Build.VERSION.SDK_INT > 10) {
                                                        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                                    } else {
                                                        intent = new Intent();
                                                        ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                                                        intent.setComponent(component);
                                                        intent.setAction("android.intent.action.VIEW");
                                                    }
                                                    startActivity(intent);
                                                }
                                            });
                                            exitbuilder.setNegativeButton("取消", null);
                                            // exitbuilder.create();
                                            exitbuilder.show();

                                        }
                                    });
//                            lists.clear();
//                            adapter.notifyDataSetChanged();
                                } else {
                                    showToast("数据列表为空");
                                }

                            }
                        });
                        builder.create().show();
                    }
                });

                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pan_load.this);
                        builder.setIcon(R.mipmap.circle);
                        builder.setTitle("删除提示");
                        builder.setMessage("是否继续删除盘点单 " + pan_id + "?");
                        builder.setNegativeButton("否", null);
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.deleteAll(Pan.class, "pan_id = " + pan_id);
                                DataSupport.deleteAll(Bianma.class, "pan_id = " + pan_id);
                                DataSupport.deleteAll(Tiaoma.class, "pid = " + pan_id);
                                showToast("删除成功!");
//                                lists.remove(lists.in)
                                finish();
                            }
                        });
                        builder.create().show();
                    }
                });
                builder.create().show();
                return false;
            }
        });
        listBtnCancle.setText("返回");
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
