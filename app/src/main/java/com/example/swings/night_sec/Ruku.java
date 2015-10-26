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
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.Serializable;
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

public class Ruku extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.pre_btn)
    Button preBtn;
    @InjectView(R.id.pre_editText)
    ClearEditText preEditText;
    @InjectView(R.id.pre_ll)
    LinearLayout preLl;
    @InjectView(R.id.this_btn)
    Button thisBtn;
    @InjectView(R.id.this_editText)
    ClearEditText thisEditText;
    @InjectView(R.id.this_ll)
    LinearLayout thisLl;
    @InjectView(R.id.edit_text_ru_bianma)
    EditText editTextRuBianma;
    @InjectView(R.id.edit_text_ru_code)
    EditText editTextRuCode;
    @InjectView(R.id.edit_text_ru_name)
    EditText editTextRuName;
    @InjectView(R.id.edit_text_ru_fukuan)
    EditText editTextRuFukuan;
    @InjectView(R.id.edit_text_ru_kezhong)
    EditText editTextRuKezhong;
    @InjectView(R.id.edit_text_ru_weight)
    EditText editTextRuWeight;
    @InjectView(R.id.edit_text_ru_length)
    EditText editTextRuLength;
    @InjectView(R.id.edit_text_ru_cjbz)
    EditText editTextRuCjbz;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;
    @InjectView(R.id.scan_num)
    TextView scanNum;
    @InjectView(R.id.ip_btn_back)
    Button ruBtnCancle;
    @InjectView(R.id.ip_btn_ok)
    Button ruBtnOk;
    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    private List<Map<String, String>> lists;
    private String chejian_str;
    private String banzu_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ruku);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.inject(this);
        lists = new ArrayList<Map<String, String>>();
        client = new AsyncHttpClient();
        //设置重复请求次数，间隔
        client.setMaxRetriesAndTimeout(3, 2000);
        //设置超时时间，默认10s
        client.setTimeout(5 * 1000);
        //设置连接超时时间为2秒（连接初始化时间）
        chejian_str = getIntent().getStringExtra("ruku");
        banzu_str = getIntent().getStringExtra("banzu");
        // 初始化振动器
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("快速入库:" + getIntent().getStringExtra("ruku"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        }); //代码开始
        progressDialog = new ProgressDialog(Ruku.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                client.cancelRequests(Ruku.this, true);
                showToast("已取消上传");
            }
        });

        ruBtnCancle.setText("清空");
        ruBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Ruku.this);
                exitbuilder.setTitle("系统提示");
                exitbuilder.setMessage("是否全部清空?");
                exitbuilder.setIcon(R.mipmap.circle);
                exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearEditText();
                    }
                });
                exitbuilder.setNegativeButton("取消", null);
                exitbuilder.show();

            }
        });
        ruBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void upload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Ruku.this);
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
                    String builder_Detail = "";
                    if (lists.size() == 1) {
                        builder_Detail = lists.get(0).get("bianma") + "," + lists.get(0).get("tiaoma") + "," + lists.get(0).get("weight") + "," + lists.get(0).get("length");

                    } else {
                        String detail_str = "";
                        for (int i = 0; i < lists.size(); i++) {
                            final int a = i;
                            final String tiaoma = lists.get(a).get("tiaoma");
                            detail_str += lists.get(i).get("bianma") + "," + lists.get(i).get("tiaoma") + "," + lists.get(i).get("weight") + "," + lists.get(i).get("length") + "|";

                        }
                        builder_Detail = detail_str.substring(0, detail_str.length() - 1);

                    }
                    final RequestParams params = new RequestParams();

                    //传递参数

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String builder_Content = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + chejian_str + "," + banzu_str;
//                                //Content:(oper,time,store,group) 操作人、上传时间、仓库(车间)、班组
                    Log.d("content", builder_Content);
                    Log.d("detail", builder_Detail);
//                                //Detail:(bianma,tiaoma,weight,lenght) 编码、条码、重量、长度
                    params.put("Content", builder_Content);
                    params.put("Detail", builder_Detail);
                    client.post(Ruku.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_InStore", params, new AsyncHttpResponseHandler() {


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

                                if ("\"成功\"".equals(info)) {
                                    MediaPlayer.create(Ruku.this, R.raw.ru_suc).start();
                                    clearEditText();
                                    showToast("全部上传成功!");
                                } else {
                                    MediaPlayer.create(Ruku.this, R.raw.fail).start();
                                    showToast(info);
                                }


                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            progressDialog.dismiss();

                            AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Ruku.this);
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
//                            lists.clear();
//                            adapter.notifyDataSetChanged();
                } else {
                    showToast("数据列表为空");
                }

            }
        });
        builder.create().show();
    }

    /**
     * 初始化扫描头
     */
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
            android.util.Log.i("debug", "----codetype--" + temp);
            try {
                // byte转码GBK
                barcodeStr = new String(barcode, 0, barocodelen, "GBK");
                barcodes = barcodeStr.split("\\|");
                if (barcodes.length > 10 && !lists.toString().contains(barcodes[1])) {
                    if (lists.toString().contains(barcodes[1])) {
                        showToast("该条码已添加!");
                    } else {

                        if (barcodes[7].equals(getIntent().getStringExtra("ruku"))) {
                            editTextRuBianma.setText(barcodes[0]);
                            editTextRuCode.setText(barcodes[1]);
                            editTextRuName.setText(barcodes[2]);
                            editTextRuFukuan.setText(barcodes[3]);
                            editTextRuKezhong.setText(barcodes[4]);
                            editTextRuWeight.setText(barcodes[5]);
                            editTextRuLength.setText(barcodes[6]);
                            editTextRuCjbz.setText(barcodes[7] + "/" + barcodes[8]);
//                        editTextRuShijian.setText(barcodes[9]);
//                        editTextRuYonghu.setText(barcodes[10]);

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("name", barcodes[2]);
                            map.put("bianma", barcodes[0]);
                            map.put("tiaoma", barcodes[1]);
                            map.put("weight", barcodes[5]);
                            map.put("length", barcodes[6]);
                            map.put("kezhong", barcodes[4]);
                            map.put("fukuan", barcodes[3]);
                            map.put("chejian", barcodes[7]);
                            map.put("banzu", barcodes[8]);
                            map.put("tag", String.valueOf(lists.size()));
                            lists.add(map);
                            thisEditText.setText(barcodes[1]);
                            if (lists.size() > 1) {
                                preEditText.setText(lists.get(lists.size() - 2).get("tiaoma"));
                            }
                            scanNum.setText(String.valueOf(lists.size()) + "件");
                        } else {
                            showToast("条码对应车间(仓库)不符!");
                            barcodes = null;
                        }
                    }
                } else {
                    showToast("条码格式不对！");
                    barcodes = null;
//                    Toast.makeText(RuKu.this, "条码格式不对！", Toast.LENGTH_LONG).show();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }

    };

    private String bianma_str;
    private String code_str;
    private String name_str;
    private String fukuan_str;
    private String kezhong_str;
    private String weight_str;
    private String lenght_str;

    private void getEditText() {
        bianma_str = editTextRuBianma.getText().toString();
        code_str = editTextRuCode.getText().toString();
        name_str = editTextRuName.getText().toString();
        fukuan_str = editTextRuFukuan.getText().toString();
        kezhong_str = editTextRuKezhong.getText().toString();
        weight_str = editTextRuWeight.getText().toString();
        lenght_str = editTextRuLength.getText().toString();
        String[] strs = editTextRuCjbz.getText().toString().split("\\/");
//        shijian_str = editTextRuShijian.getText().toString();
//        yonghu_str = editTextRuYonghu.getText().toString();

    }

    private void clearEditText() {
        lists.clear();
        scanNum.setText(String.valueOf(lists.size()) + "件");
        thisEditText.setText("");
        preEditText.setText("");
        editTextRuBianma.setText("");
        editTextRuCode.setText("");
        editTextRuName.setText("");
        editTextRuFukuan.setText("");
        editTextRuKezhong.setText("");
        editTextRuWeight.setText("");
        editTextRuLength.setText("");
        editTextRuCjbz.setText("");
//        editTextRuShijian.setText("");
//        editTextRuYonghu.setText("");
    }

    private void clearString() {
        bianma_str = "";
        code_str = "";
        name_str = "";
        fukuan_str = "";
        kezhong_str = "";
        weight_str = "";
        lenght_str = "";
        chejian_str = "";
        banzu_str = "";
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

    @Override
    public void onBackPressed() {
        if (lists.isEmpty()) {
            AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Ruku.this);
            exitbuilder.setTitle("系统提示");
            exitbuilder.setMessage("是否继续退出?");
            exitbuilder.setIcon(R.mipmap.circle);
            exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    client.cancelRequests(Ruku.this, true);
                    finish();
                }
            });
            exitbuilder.setNegativeButton("取消", null);
            // exitbuilder.create();
            exitbuilder.show();
        } else {
            AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Ruku.this);
            exitbuilder.setTitle("系统提示");
            exitbuilder.setMessage("扫描信息还未上传，是否需要立刻上传吗?");
            exitbuilder.setIcon(R.mipmap.circle);
            exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    upload();
                    // TODO Auto-generated method stub
                }
            });
            exitbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    client.cancelRequests(Ruku.this, true);
                    finish();
                }
            });
            // exitbuilder.create();
            exitbuilder.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_ru, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(Ruku.this, Ruku_Detail.class);
            intent.putExtra("ruku_list", (Serializable) lists);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
