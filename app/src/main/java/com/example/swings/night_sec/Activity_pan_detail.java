package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swings.night_sec.module.Bianma;
import com.example.swings.night_sec.module.Pan;
import com.example.swings.night_sec.module.Tiaoma;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.litepal.crud.DataSupport;

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

/**
 * 盘点操作
 * 1.扫描条码
 * 2.查看扫描信息
 * 3.删除扫描条码
 * 4.离线保存已扫描信息
 * 5.上传盘点信息
 */
public class Activity_pan_detail extends AppCompatActivity {

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
//    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    private List<Map<String, String>> lists;
    private String chejian_str;
    private String pandian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ruku);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.inject(this);
        Pan pan = new Pan();
        pan.setUser(preferences.getString("user", "admin"));
        pan.setCangku(getIntent().getStringExtra("ck"));
        pan.setPan_id(getIntent().getStringExtra("pandian"));
//        pan.setStatus("0");
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        pan.setDate(new Date());
        pan.save();
        lists = new ArrayList<Map<String, String>>();
//        client = new AsyncHttpClient();
        //设置重复请求次数，间隔
//        client.setMaxRetriesAndTimeout(5, 2000);
        //设置超时时间，默认10s
//        client.setTimeout(3 * 2000);
        //设置连接超时时间为2秒（连接初始化时间）
        chejian_str = getIntent().getStringExtra("ck");
        pandian = getIntent().getStringExtra("pandian");
        // 初始化振动器
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("盘点单:" + getIntent().getStringExtra("pandian"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        }); //代码开始
        progressDialog = new ProgressDialog(Activity_pan_detail.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                client.cancelRequests(Activity_pan_detail.this, true);
                MyClient.cancleClient(Activity_pan_detail.this);
                showToast("已取消上传");
            }
        });

        ruBtnCancle.setText("清空");
        ruBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_pan_detail.this);
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
        ruBtnOk.setText("上传");
        ruBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pandian_str = getIntent().getStringExtra("pandian");
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pan_detail.this);
                builder.setIcon(R.mipmap.right);
                builder.setTitle("全部上传");
                builder.setMessage("请确认是否全部上次服务器?");
                builder.setNegativeButton("否", null);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获取产品信息，全部上次
                        //Todo
                        if (DataSupport.where("pid = " + pandian_str).find(Tiaoma.class).size() > 0) {
                            progressDialog.setTitle("上传中...");
                            progressDialog.setMessage("正在上传数据，请稍后...");
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                            String builder_Detail = "";
                            if (DataSupport.where("pid = " + getIntent().getStringExtra("pandian")).find(Tiaoma.class).size() == 1) {
                                builder_Detail = DataSupport.where("pid = " + getIntent().getStringExtra("pandian")).find(Tiaoma.class).get(0).getTiaoma_id();
                            } else {
                                String detail_str = "";
                                for (int i = 0; i < DataSupport.where("pid = " + getIntent().getStringExtra("pandian")).find(Tiaoma.class).size(); i++) {
                                    detail_str += DataSupport.where("pid = " + getIntent().getStringExtra("pandian")).find(Tiaoma.class).get(i).getTiaoma_id() + ",";

                                }
                                builder_Detail = detail_str.substring(0, detail_str.length() - 1);

                            }
                            final RequestParams params = new RequestParams();

                            //传递参数

                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Log.d("detail", builder_Detail);
                            Log.d("store", getIntent().getStringExtra("ck"));
                            //Detail:(bianma,tiaoma,weight,lenght) 编码、条码、重量、长度
                            params.put("store", getIntent().getStringExtra("ck"));
                            params.put("detail", builder_Detail);
                            MyClient.post(Activity_pan_detail.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/JsonHandler.ashx?doc=GetPD_Info", params, new AsyncHttpResponseHandler() {


                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    progressDialog.dismiss();
                                    if (statusCode == 200) {
                                        String info = "";
                                        String text = new String(responseBody);
//                                        Log.d("zhang", text + ">>>>>zhang");
//                                        try {
//                                            Document document = DocumentHelper.parseText(text);
//                                            Element element = document.getRootElement();
//                                            info = element.getText();
//
//                                        } catch (DocumentException de) {
//                                            Log.e("de", de.toString());
//                                        }

                                        if ("true".equals(text)) {
                                            clearEditText();
                                            showToast("全部上传成功!");
                                            DataSupport.deleteAll(Pan.class, "pan_id = " + getIntent().getStringExtra("pandian"));
//                                            DataSupport.where("pid = "+getIntent().getStringExtra("pandian")).de
                                            DataSupport.deleteAll(Tiaoma.class, "pid = " + getIntent().getStringExtra("pandian"));
                                            finish();
                                        } else {

                                            showToast(info);
                                        }


                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    progressDialog.dismiss();

                                    AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_pan_detail.this);
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
                    if (barcodes[7].equals(getIntent().getStringExtra("ck"))) {
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
                        Tiaoma tiaoma = new Tiaoma();
//                        if (DataSupport.where("bianma_id = '"+ barcodes[0]+"'").find(Bianma.class).isEmpty()){
//                            Bianma bianma=new Bianma();
//                            bianma.setBianma_id(barcodes[0]);
//                            bianma.setFukuan(barcodes[3]);
//                            bianma.setKezhong(barcodes[4]);
//                            bianma.setWuliao(barcodes[2]);
//                            bianma.setPan_id(getIntent().getStringExtra("pandian"));
//                            bianma.setNums(1);

                        tiaoma.setTiaoma_id(barcodes[1]);
                        tiaoma.setLength(barcodes[6]);
                        tiaoma.setWeight(barcodes[5]);
                        tiaoma.setBianma_id(barcodes[0]);
                        tiaoma.setBid(barcodes[0]);
                        tiaoma.setPid(getIntent().getStringExtra("pandian"));
//                            tiaoma.setBianma(bianma);
                        tiaoma.save();
//                            bianma.getBianma_tiaoma().add(tiaoma);
//                            bianma.save();
                        Pan pan = DataSupport.where("pan_id = " + getIntent().getStringExtra("pandian")).find(Pan.class).get(0);
//                            pan.getPan_bianma().add(bianma);
                        pan.setStatus("2");
                        pan.save();
//                        }else{
////                            Bianma bianma=DataSupport.where("bianma_id = "+ barcodes[0]).find(Bianma.class).get(0);
//                            tiaoma.setTiaoma_id(barcodes[1]);
//                            tiaoma.setLength(barcodes[6]);
//                            tiaoma.setWeight(barcodes[5]);
////                            tiaoma.setBianma(bianma);
//                            tiaoma.save();
////                           int nums= bianma.getNums()+1;
////                            bianma.setNums(nums);
////                            bianma.getBianma_tiaoma().add(tiaoma);
////                            bianma.save();
//                            Pan pan=DataSupport.where("pan_id = "+getIntent().getStringExtra("pandian")).find(Pan.class).get(0);
////                            pan.getPan_bianma().add(bianma);
//                            pan.setStatus("2");
//                            pan.save();
//                        }
                        if (lists.size() > 1) {
                            preEditText.setText(lists.get(lists.size() - 2).get("tiaoma"));
                        }
//                        int num=DataSupport.where("pan_id = "+getIntent().getStringExtra("pandian")).find(Bianma.class).get(0).getNums();
                        scanNum.setText(lists.size() + "件");
                    } else {
                        showToast("条码对应车间(仓库)不符!");
                        barcodes = null;
                    }
                } else {
                    if (lists.toString().contains(barcodes[1])) {
                        showToast("该条码已添加!");
                    } else {
                        showToast("条码格式不对！");
                    }
                    barcodes = null;
//                    Toast.makeText(Activity_pan_detail.this, "条码格式不对！", Toast.LENGTH_LONG).show();
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
        scanNum.setText("0件");
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
//        DataSupport.deleteAll(Pan.class,"pan_id = "+getIntent().getStringExtra("pandian"));
        DataSupport.deleteAll(Bianma.class, "pan_id = " + getIntent().getStringExtra("pandian"));
        DataSupport.deleteAll(Tiaoma.class, "pid = " + getIntent().getStringExtra("pandian"));
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
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_pan_detail.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("是否继续退出?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                client.cancelRequests(Activity_pan_detail.this, true);
                MyClient.cancleClient(Activity_pan_detail.this);
                if (DataSupport.where("pid = " + getIntent().getStringExtra("pandian")).find(Tiaoma.class).size() > 0) {

                    Pan pan = DataSupport.where("pan_id = " + getIntent().getStringExtra("pandian")).find(Pan.class).get(0);
                    //盘点未上传
                    pan.setStatus("2");
                    pan.save();
                } else {
                    DataSupport.deleteAll(Pan.class, "pan_id = " + getIntent().getStringExtra("pandian"));
                    DataSupport.deleteAll(Bianma.class, "pan_id = " + getIntent().getStringExtra("pandian"));
                    DataSupport.deleteAll(Tiaoma.class, "pid = " + getIntent().getStringExtra("pandian"));
                }
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
            Intent intent = new Intent();
            intent.setClass(Activity_pan_detail.this, Ruku_Detail.class);
            intent.putExtra("ruku_list", (Serializable) lists);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

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
