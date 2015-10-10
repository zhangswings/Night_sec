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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class Tuiku_Detail extends AppCompatActivity {

    @InjectView(R.id.out_editText)
    ClearEditText outEditText;
    @InjectView(R.id.out_gongyingshang_list)
    ListView outGongyingshangList;
    @InjectView(R.id.out_btn_cancle)
    Button outBtnCancle;
    @InjectView(R.id.out_btn_ok)
    Button outBtnOk;
    private static AsyncHttpClient client;
    private SimpleAdapter adapter;
    private List<Map<String, String>> lists;
    String ghs = "";
    String ghsname = "";
    String ck = "";

    ProgressDialog progressDialog;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_out_second);
        ButterKnife.inject(this);
        outEditText.setHint("请扫描退库产品条码");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        client = new AsyncHttpClient();
        ghs = getIntent().getStringExtra("ghs");
        ghsname = getIntent().getStringExtra("ghsname");
        ck = getIntent().getStringExtra("ck");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("仓库" + ck + "&" + ghsname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progressDialog = new ProgressDialog(Tuiku_Detail.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                client.cancelRequests(Tuiku_Detail.this, true);
                showToast("已取消上传");
            }
        });
        // 初始化振动器
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        outEditText.setText(ghs + ck);
        outBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        outBtnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!TextUtils.isEmpty(outEditText.getText()) && !lists.isEmpty()) {
                                                RequestParams params = new RequestParams();
                                                //传递参数
                                                String content_str = "";
                                                String detail_str = "";
                                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                //操作人、客户编号、客户、订单号、备注
                                                content_str = preferences.getString("user", "admin") + "," + ghs+ "," +ghsname+","+",";
                                                StringBuilder detail_builder = new StringBuilder();
                                                if (lists.size() > 1) {
                                                    for (int i = 0; i < lists.size(); i++) {
                                                        String bianma_str = lists.get(i).get("bianma");
                                                        String tiaoma_str = lists.get(i).get("tiaoma");
                                                        String weight_str = lists.get(i).get("weight");
                                                        String length_str = lists.get(i).get("length");
                                                        detail_builder.append(bianma_str + "," + tiaoma_str + "," + weight_str + "," + length_str + "|");
                                                    }
                                                    detail_str = detail_builder.substring(0, detail_builder.length() - 1);
                                                } else {
                                                    String bianma_str = lists.get(0).get("bianma");
                                                    String tiaoma_str = lists.get(0).get("tiaoma");
                                                    String weight_str = lists.get(0).get("weight");
                                                    String length_str = lists.get(0).get("length");
                                                    detail_str = bianma_str + "," + tiaoma_str + "," + weight_str + "," + length_str;
                                                }
                                                params.put("content", content_str);
                                                params.put("detail", detail_str);
                                                //如果选择"04"仓库，即为散货，其他仓库为单货
                                                params.put("rtype", ck.equals("04") ? "3" : "2");
                                                Log.d("zhang", content_str);
                                                Log.d("zhang", detail_str);
                                                progressDialog.setTitle("上传中...");
                                                progressDialog.setMessage("正在上传数据，请稍后...");
                                                progressDialog.setCancelable(true);
                                                progressDialog.show();
                                                //设置重复请求次数，间隔
                                                client.setMaxRetriesAndTimeout(5, 3000);
                                                client.setTimeout(3000);
                                                //设置超时时间，默认10s
                                                client.post(Tuiku_Detail.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/AddReturnStore", params, new AsyncHttpResponseHandler() {
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
                                                            showToast(info);
                                                            barcodes = null;
                                                        }
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                        progressDialog.dismiss();
                                                        showToast(error.toString());
                                                    }
                                                });
                                            } else {
                                                outEditText.setShakeAnimation();
                                                showToast("配货单信息不能为空");
                                            }

                                        }
                                    }

        );
        lists = new ArrayList<Map<String, String>>();
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
        adapter = new SimpleAdapter(this, lists, R.layout.item_paper, new String[]{"name", "kezhong", "fukuan", "weight"}, new int[]{R.id.text_name, R.id.text_kezhong, R.id.text_fukuan, R.id.text_weight});
        outGongyingshangList.setAdapter(adapter);
        outGongyingshangList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Tuiku_Detail.this,ActivityOut_detail.class);
                intent.putExtra("bianma",lists.get(position).get("bianma"));
                intent.putExtra("tiaoma",lists.get(position).get("tiaoma"));
                intent.putExtra("name",lists.get(position).get("name"));
                intent.putExtra("kezhong",lists.get(position).get("kezhong"));
                intent.putExtra("fukuan",lists.get(position).get("fukuan"));
                intent.putExtra("weight",lists.get(position).get("weight"));
                intent.putExtra("length",lists.get(position).get("length"));
                startActivity(intent);
            }
        });
        outGongyingshangList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Tuiku_Detail.this);
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
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Tuiku_Detail.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("您确定返回吗?");
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
//                    for (String str : barcodes
//                            ) {
//                        Log.d("wan", str);
//                    }
                    if (ck.equals(barcodes[7])) {
                        if (!lists.toString().contains(barcodes[1])) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("name", barcodes[2]);
                            map.put("bianma", barcodes[0]);
                            map.put("tiaoma", barcodes[1]);
                            map.put("weight", barcodes[5]);
                            map.put("length", barcodes[6]);
                            map.put("kezhong", barcodes[4]);
                            map.put("fukuan", barcodes[3]);
                            lists.add(map);
                            adapter.notifyDataSetChanged();
                            outEditText.setText(barcodes[1]);
                        } else {
                            showToast("该条码已添加!");
                        }
                    } else {
                        showToast("该条码对应车间(仓库)有误!");
                    }

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


}
