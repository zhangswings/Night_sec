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
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * 查询产品信息
 * 1.扫描条码直接查询
 * 2.输入条码，点击查询按钮进行查询
 * 3.查询的信息包括：条码、物料名称、重量、克重、长度、幅宽、操作人、状态、仓库、时间、客户信息等
 */
public class ActivityInfo extends AppCompatActivity {

    @InjectView(R.id.info_editText)
    ClearEditText infoEditText;
    @InjectView(R.id.info_btn_back)
    Button infoBtnBack;
    @InjectView(R.id.info_btn_ok)
    Button infoBtnOk;
//    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.info_text)
    TextView infoText;
    @InjectView(R.id.info_ll_content)
    LinearLayout infoLlContent;
    @InjectView(R.id.info_ll_bottom)
    LinearLayout infoLlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_info);
        ButterKnife.inject(this);
        builder=new StringBuilder();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        client = new AsyncHttpClient();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ip_str=preferences.getString("ip", "192.168.0.187");
        progressDialog = new ProgressDialog(ActivityInfo.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                client.cancelRequests(ActivityInfo.this, true);
                showToast("已取消上传");
                MyClient.cancleClient(ActivityInfo.this);
            }
        });
        // 初始化振动器
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        infoBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        infoBtnOk.setText("查询");
        infoBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoText.setText("");
                if (TextUtils.isEmpty(infoEditText.getText()) || infoEditText.getText().toString().isEmpty()) {
                    infoEditText.setShakeAnimation();
                    //设置提示
                    showToast("查询信息不能为空!");
                } else {
                    RequestParams params = new RequestParams();
                    params.put("id", infoEditText.getText().toString().trim());
//                    client.cancelRequests(ActivityInfo.this, true);
                    MyClient.post( ActivityInfo.this,"http://" + ip_str + ":8092/JsonHandler.ashx?doc=GetMaterialStatus", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
//                                String info = "";
                                String text = new String(responseBody);
                                Log.d("zhang", text + ">>>>>zhang");
//                                try {
//                                    Document document = DocumentHelper.parseText(text);
//                                    Element element = document.getRootElement();
//                                    info = element.getText();
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<MaterialStatus>>() {
                                    }.getType();
                                    List<MaterialStatus> materialStatus = gson.fromJson(text, type);
                                    String[] temp = new String[materialStatus.size()];
                                    if (materialStatus.size() > 0) {
                                        StringBuilder builder=new StringBuilder();

                                        builder.append("操作人：").append( materialStatus.get(0).getOperaName()).append("\n状态：").append(materialStatus.get(0).getStatus()).append("\n仓库：").append(materialStatus.get(0).getStoreId()).append("\n时间：").append(materialStatus.get(0).getTime());
                                        if("已出库".equals(materialStatus.get(0).getStatus().trim())){
                                            builder.append("\n客户信息：").append(materialStatus.get(0).getSupplierName());
                                            Log.d("zhang",builder.toString());
                                        }


                                        infoText.setText(builder);
                                    } else {
                                        showToast("所查询条码信息不存在");
                                        infoText.setText("所查询条码信息不存在");
                                    }

//                                } catch (DocumentException de) {
//                                    Log.e("de", de.toString());
//                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            showToast(error.toString());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityInfo.this);
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
    StringBuilder builder;
    private String ip_str;
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
            infoEditText.setText("");
            infoText.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            Log.i("debug", "----codetype--" + temp);
            builder.delete(0, builder.length());
            try {
                // byte转码GBK
                barcodeStr = new String(barcode, 0, barocodelen, "GBK");
                barcodes = barcodeStr.split("\\|");
                if (barcodes.length > 10) {
//                    for (String str : barcodes
//                            ) {
//                        Log.d("wan", str);
//                    }
                   infoText.setText(builder);
                    infoEditText.setText(barcodes[1]);
                    RequestParams params = new RequestParams();
                    params.put("id", barcodes[1]);
//                    client.cancelRequests(ActivityInfo.this, true);
                    MyClient.cancleClient(ActivityInfo.this);
                    MyClient.post(ActivityInfo.this,"http://" + ip_str + ":8092/JsonHandler.ashx?doc=GetMaterialStatus", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
//                                String info = "";
                                String text = new String(responseBody);
                                Log.d("zhang", text + ">>>>>zhang");
//                                try {
//                                    Document document = DocumentHelper.parseText(text);
//                                    Element element = document.getRootElement();
//                                    info = element.getText();
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<MaterialStatus>>() {
                                }.getType();
                                List<MaterialStatus> materialStatus = gson.fromJson(text, type);
                                String[] temp = new String[materialStatus.size()];
                                if (materialStatus.size() > 0) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("条码：").append(barcodes[1]).append("\n物料：").append(barcodes[2]).append("\n重量：").append(barcodes[5]).append("\n克重：").append(barcodes[4]).append("\n长度：").append(barcodes[6]).append("\n幅宽：").append(barcodes[3]).append("\n");

                                    builder.append("操作人：").append(materialStatus.get(0).getOperaName()).append("\n状态：").append(materialStatus.get(0).getStatus()).append("\n仓库：").append(materialStatus.get(0).getStoreId()).append("\n时间：").append(materialStatus.get(0).getTime());
                                    if ("已出库".equals(materialStatus.get(0).getStatus().trim())) {
                                        builder.append("\n客户信息：").append(materialStatus.get(0).getSupplierName());
                                        Log.d("zhang", builder.toString());
                                    }
                                    infoText.setText(builder);
                                } else {
                                    showToast("所查询条码信息不存在");
                                }

//                                } catch (DocumentException de) {
//                                    Log.e("de", de.toString());
//                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            showToast(error.toString());
                        }
                    });
                } else {
                    showToast("条码格式不正确!");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


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

    private void initScan() {
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
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
