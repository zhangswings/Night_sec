package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class Activity_Ru_off_list extends AppCompatActivity {

    @InjectView(R.id.list_off_listview)
    ListView listOffListview;
    @InjectView(R.id.list_btn_cancle)
    Button listBtnCancle;
    @InjectView(R.id.list_btn_ok)
    Button listBtnOk;
    SharedPreferences preferences;
    private SimpleAdapter adapter;
    private List<Map<String, String>> lists;
    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    int add_num = 0;
    String ruku_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__ru_off_list);
        ButterKnife.inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("离线出库信息" + getIntent().getStringExtra("ruku"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //代码开始
        progressDialog = new ProgressDialog(Activity_Ru_off_list.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                client.cancelRequests(Activity_Ru_off_list.this, true);
                showToast("已取消上传");
            }
        });
        client = new AsyncHttpClient();
        //设置重复请求次数，间隔
        client.setMaxRetriesAndTimeout(3, 2000);
        //设置超时时间，默认10s
        client.setTimeout(2 * 1000);
        //设置连接超时时间为2秒（连接初始化时间）
        lists = new ArrayList<Map<String, String>>();
         ruku_str = getIntent().getStringExtra("ruku");
        final List<Papers> papers = DataSupport.where("paper_status='0' and paper_chejian='" + ruku_str + "'").find(Papers.class);
//        final List<Papers> papers = DataSupport.where("paper_status='0' and paper_chejian='02'").find(Papers.class);
        if (!papers.isEmpty()) {
            for (int i = 0; i < papers.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", papers.get(i).getPaper_name());
                map.put("bianma", papers.get(i).getPaper_tiaoma());
                map.put("tiaoma", papers.get(i).getPaper_code());
                map.put("weight", papers.get(i).getPaper_weight());
                map.put("length", papers.get(i).getPaper_length());
                map.put("banzu", papers.get(i).getPaper_banzu());
                map.put("chejian", papers.get(i).getPaper_chejian());
                lists.add(map);
                Log.d("zhang", papers.toString());
            }
        } else {
            showToast("数据为空!");
        }
        adapter = new SimpleAdapter(this, lists, R.layout.item_view, new String[]{"name", "bianma", "tiaoma", "weight", "length"}, new int[]{R.id.text_name, R.id.text_bianma, R.id.text_tiaoma, R.id.text_weight, R.id.text_lenght});
        listOffListview.setAdapter(adapter);
        listOffListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Activity_Ru_off_list.this, Activity_Ru_off_detail.class);
                intent.putExtra("tiaoma", lists.get(position).get("tiaoma"));
                startActivity(intent);
            }
        });
        listOffListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Ru_off_list.this);
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
        listBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Ru_off_list.this);
                builder.setIcon(R.mipmap.right);
                builder.setTitle("全部上传");
                builder.setMessage("请确认是否全部上次服务器?");
                builder.setNegativeButton("否", null);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获取产品信息，全部上次
                        //Todo
                        if (!lists.isEmpty()) {
                            progressDialog.setTitle("上传中...");
                            progressDialog.setMessage("正在上传数据，请稍后...");
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                            for (int i = 0; i < lists.size(); i++) {
                                final RequestParams params = new RequestParams();
                                final int a = i;
                                final String tiaoma = lists.get(a).get("tiaoma");
                                //传递参数
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String builder_Content = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + lists.get(i).get("chejian") + "," + lists.get(i).get("banzu");
//                                //Content:(oper,time,store,group) 操作人、上传时间、仓库(车间)、班组
                                Log.d("content", builder_Content);
                                String builder_Detail = lists.get(i).get("bianma") + "," + lists.get(i).get("tiaoma") + "," + lists.get(i).get("weight") + "," + lists.get(i).get("length");
                                Log.d("detail", builder_Detail);
//                                //Detail:(bianma,tiaoma,weight,lenght) 编码、条码、重量、长度
                                params.put("Content", builder_Content);
                                params.put("Detail", builder_Detail);
                                client.post(Activity_Ru_off_list.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_InStore", params, new AsyncHttpResponseHandler() {


                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        progressDialog.dismiss();
                                        if (statusCode == 200) {
                                            String info = "";
                                            String text = new String(responseBody);
                                            Log.d("zhang", text + ">>>>>zhang");
                                            try {
                                                Document document = DocumentHelper.parseText(text);
                                                Element element = document.getRootElement();
                                                info = element.getText();

                                            } catch (DocumentException de) {
                                                Log.e("de", de.toString());
                                            }
                                            showToast(info);
//                                            clearEditText();
//                                            clearString();
//                                            finish();
//                                            if ("成功".equals(info.substring(1, info.length() - 1))) {
                                            Papers paper = new Papers();
                                            paper.setPaper_status("1");
                                            paper.updateAll("paper_code=" + tiaoma);
//                                            lists.remove(lists.indexOf(tiaoma));
                                            lists.clear();
                                            final List<Papers> papers = DataSupport.where("paper_status='0' and paper_chejian='" + ruku_str + "'").find(Papers.class);
//        final List<Papers> papers = DataSupport.where("paper_status='0' and paper_chejian='02'").find(Papers.class);
                                            if (!papers.isEmpty()) {
                                                for (int i = 0; i < papers.size(); i++) {
                                                    Map<String, String> map = new HashMap<String, String>();
                                                    map.put("name", papers.get(i).getPaper_name());
                                                    map.put("bianma", papers.get(i).getPaper_tiaoma());
                                                    map.put("tiaoma", papers.get(i).getPaper_code());
                                                    map.put("weight", papers.get(i).getPaper_weight());
                                                    map.put("length", papers.get(i).getPaper_length());
                                                    map.put("banzu", papers.get(i).getPaper_banzu());
                                                    map.put("chejian", papers.get(i).getPaper_chejian());
                                                    lists.add(map);
                                                    Log.d("zhang", papers.toString());
                                                }
                                            } else {
                                                showToast("全部上传成功!");
                                            }
                                            adapter.notifyDataSetChanged();
//                                            }
//                                            if (lists.size() == 0) {
//                                                Papers paper = new Papers();
//                                                paper.setPaper_status("1");
//                                                paper.updateAll("paper_chejian=" + getIntent().getStringExtra("chuku") + " and paper_status=0");
//                                                showToast("全部上传成功");
//                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        progressDialog.dismiss();

                                        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_Ru_off_list.this);
                                        exitbuilder.setTitle("系统提示");
                                        exitbuilder.setMessage("未上传成功!\n网络错误,是否将该条码信息保存到本地?");
                                        exitbuilder.setIcon(R.mipmap.circle);
                                        exitbuilder.setCancelable(false);
                                        exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                showToast("该条码已存在");
                                            }
                                        });
                                        exitbuilder.setNegativeButton("取消", null);
                                        // exitbuilder.create();
                                        exitbuilder.show();

                                    }
                                });
                            }
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
        listBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_Ru_off_list.this);
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
