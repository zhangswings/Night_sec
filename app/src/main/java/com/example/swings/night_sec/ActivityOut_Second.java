package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swings.night_sec.module.ChukuInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
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

public class ActivityOut_Second extends AppCompatActivity {

    @InjectView(R.id.out_editText)
    ClearEditText outEditText;
    @InjectView(R.id.out_gongyingshang_list)
    ListView outGongyingshangList;
    @InjectView(R.id.out_btn_cancle)
    Button outBtnCancle;
    @InjectView(R.id.out_btn_ok)
    Button outBtnOk;
    private static AsyncHttpClient client;
    @InjectView(R.id.sacn_num)
    TextView sacnNum;
    @InjectView(R.id.out_ll_bottom)
    LinearLayout outLlBottom;
    @InjectView(R.id.sacn_chepai)
    TextView sacnChepai;
    int chukunum = 0;
    int accountnum = 0;
    private SimpleAdapter adapter;
    private List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
    ;
    String ghs = "";
    String ghsname = "";
    String ck = "";
    String chepaihao = "";
    String kehu = "";

    ProgressDialog progressDialog;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_activity_out_second);
        ButterKnife.inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        client = new AsyncHttpClient();
        ghs = getIntent().getStringExtra("ghs");
        kehu = getIntent().getStringExtra("ghsname");
        chepaihao = getIntent().getStringExtra("chepaihao");
        sacnChepai.setText("车牌号：" + chepaihao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(kehu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progressDialog = new ProgressDialog(ActivityOut_Second.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                client.cancelRequests(ActivityOut_Second.this, true);
                showToast("已取消上传");
            }
        });
        // 初始化振动器
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        outEditText.setText(ghs + ck);

        if (("guaqi").equals(getIntent().getStringExtra("guaqi"))) {
            List<ChukuInfo> chukuInfos = DataSupport.where("status = 2").find(ChukuInfo.class);
            for (ChukuInfo chukuInfo : chukuInfos
                    ) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", chukuInfo.getNama());
                map.put("bianma", chukuInfo.getBianma());
                map.put("tiaoma", chukuInfo.getTiaoma());
                map.put("weight", chukuInfo.getWeight());
                map.put("length", chukuInfo.getLenght());
                map.put("kezhong", chukuInfo.getKezhong());
                map.put("fukuan", chukuInfo.getFukuan());
                map.put("chejian", chukuInfo.getChejian());
                lists.add(map);
            }
            sacnNum.setText("已扫描：" + lists.size() + "件");
        }
        //挂起按钮
        outBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder nextBuilder = new AlertDialog.Builder(ActivityOut_Second.this);
                nextBuilder.setTitle("请输入车牌号进行二次确认");
                nextBuilder.setIcon(R.mipmap.right);
                LayoutInflater factory = LayoutInflater.from(ActivityOut_Second.this);
                View view = factory.inflate(R.layout.alert_dialog, null);
                final EditText editText = (EditText) view.findViewById(R.id.editText);
                final TextView textView = (TextView) view.findViewById(R.id.textView2);
                textView.setText("请输入车牌号:" + chepaihao);
                nextBuilder.setView(view);
                nextBuilder.setPositiveButton("确认挂起", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText("请输入车牌号:" + chepaihao);
                        String chepaihao_str = editText.getText().toString();
                        if (chepaihao.equals(chepaihao_str)) {
                            showToast("输入正确");
                            if (!lists.isEmpty()) {
                                if (DataSupport.where("  status = '2'").find(ChukuInfo.class).isEmpty() || !DataSupport.where("chepai = " + chepaihao + " and status = '2'").find(ChukuInfo.class).isEmpty()) {
                                    List<ChukuInfo> ChukuInfos = DataSupport.where("chepai = " + chepaihao + " and status = '1'").find(ChukuInfo.class);
                                    Log.d("zhang", ChukuInfos.toString());
                                    int temp = 0;
                                    if (ChukuInfos.size() > 0) {
                                        for (ChukuInfo chukuinfo : ChukuInfos
                                                ) {
                                            chukuinfo.setStatus("2");
                                            if (chukuinfo.save()) {
                                                temp++;
                                            }
                                        }
                                        if (temp == ChukuInfos.size()) {
                                            showToast("已全部挂起！");
                                            onBackPressed();
                                        }
                                    } else {
                                        showToast("已全部挂起！");
                                        onBackPressed();
                                    }
                                } else {
                                    showToast("已有挂起车辆！不能再次挂起！");
                                }
                            } else {
                                showToast("需挂起出货信息为空！");
                            }
                        } else {
                            showToast("输入错误，请重新操作!");
                        }
                    }
                });
                nextBuilder.setNegativeButton("取消", null);
                nextBuilder.create().show();


            }
        });
        //上传按钮
        outBtnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!lists.isEmpty()) {
                                                List<ChukuInfo> Chukus;
//                                                if (("guaqi").equals(getIntent().getStringExtra("guaqi"))) {
//                                                    //挂起出库车辆信息
//                                                    Chukus = DataSupport.where("chepai = " + chepaihao + " and status = '2'").find(ChukuInfo.class);
//                                                } else {
                                                //当前出库车辆信息
                                                Chukus = DataSupport.where("chepai = " + chepaihao).find(ChukuInfo.class);

//                                                }

                                                int strck_01 = 0, strck_02 = 0, strck_03 = 0, strck_04 = 0, strck_05 = 0;
                                                float numck_01 = 0, numck_02 = 0, numck_03 = 0, numck_04 = 0, numck_05 = 0;
//                                                content_str);
//                                                Log.d("zhang", detail_str);
//                                                String content_str_01, content_str_02, content_str_03, content_str_04, content_str_05;
                                                StringBuilder detail_str_01 = new StringBuilder();
                                                StringBuilder detail_str_02 = new StringBuilder();
                                                StringBuilder detail_str_03 = new StringBuilder();
                                                StringBuilder detail_str_04 = new StringBuilder();
                                                StringBuilder detail_str_05 = new StringBuilder();
                                                for (int i = 0; i < Chukus.size(); i++) {
                                                    switch (Chukus.get(i).getChejian()) {
                                                        case "01":
                                                            numck_01 += Float.parseFloat(Chukus.get(i).getWeight());
                                                            strck_01 += 1;
                                                            break;
                                                        case "02":
                                                            numck_02 += Float.parseFloat(Chukus.get(i).getWeight());
                                                            strck_02 += 1;
                                                            break;
                                                        case "03":
                                                            numck_03 += Float.parseFloat(Chukus.get(i).getWeight());
                                                            strck_03 += 1;
                                                            break;
                                                        case "04":
                                                            numck_04 += Float.parseFloat(Chukus.get(i).getWeight());
                                                            strck_04 += 1;
                                                            break;
                                                        case "05":
                                                            numck_05 += Float.parseFloat(Chukus.get(i).getWeight());
                                                            strck_05 += 1;
                                                            break;
                                                    }
                                                }
                                                StringBuilder sb = new StringBuilder();
                                                sb.append((numck_01 > 0 ? "车间:01 " + "总重量:" + numck_01 + "\n" : "") + (numck_02 > 0 ? "车间:02 " + "总重量:" + numck_02 + "\n" : "") + (numck_03 > 0 ? "车间:03 " + "总重量:" + numck_03 + "\n" : "") + (numck_04 > 0 ? "车间:04" + "总重量:" + numck_04 + "\n" : "") + (numck_05 > 0 ? "车间:05" + "总重量:" + numck_05 + "\n" : ""));
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOut_Second.this);
                                                builder.setTitle("提示信息");
                                                builder.setIcon(R.mipmap.right);
                                                builder.setMessage("请确认扫描信息\n" + sb);
                                                builder.setNegativeButton("否", null);
                                                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        AlertDialog.Builder nextBuilder = new AlertDialog.Builder(ActivityOut_Second.this);
                                                        nextBuilder.setTitle("请输入车牌号进行二次确认");
                                                        nextBuilder.setIcon(R.mipmap.right);
                                                        LayoutInflater factory = LayoutInflater.from(ActivityOut_Second.this);
                                                        View view = factory.inflate(R.layout.alert_dialog, null);
                                                        final EditText editText = (EditText) view.findViewById(R.id.editText);
                                                        final TextView textView = (TextView) view.findViewById(R.id.textView2);
                                                        textView.setText("请输入车牌号:" + chepaihao);
                                                        nextBuilder.setView(view);
                                                        nextBuilder.setPositiveButton("确认上传", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                textView.setText("请输入车牌号:" + chepaihao);
                                                                String chepaihao_str = editText.getText().toString();
                                                                if (chepaihao.equals(chepaihao_str)) {
                                                                    showToast("输入正确");
                                                                    RequestParams params = new RequestParams();
                                                                    //传递参数
                                                                    String content_str = "";
                                                                    String detail_str = "";
                                                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                    //操作人、时间。仓库(车间)、客户编号、客户、车牌号
                                                                    content_str = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + ck + "," + ghs + "," + kehu;
                                                                    List<ChukuInfo> Chukus;
//                                                if (("guaqi").equals(getIntent().getStringExtra("guaqi"))) {
//                                                    //挂起出库车辆信息
//                                                    Chukus = DataSupport.where("chepai = " + chepaihao + " and status = '2'").find(ChukuInfo.class);
//                                                } else {
                                                                    //当前出库车辆信息
                                                                    Chukus = DataSupport.where("chepai = " + chepaihao).find(ChukuInfo.class);

//                                                }

                                                                    int strck_01 = 0, strck_02 = 0, strck_03 = 0, strck_04 = 0, strck_05 = 0;
//                                                content_str);
//                                                Log.d("zhang", detail_str);
                                                                    String content_str_01, content_str_02, content_str_03, content_str_04, content_str_05;
                                                                    StringBuilder detail_str_01 = new StringBuilder();
                                                                    StringBuilder detail_str_02 = new StringBuilder();
                                                                    StringBuilder detail_str_03 = new StringBuilder();
                                                                    StringBuilder detail_str_04 = new StringBuilder();
                                                                    StringBuilder detail_str_05 = new StringBuilder();
                                                                    for (int i = 0; i < Chukus.size(); i++) {
                                                                        switch (Chukus.get(i).getChejian()) {
                                                                            case "01":
                                                                                strck_01 += 1;
                                                                                if (strck_01 > 0) {
                                                                                    detail_str_01.append(Chukus.get(i).getBianma() + "," + Chukus.get(i).getTiaoma() + "," + Chukus.get(i).getWeight() + "," + Chukus.get(i).getLenght() + "|");
                                                                                }
                                                                                if (strck_01 == 1) {
                                                                                    accountnum += 1;
                                                                                }
                                                                                break;
                                                                            case "02":
                                                                                strck_02 += 1;
                                                                                if (strck_02 > 0) {
                                                                                    detail_str_02.append(Chukus.get(i).getBianma() + "," + Chukus.get(i).getTiaoma() + "," + Chukus.get(i).getWeight() + "," + Chukus.get(i).getLenght() + "|");
                                                                                }
                                                                                if (strck_02 == 1) {
                                                                                    accountnum += 1;
                                                                                }
                                                                                break;
                                                                            case "03":
                                                                                strck_03 += 1;
                                                                                if (strck_03 > 0) {
                                                                                    detail_str_03.append(Chukus.get(i).getBianma() + "," + Chukus.get(i).getTiaoma() + "," + Chukus.get(i).getWeight() + "," + Chukus.get(i).getLenght() + "|");
                                                                                }
                                                                                if (strck_03 == 1) {
                                                                                    accountnum += 1;
                                                                                }
                                                                                break;
                                                                            case "04":
                                                                                strck_04 += 1;
                                                                                if (strck_04 > 0) {
                                                                                    detail_str_04.append(Chukus.get(i).getBianma() + "," + Chukus.get(i).getTiaoma() + "," + Chukus.get(i).getWeight() + "," + Chukus.get(i).getLenght() + "|");
                                                                                }
                                                                                if (strck_04 == 1) {
                                                                                    accountnum += 1;
                                                                                }
                                                                                break;
                                                                            case "05":
                                                                                strck_05 += 1;
                                                                                if (strck_05 > 0) {
                                                                                    detail_str_05.append(Chukus.get(i).getBianma() + "," + Chukus.get(i).getTiaoma() + "," + Chukus.get(i).getWeight() + "," + Chukus.get(i).getLenght() + "|");
                                                                                }
                                                                                if (strck_05 == 1) {
                                                                                    accountnum += 1;
                                                                                }
                                                                                break;
                                                                        }
                                                                    }
                                                                    if (strck_01 > 0) {
                                                                        detail_str = detail_str_01.substring(0, detail_str_01.length() - 1);
                                                                        content_str = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + "01" + "," + ghs + "," + kehu + "," + chepaihao;
                                                                        RequestParams params_01 = new RequestParams();
                                                                        params.put("Content", content_str);
                                                                        params.put("Detail", detail_str);
                                                                        Log.d("zhang", content_str);
                                                                        Log.d("zhang", detail_str);
                                                                        //设置重复请求次数，间隔
                                                                        client.setMaxRetriesAndTimeout(3, 2000);
                                                                        //设置超时时间，默认10s
                                                                        client.setTimeout(5 * 1000);
                                                                        client.post(ActivityOut_Second.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_OutStore", params, new AsyncHttpResponseHandler() {
                                                                            @Override
                                                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                if (statusCode == 200) {
                                                                                    progressDialog.dismiss();
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
                                                                                    if ("\"成功\"".equals(info)) {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                        chukunum += 1;
                                                                                        DataSupport.deleteAll(ChukuInfo.class, "chejian = '01'");
                                                                                        List<ChukuInfo> chukuInfos = DataSupport.where("chepaihao = " + chepaihao).find(ChukuInfo.class);
                                                                                        if (!chukuInfos.isEmpty()) {
                                                                                            for (ChukuInfo chukuInfo : chukuInfos
                                                                                                    ) {
                                                                                                Map<String, String> map = new HashMap<String, String>();
                                                                                                map.put("name", chukuInfo.getNama());
                                                                                                map.put("bianma", chukuInfo.getBianma());
                                                                                                map.put("tiaoma", chukuInfo.getTiaoma());
                                                                                                map.put("weight", chukuInfo.getWeight());
                                                                                                map.put("length", chukuInfo.getLenght());
                                                                                                map.put("kezhong", chukuInfo.getKezhong());
                                                                                                map.put("fukuan", chukuInfo.getFukuan());
                                                                                                map.put("chejian", chukuInfo.getChejian());
                                                                                                lists.add(map);
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");
                                                                                        } else {
                                                                                            lists.clear();
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");

                                                                                        }
                                                                                        if (chukunum == accountnum) {
                                                                                            showToast("全部出库成功!");
                                                                                            MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                            finish();
                                                                                        }
                                                                                    } else {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                        showToast(info);
                                                                                    }
                                                                                    barcodes = null;
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                                progressDialog.dismiss();
                                                                                MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                showToast(error.toString());
                                                                            }
                                                                        });
                                                                    }
                                                                    if (strck_02 > 0) {
                                                                        detail_str = detail_str_02.substring(0, detail_str_02.length() - 1);
                                                                        content_str = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + "02" + "," + ghs + "," + kehu + "," + chepaihao;
                                                                        RequestParams params_01 = new RequestParams();
                                                                        params.put("Content", content_str);
                                                                        params.put("Detail", detail_str);
                                                                        Log.d("zhang", content_str);
                                                                        Log.d("zhang", detail_str);
                                                                        //设置重复请求次数，间隔
                                                                        client.setMaxRetriesAndTimeout(3, 2000);
                                                                        //设置超时时间，默认10s
                                                                        client.setTimeout(5 * 1000);
                                                                        client.post(ActivityOut_Second.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_OutStore", params, new AsyncHttpResponseHandler() {
                                                                            @Override
                                                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                if (statusCode == 200) {
                                                                                    progressDialog.dismiss();
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
                                                                                    if ("\"成功\"".equals(info)) {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                        chukunum += 1;
                                                                                        DataSupport.deleteAll(ChukuInfo.class, "chejian = '02'");
                                                                                        List<ChukuInfo> chukuInfos = DataSupport.where("chepaihao = " + chepaihao).find(ChukuInfo.class);
                                                                                        if (!chukuInfos.isEmpty()) {
                                                                                            for (ChukuInfo chukuInfo : chukuInfos
                                                                                                    ) {
                                                                                                Map<String, String> map = new HashMap<String, String>();
                                                                                                map.put("name", chukuInfo.getNama());
                                                                                                map.put("bianma", chukuInfo.getBianma());
                                                                                                map.put("tiaoma", chukuInfo.getTiaoma());
                                                                                                map.put("weight", chukuInfo.getWeight());
                                                                                                map.put("length", chukuInfo.getLenght());
                                                                                                map.put("kezhong", chukuInfo.getKezhong());
                                                                                                map.put("fukuan", chukuInfo.getFukuan());
                                                                                                map.put("chejian", chukuInfo.getChejian());
                                                                                                lists.add(map);
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");
                                                                                        } else {
                                                                                            lists.clear();
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");

                                                                                        }
                                                                                        if (chukunum == accountnum) {
                                                                                            showToast("全部出库成功!");
                                                                                            MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                            finish();
                                                                                        }
                                                                                    } else {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                        showToast(info);
                                                                                    }
                                                                                    barcodes = null;
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                                progressDialog.dismiss();
                                                                                MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                showToast(error.toString());
                                                                            }
                                                                        });
                                                                    }
                                                                    if (strck_03 > 0) {
                                                                        detail_str = detail_str_03.substring(0, detail_str_03.length() - 1);
                                                                        content_str = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + "03" + "," + ghs + "," + kehu + "," + chepaihao;
                                                                        RequestParams params_01 = new RequestParams();
                                                                        params.put("Content", content_str);
                                                                        params.put("Detail", detail_str);
                                                                        Log.d("zhang", content_str);
                                                                        Log.d("zhang", detail_str);
                                                                        //设置重复请求次数，间隔
                                                                        client.setMaxRetriesAndTimeout(3, 2000);
                                                                        //设置超时时间，默认10s
                                                                        client.setTimeout(5 * 1000);
                                                                        client.post(ActivityOut_Second.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_OutStore", params, new AsyncHttpResponseHandler() {
                                                                            @Override
                                                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                if (statusCode == 200) {
                                                                                    progressDialog.dismiss();
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
                                                                                    if ("\"成功\"".equals(info)) {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                        chukunum += 1;
                                                                                        DataSupport.deleteAll(ChukuInfo.class, "chejian = '03'");
                                                                                        List<ChukuInfo> chukuInfos = DataSupport.where("chepaihao = " + chepaihao).find(ChukuInfo.class);
                                                                                        if (!chukuInfos.isEmpty()) {
                                                                                            for (ChukuInfo chukuInfo : chukuInfos
                                                                                                    ) {
                                                                                                Map<String, String> map = new HashMap<String, String>();
                                                                                                map.put("name", chukuInfo.getNama());
                                                                                                map.put("bianma", chukuInfo.getBianma());
                                                                                                map.put("tiaoma", chukuInfo.getTiaoma());
                                                                                                map.put("weight", chukuInfo.getWeight());
                                                                                                map.put("length", chukuInfo.getLenght());
                                                                                                map.put("kezhong", chukuInfo.getKezhong());
                                                                                                map.put("fukuan", chukuInfo.getFukuan());
                                                                                                map.put("chejian", chukuInfo.getChejian());
                                                                                                lists.add(map);
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");
                                                                                        } else {
                                                                                            lists.clear();
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");

                                                                                        }
                                                                                        if (chukunum == accountnum) {
                                                                                            showToast("全部出库成功!");
                                                                                            MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                            finish();
                                                                                        }
                                                                                    } else {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                        showToast(info);
                                                                                    }
                                                                                    barcodes = null;
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                                progressDialog.dismiss();
                                                                                MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                showToast(error.toString());
                                                                            }
                                                                        });
                                                                    }
                                                                    if (strck_04 > 0) {
                                                                        detail_str = detail_str_04.substring(0, detail_str_04.length() - 1);
                                                                        content_str = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + "04" + "," + ghs + "," + kehu + "," + chepaihao;
                                                                        RequestParams params_01 = new RequestParams();
                                                                        params.put("Content", content_str);
                                                                        params.put("Detail", detail_str);
                                                                        Log.d("zhang", content_str);
                                                                        Log.d("zhang", detail_str);
                                                                        //设置重复请求次数，间隔
                                                                        client.setMaxRetriesAndTimeout(3, 2000);
                                                                        //设置超时时间，默认10s
                                                                        client.setTimeout(5 * 1000);
                                                                        client.post(ActivityOut_Second.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_OutStore", params, new AsyncHttpResponseHandler() {
                                                                            @Override
                                                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                if (statusCode == 200) {
                                                                                    progressDialog.dismiss();
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
                                                                                    if ("\"成功\"".equals(info)) {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                        chukunum += 1;
                                                                                        DataSupport.deleteAll(ChukuInfo.class, "chejian = '04'");
                                                                                        List<ChukuInfo> chukuInfos = DataSupport.where("chepaihao = " + chepaihao).find(ChukuInfo.class);
                                                                                        if (!chukuInfos.isEmpty()) {
                                                                                            for (ChukuInfo chukuInfo : chukuInfos
                                                                                                    ) {
                                                                                                Map<String, String> map = new HashMap<String, String>();
                                                                                                map.put("name", chukuInfo.getNama());
                                                                                                map.put("bianma", chukuInfo.getBianma());
                                                                                                map.put("tiaoma", chukuInfo.getTiaoma());
                                                                                                map.put("weight", chukuInfo.getWeight());
                                                                                                map.put("length", chukuInfo.getLenght());
                                                                                                map.put("kezhong", chukuInfo.getKezhong());
                                                                                                map.put("fukuan", chukuInfo.getFukuan());
                                                                                                map.put("chejian", chukuInfo.getChejian());
                                                                                                lists.add(map);
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");
                                                                                        } else {
                                                                                            lists.clear();
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");

                                                                                        }
                                                                                        if (chukunum == accountnum) {
                                                                                            showToast("全部出库成功!");
                                                                                            MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                            finish();
                                                                                        }
                                                                                    } else {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                        showToast(info);
                                                                                    }
                                                                                    barcodes = null;
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                                progressDialog.dismiss();
                                                                                MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                showToast(error.toString());
                                                                            }
                                                                        });
                                                                    }
                                                                    if (strck_05 > 0) {
                                                                        detail_str = detail_str_05.substring(0, detail_str_05.length() - 1);
                                                                        content_str = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + "05" + "," + ghs + "," + kehu + "," + chepaihao;
                                                                        RequestParams params_01 = new RequestParams();
                                                                        params.put("Content", content_str);
                                                                        params.put("Detail", detail_str);
                                                                        Log.d("zhang", content_str);
                                                                        Log.d("zhang", detail_str);
                                                                        //设置重复请求次数，间隔
                                                                        client.setMaxRetriesAndTimeout(3, 2000);
                                                                        //设置超时时间，默认10s
                                                                        client.setTimeout(5 * 1000);
                                                                        client.post(ActivityOut_Second.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_OutStore", params, new AsyncHttpResponseHandler() {
                                                                            @Override
                                                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                if (statusCode == 200) {
                                                                                    progressDialog.dismiss();
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
                                                                                    if ("\"成功\"".equals(info)) {

                                                                                        chukunum += 1;
                                                                                        DataSupport.deleteAll(ChukuInfo.class, "chejian = '05'");
                                                                                        List<ChukuInfo> chukuInfos = DataSupport.where("chepaihao = " + chepaihao).find(ChukuInfo.class);
                                                                                        if (!chukuInfos.isEmpty()) {
                                                                                            for (ChukuInfo chukuInfo : chukuInfos
                                                                                                    ) {
                                                                                                Map<String, String> map = new HashMap<String, String>();
                                                                                                map.put("name", chukuInfo.getNama());
                                                                                                map.put("bianma", chukuInfo.getBianma());
                                                                                                map.put("tiaoma", chukuInfo.getTiaoma());
                                                                                                map.put("weight", chukuInfo.getWeight());
                                                                                                map.put("length", chukuInfo.getLenght());
                                                                                                map.put("kezhong", chukuInfo.getKezhong());
                                                                                                map.put("fukuan", chukuInfo.getFukuan());
                                                                                                map.put("chejian", chukuInfo.getChejian());
                                                                                                lists.add(map);
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");
                                                                                        } else {
                                                                                            lists.clear();
                                                                                            adapter.notifyDataSetChanged();
                                                                                            sacnNum.setText("已扫描：" + lists.size() + "件");

                                                                                        }

                                                                                        if (chukunum == accountnum) {
                                                                                            showToast("全部出库成功!");
                                                                                            MediaPlayer.create(ActivityOut_Second.this, R.raw.chu_suc).start();
                                                                                            finish();
                                                                                        }
                                                                                    } else {
                                                                                        MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                        showToast(info);
                                                                                    }
                                                                                    barcodes = null;
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                                progressDialog.dismiss();
                                                                                MediaPlayer.create(ActivityOut_Second.this, R.raw.fail).start();
                                                                                showToast(error.toString());
                                                                            }
                                                                        });
                                                                    }
//                                                        StringBuilder detail_builder = new StringBuilder();
//                                                        if (lists.size() > 1) {
//                                                            for (int i = 0; i < lists.size(); i++) {
//                                                                String bianma_str = lists.get(i).get("bianma");
//                                                                String tiaoma_str = lists.get(i).get("tiaoma");
//                                                                String weight_str = lists.get(i).get("weight");
//                                                                String length_str = lists.get(i).get("length");
//                                                                detail_builder.append(bianma_str + "," + tiaoma_str + "," + weight_str + "," + length_str + "|");
//                                                            }
//                                                            detail_str = detail_builder.substring(0, detail_builder.length() - 1);
//                                                        } else {
//                                                            String bianma_str = lists.get(0).get("bianma");
//                                                            String tiaoma_str = lists.get(0).get("tiaoma");
//                                                            String weight_str = lists.get(0).get("weight");
//                                                            String length_str = lists.get(0).get("length");
//                                                            detail_str = bianma_str + "," + tiaoma_str + "," + weight_str + "," + length_str;
//                                                        }
                                                                    progressDialog.setTitle("上传中...");
                                                                    progressDialog.setMessage("正在上传数据，请稍后...");
                                                                    progressDialog.setCancelable(true);
                                                                    progressDialog.show();
                                                                } else {
                                                                    showToast("输入错误，请重新操作!");
                                                                }

                                                            }
                                                        });
                                                        nextBuilder.setNegativeButton("取消", null);
                                                        nextBuilder.create().show();


                                                    }
                                                });
                                                builder.create().show();
                                            } else {
                                                outEditText.setShakeAnimation();
                                                showToast("配货单信息不能为空");
                                            }

                                        }
                                    }

        );

//        for (int i = 0; i < 10; i++) {
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", "施胶瓦纸" + i);
//            map.put("bianma", "10002" + i);
//            map.put("tiaoma", "15092520002" + i);
//            map.put("weight", "552" + i);
//            map.put("length", "1300" + i);
//
//            lists.add(map);
//        }
        adapter = new SimpleAdapter(this, lists, R.layout.item_paper, new String[]{"name", "chejian", "kezhong", "fukuan", "weight"}, new int[]{R.id.text_name, R.id.text_chejian, R.id.text_kezhong, R.id.text_fukuan, R.id.text_weight});
        outGongyingshangList.setAdapter(adapter);
        outGongyingshangList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityOut_Second.this, ActivityOut_detail.class);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOut_Second.this);
                builder.setIcon(R.mipmap.circle);
                builder.setTitle("提示信息");
                builder.setMessage("是否删除该条码" + lists.get(position).get("tiaoma") + "?\n" + lists.get(position).get("name") + "\n克重:" + lists.get(position).get("kezhong") + "/幅宽:" + lists.get(position).get("fukuan") + "\n重量:" + lists.get(position).get("weight"));
                builder.setNegativeButton("否", null);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DataSupport.deleteAll(ChukuInfo.class, "tiaoma = " + lists.get(position).get("tiaoma"));
                        lists.remove(position);
                        sacnNum.setText("已扫描：" + lists.size() + "件");
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.create().show();

                return false;


            }
        });
//        outEditText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP) {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//
//
//
//                        return true;
//                    }
//                }
//                return false;
//            }
//
//        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityOut_Second.this);
        exitbuilder.setTitle("系统提示");
        if (lists.isEmpty()) {
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
        } else {
            exitbuilder.setMessage("您还有未上传的出库信息，是否要继续退出?");
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
        }
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

    private void initScan() {
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private final static String SCAN_ACTION = "urovo.rcv.message";// 扫描结束action
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr = "0";
    private String[] barcodes = null;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            // id_EditText.setText("");

            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            Log.i("debug", "----codetype--" + temp);
            try {
                // byte转码GBK
                barcodeStr = new String(barcode, 0, barocodelen, "GBK");
                barcodes = barcodeStr.split("\\|");
                if (barcodes.length > 10) {
//                    for (String str : barcodes
//                            ) {
//                        Log.d("wan", str);
//                    }
//                    if (ck.equals(barcodes[7])) {
                    if (!lists.toString().contains(barcodes[1])) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", barcodes[2]);
                        map.put("bianma", barcodes[0]);
                        map.put("tiaoma", barcodes[1]);
                        map.put("weight", barcodes[5]);
                        map.put("length", barcodes[6]);
                        map.put("kezhong", barcodes[4]);
                        map.put("fukuan", barcodes[3]);
                        map.put("chejian", barcodes[7]);
                        lists.add(map);
                        adapter.notifyDataSetChanged();
                        outEditText.setText(barcodes[1]);

                        ChukuInfo chukuInfo;
                        if (DataSupport.where("tiaoma = " + barcodes[1]).find(ChukuInfo.class).size() > 0) {
                            chukuInfo = DataSupport.where("tiaoma = " + barcodes[1]).find(ChukuInfo.class).get(0);
                        } else {
                            chukuInfo = new ChukuInfo();
                        }
                        chukuInfo.setStatus("1");

                        chukuInfo.setBianma(barcodes[0]);
                        chukuInfo.setTiaoma(barcodes[1]);
                        chukuInfo.setNama(barcodes[2]);
                        chukuInfo.setFukuan(barcodes[3]);
                        chukuInfo.setKezhong(barcodes[4]);
                        chukuInfo.setWeight(barcodes[5]);
                        chukuInfo.setLenght(barcodes[6]);
                        chukuInfo.setChejian(barcodes[7]);
                        chukuInfo.setBanzu(barcodes[8]);
                        chukuInfo.setTime(barcodes[9]);
                        chukuInfo.setCaozuoren(barcodes[10]);

                        chukuInfo.setChepai(chepaihao);
                        chukuInfo.setKehu(kehu);
                        chukuInfo.setGhs(ghs);
                        chukuInfo.save();

                    } else {
                        showToast("该条码已添加!");
                        MediaPlayer.create(ActivityOut_Second.this, R.raw.tiaoma_added).start();
                    }
//                    } else {
//                        MediaPlayer.create(ActivityOut_Second.this, R.raw.cangku_err).start();
//                        showToast("该条码对应车间(仓库)有误!");
//                    }

                } else {
                    showToast("条码格式不正确!");
                    MediaPlayer.create(ActivityOut_Second.this, R.raw.tiaoma_err).start();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sacnNum.setText("已扫描：" + lists.size() + "件");

        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
        }
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScan();
        // showScanResult.setText("");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }


}
