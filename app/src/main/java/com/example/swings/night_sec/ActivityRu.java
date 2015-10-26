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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swings.night_sec.module.Papers;
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
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * 入库操作
 */
public class ActivityRu extends AppCompatActivity {
    //编码
    @InjectView(R.id.edit_text_ru_bianma)
    EditText editTextRuBianma;
    //条码
    @InjectView(R.id.edit_text_ru_code)
    EditText editTextRuCode;
    //名称
    @InjectView(R.id.edit_text_ru_name)
    EditText editTextRuName;
    //幅宽
    @InjectView(R.id.edit_text_ru_fukuan)
    EditText editTextRuFukuan;
    //克重
    @InjectView(R.id.edit_text_ru_kezhong)
    EditText editTextRuKezhong;
    //重量
    @InjectView(R.id.edit_text_ru_weight)
    EditText editTextRuWeight;
    //长度
    @InjectView(R.id.edit_text_ru_length)
    EditText editTextRuLength;
    //车间&班组
    @InjectView(R.id.edit_text_ru_cjbz)
    EditText editTextRuCjbz;
    //时间
    @InjectView(R.id.edit_text_ru_shijian)
    EditText editTextRuShijian;
    //用户
    @InjectView(R.id.edit_text_ru_yonghu)
    EditText editTextRuYonghu;
    //取消按钮
    @InjectView(R.id.ru_btn_cancle)
    Button ruBtnCancle;
    //确认按钮
    @InjectView(R.id.ru_btn_ok)
    Button ruBtnOk;
    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_activity_ru);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.inject(this);
        client = new AsyncHttpClient();
        //设置重复请求次数，间隔
        client.setMaxRetriesAndTimeout(5, 3000);
        client.setTimeout(3000);
        //设置超时时间，默认10s
        //设置连接超时时间为2秒（连接初始化时间）
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
        });

        //代码开始
        progressDialog = new ProgressDialog(ActivityRu.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                client.cancelRequests(ActivityRu.this, true);
                showToast("已取消上传");
            }
        });

        ruBtnCancle.setText("清空");
        ruBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityRu.this);
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

                if (!TextUtils.isEmpty(editTextRuBianma.getText()) && !TextUtils.isEmpty(editTextRuCode.getText()) && !TextUtils.isEmpty(editTextRuName.getText()) && !TextUtils.isEmpty(editTextRuKezhong.getText()) && !TextUtils.isEmpty(editTextRuWeight.getText()) && !TextUtils.isEmpty(editTextRuLength.getText()) && !TextUtils.isEmpty(editTextRuCjbz.getText()) && !TextUtils.isEmpty(editTextRuShijian.getText()) && !TextUtils.isEmpty(editTextRuYonghu.getText())) {
                    progressDialog.setTitle("上传中...");
                    progressDialog.setMessage("正在上传数据，请稍后...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    getEditText();
                    RequestParams params = new RequestParams();
                    //传递参数
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String builder_Content = preferences.getString("user", "admin") + "," + format.format(new Date()) + "," + chejian_str + "," + banzu_str;
                    //Content:(oper,time,store,group) 操作人、上传时间、仓库(车间)、班组
                    Log.d("content", builder_Content);
                    String builder_Detail = bianma_str + "," + code_str + "," + weight_str + "," + lenght_str;
                    Log.d("detail", builder_Detail);
                    //Detail:(bianma,tiaoma,weight,lenght) 编码、条码、重量、长度
                    params.put("Content", builder_Content);
                    params.put("Detail", builder_Detail);
                    client.post(ActivityRu.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/PDA_InStore", params, new AsyncHttpResponseHandler() {


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
                                    if ("\"成功\"".equals(info)){
                                        mediaPlayer=MediaPlayer.create(ActivityRu.this,R.raw.ru_suc);
                                        mediaPlayer.start();
                                        Log.d("tag","成功");
                                    }else{
                                        mediaPlayer=MediaPlayer.create(ActivityRu.this,R.raw.fail);
                                        mediaPlayer.start();
                                        Log.d("tag", "失败");
                                    }

                                } catch (DocumentException de) {
                                    Log.e("de", de.toString());
                                }
                                mediaPlayer=MediaPlayer.create(ActivityRu.this,R.raw.ru_suc);
                                mediaPlayer.start();
                                showToast(info);
                                barcodes = null;
                                clearEditText();
                                clearString();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            progressDialog.dismiss();

                            AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityRu.this);
                            exitbuilder.setTitle("系统提示");
                            exitbuilder.setMessage("未上传成功!\n网络错误,是否将该条码信息保存到本地?");
                            exitbuilder.setIcon(R.mipmap.circle);
                            exitbuilder.setCancelable(false);
                            exitbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /**
                                     *纸类信息
                                     */
                                    if (DataSupport.where("paper_code=" + barcodes[1]).find(Papers.class).isEmpty()) {
                                        Papers paper = new Papers();
                                        paper.setPaper_tiaoma(barcodes[0]);
                                        paper.setPaper_code(barcodes[1]);
                                        paper.setPaper_name(barcodes[2]);
                                        paper.setPaper_fukuan(barcodes[3]);
                                        paper.setPaper_kezhong(barcodes[4]);
                                        paper.setPaper_weight(barcodes[5]);
                                        paper.setPaper_length(barcodes[6]);
                                        paper.setPaper_chejian(barcodes[7]);
                                        paper.setPaper_banzu(barcodes[8]);
                                        paper.setPaper_code_time(barcodes[9]);
                                        paper.setPaper_user(barcodes[10]);
                                        paper.setPaper_status("0");
                                        if (paper.save()) {
                                            showToast(barcodes[1] + "保存成功！");

                                        }
                                    } else {
                                        showToast("该条码已存在");
                                    }
                                }
                            });
                            exitbuilder.setNegativeButton("取消", null);
                            // exitbuilder.create();
                            exitbuilder.show();

                        }
                    });


                } else {
                    showToast("数据项不能有空值");
                }
            }
        });
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
                if (barcodes.length > 10) {
                    if (barcodes[7].equals(getIntent().getStringExtra("ruku"))) {
                        editTextRuBianma.setText(barcodes[0]);
                        editTextRuCode.setText(barcodes[1]);
                        editTextRuName.setText(barcodes[2]);
                        editTextRuFukuan.setText(barcodes[3]);
                        editTextRuKezhong.setText(barcodes[4]);
                        editTextRuWeight.setText(barcodes[5]);
                        editTextRuLength.setText(barcodes[6]);
                        editTextRuCjbz.setText(barcodes[7] + "/" + barcodes[8]);
                        editTextRuShijian.setText(barcodes[9]);
                        editTextRuYonghu.setText(barcodes[10]);
                    } else {
                        showToast("条码对应车间(仓库)不符!");
                        barcodes = null;
                    }
                } else {
                    showToast("条码格式不对！");
//                    Toast.makeText(ActivityRu.this, "条码格式不对！", Toast.LENGTH_LONG).show();
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
    private String chejian_str;
    private String banzu_str;
    private String shijian_str;
    private String yonghu_str;

    private void getEditText() {
        bianma_str = editTextRuBianma.getText().toString();
        code_str = editTextRuCode.getText().toString();
        name_str = editTextRuName.getText().toString();
        fukuan_str = editTextRuFukuan.getText().toString();
        kezhong_str = editTextRuKezhong.getText().toString();
        weight_str = editTextRuWeight.getText().toString();
        lenght_str = editTextRuLength.getText().toString();
        String[] strs = editTextRuCjbz.getText().toString().split("\\/");
        chejian_str = strs[0];
        banzu_str = strs[1];
        shijian_str = editTextRuShijian.getText().toString();
        yonghu_str = editTextRuYonghu.getText().toString();

    }

    private void clearEditText() {
        editTextRuBianma.setText("");
        editTextRuCode.setText("");
        editTextRuName.setText("");
        editTextRuFukuan.setText("");
        editTextRuKezhong.setText("");
        editTextRuWeight.setText("");
        editTextRuLength.setText("");
        editTextRuCjbz.setText("");
        editTextRuShijian.setText("");
        editTextRuYonghu.setText("");
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
        shijian_str = "";
        yonghu_str = "";
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
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityRu.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("是否继续退出?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                client.cancelRequests(ActivityRu.this, true);
                finish();
            }
        });
        exitbuilder.setNegativeButton("否", null);
        // exitbuilder.create();
        exitbuilder.show();
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
