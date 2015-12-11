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

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * 修改产品重量信息
 * 1.扫描条码直接查询
 * 2.输入条码，点击查询按钮进行查询，对比查询产品和数据库信息
 * 3.查询的信息包括：条码、物料名称、重量、克重、长度、幅宽、操作人、状态、仓库、时间、客户信息等
 * 4.如果已经入库，提示是否更新入库重量
 * 5.输入更新操作密码，只能两次数据库操作
 * <p/>
 * http://IP:8092/JsonHandler.ashx?doc=GetUserInfo&Id=9001&pwd=123
 * GetChangeWeight  Id 编码  weight重量 pwd 更新密码
 */
public class ActivityChangeWeight extends AppCompatActivity {

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
    @InjectView(R.id.info_weight)
    ClearEditText infoWeight;
    @InjectView(R.id.info_password)
    ClearEditText infoPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        ButterKnife.inject(this);
        builder = new StringBuilder();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("修改入库重量");
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
        ip_str = preferences.getString("ip", "192.168.0.187");
        progressDialog = new ProgressDialog(ActivityChangeWeight.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                client.cancelRequests(ActivityChangeWeight.this, true);
                MyClient.cancleClient(ActivityChangeWeight.this);
                showToast("已取消上传");
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

        infoBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新操作

                if (TextUtils.isEmpty(infoWeight.getText())) {
                    infoWeight.setShakeAnimation();
                    showToast("修改重量不能为空！");
                } else if (TextUtils.isEmpty(infoPassword.getText())) {
                    infoPassword.setShakeAnimation();
                    showToast("修改密码不能为空！");
                } else {
                    final String weight = infoWeight.getText().toString().trim();
                    final String password = infoPassword.getText().toString().trim();
                    AlertDialog.Builder builder_upload = new AlertDialog.Builder(ActivityChangeWeight.this);
                    builder_upload.setIcon(R.mipmap.right);
                    builder_upload.setTitle("系统提示");
                    builder_upload.setMessage("请确认是否上传信息？");
                    //上传更新重量信息
                    builder_upload.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestParams params = new RequestParams();
                            params.put("Id", infoEditText.getText().toString().trim());
                            params.put("weight", weight);
                            params.put("pwd", password);
//                            client.cancelRequests(ActivityChangeWeight.this, true);
                            MyClient.cancleClient(ActivityChangeWeight.this);
                            MyClient.post(ActivityChangeWeight.this, "http://" + ip_str + ":8092/JsonHandler.ashx?doc=GetChangeWeight", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
//                                            String info = "";
                                        String text = new String(responseBody);
                                        Log.d("zhang", text + ">>>>>zhang");
//                                            try {
//                                                Document document = DocumentHelper.parseText(text);
//                                                Element element = document.getRootElement();
//                                                info = element.getText();
                                        switch (text) {
                                            case "\"-1\"":
                                                showToast("数据库操作失败！");
                                                infoPassword.setShakeAnimation();
                                                infoWeight.setShakeAnimation();
                                                infoText.setText("数据库操作失败！");
                                                break;
                                            case "\"0\"":
                                                showToast("上传成功！");
                                                infoText.setText("上传成功！");
                                                clearEdt();
                                                break;
                                            case "\"1\"":
                                                showToast("密码错误！");
                                                infoPassword.setShakeAnimation();
                                                infoText.setText("密码错误！");
                                                break;
                                            case "\"3\"":
                                                showToast("更新频繁禁止！");
                                                infoText.setText("更新频繁禁止！");
                                                infoPassword.setShakeAnimation();
                                                infoWeight.setShakeAnimation();
                                                break;
                                            default:
                                                break;
                                        }

//                                            } catch (DocumentException de) {
//                                                Log.e("de", de.toString());
//                                            }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    showToast(error.toString());
                                }
                            });

                        }
                    });
                    builder_upload.setNegativeButton("否", null);
                    builder_upload.create().show();
                }


            }
        });

//        infoText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialogInfo();
//            }
//        });
       /* infoEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {

                        infoText.setText("");
                        if (TextUtils.isEmpty(infoEditText.getText()) || infoEditText.getText().toString().isEmpty()) {
                            infoEditText.setShakeAnimation();
                            //设置提示
                            showToast("查询信息不能为空!");
                        } else {
                            RequestParams params = new RequestParams();
                            params.put("id", infoEditText.getText().toString().trim());
                            client.cancelRequests(ActivityChangeWeight.this, true);
                            client.post(ActivityChangeWeight.this, "http://" + ip_str + ":8092/JsonHandler.ashx?doc=GetInMaterialInfo", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
//                                String info = "";
                                        String text = new String(responseBody);
                                        Log.d("zhang", text + ">>>>>zhang");
                                        if (text.length() > 2) {
                                            infoWeight.setText(text.substring(1, text.length() - 1));
                                        } else {
                                            showToast("所查询条码信息不存在！");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    showToast(error.toString());
                                }
                            });
                        }
                    }
                }
                return false;
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityChangeWeight.this);
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
//                    client.cancelRequests(ActivityChangeWeight.this, true);
                    //获取已入库返回的重量信息
                    MyClient.cancleClient(ActivityChangeWeight.this);
                    MyClient.post( ActivityChangeWeight.this,"http://" + ip_str + ":8092/JsonHandler.ashx?doc=GetInMaterialInfo", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
//                                String info = "";
                                String text = new String(responseBody);
                                Log.d("zhang", text + ">>>>>zhang");
                                if (text.length() > 2) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("条码：").append(barcodes[1]).append("\n物料：").append(barcodes[2]).append("\n重量：").append(barcodes[5]).append("\n克重：").append(barcodes[4]).append("\n长度：").append(barcodes[6]).append("\n幅宽：").append(barcodes[3]).append("\n");
                                    infoText.setText(builder);
                                    final String weight_str = barcodes[5];
//                                    infoWeight.setText(text.substring(1, text.length() - 1));
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityChangeWeight.this);
                                    builder1.setTitle("修改重量提示");
                                    builder1.setMessage("该产品已入库！\n请确认是否修改产品重量?");
                                    builder1.setIcon(R.mipmap.right);
                                    builder1.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //显示修改重量编辑框、显示输入更新密码编辑框

                                            infoWeight.setText(weight_str);
//                showEdt();
                                        }
                                    });
                                    builder1.setNegativeButton("否", null);
                                    builder1.create().show();
                                } else {
                                    showToast("所查询条码信息不存在！");
                                }
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

    //密码框获取光标，密码框文本全选
    private void showEdt() {

        infoPassword.requestFocus();
        infoPassword.selectAll();
//        infoWeight.setVisibility(View.VISIBLE);
//        infoPassword.setVisibility(View.VISIBLE);
    }

    private void clearEdt() {
        //隐藏修改重量编辑框、输入更新密码编辑框
        infoEditText.setText("");
        infoWeight.setText("");
        infoText.setText("");
        infoPassword.setText("");
    }

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
