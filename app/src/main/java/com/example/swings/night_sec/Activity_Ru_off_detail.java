package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.swings.night_sec.module.Papers;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * 入库离线保存（2）详情页
 */
public class Activity_Ru_off_detail extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
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
    @InjectView(R.id.edit_text_ru_shijian)
    EditText editTextRuShijian;
    @InjectView(R.id.edit_text_ru_yonghu)
    EditText editTextRuYonghu;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;
    @InjectView(R.id.ru_btn_cancle)
    Button ruBtnCancle;
    @InjectView(R.id.ru_btn_ok)
    Button ruBtnOk;
    @InjectView(R.id.ru_ll_bottom)
    LinearLayout ruLlBottom;
//    private static AsyncHttpClient client;
    ProgressDialog progressDialog;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_activity_ru);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("快速入库");
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
        progressDialog = new ProgressDialog(Activity_Ru_off_detail.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                client.cancelRequests(Activity_Ru_off_detail.this, true);
                MyClient.cancleClient(Activity_Ru_off_detail.this);
                showToast("已取消上传");
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.inject(this);
//        client = new AsyncHttpClient();
        //设置重复请求次数，间隔
//        client.setMaxRetriesAndTimeout(3, 2000);
        //设置超时时间，默认10s
//        client.setTimeout(2 * 1000);
        //设置连接超时时间为2秒（连接初始化时间）
        ruBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                    MyClient.post(Activity_Ru_off_detail.this,"http://" + preferences.getString("ip", "192.168.0.187") + ":8092/JsonHandler.ashx?doc=PDA_InStore", params, new AsyncHttpResponseHandler() {


                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            progressDialog.dismiss();
                            if (statusCode == 200) {
                                String info = "";
                                String text = new String(responseBody);
                                Log.d("zhang", text + ">>>>>zhang");
//                                try {
//                                    Document document = DocumentHelper.parseText(text);
//                                    Element element = document.getRootElement();
//                                    info = element.getText();
//
//                                } catch (DocumentException de) {
//                                    Log.e("de", de.toString());
//                                }
                                Papers paper = new Papers();
                                paper.setPaper_status("1");
                                paper.updateAll("paper_code=" + code_str);
                                showToast(text);
                                clearEditText();
                                clearString();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            progressDialog.dismiss();

                            AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_Ru_off_detail.this);
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


                } else {
                    showToast("数据项不能有空值");
                }
            }
        });
        setEditText();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(Activity_Ru_off_detail.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("是否继续退出?");
        exitbuilder.setIcon(R.mipmap.circle);
        exitbuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                client.cancelRequests(Activity_Ru_off_detail.this, true);
                MyClient.cancleClient(Activity_Ru_off_detail.this);
                finish();
            }
        });
        exitbuilder.setNegativeButton("否", null);
        // exitbuilder.create();
        exitbuilder.show();
    }

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

    private void setEditText() {
        List<Papers> papers = DataSupport.where("paper_status='0' and paper_code=" + getIntent().getStringExtra("tiaoma")).find(Papers.class);
        if (papers.size() > 0) {
            editTextRuBianma.setText(papers.get(0).getPaper_tiaoma());
            editTextRuCode.setText(papers.get(0).getPaper_code());
            editTextRuName.setText(papers.get(0).getPaper_name());
            editTextRuFukuan.setText(papers.get(0).getPaper_fukuan());
            editTextRuKezhong.setText(papers.get(0).getPaper_kezhong());
            editTextRuWeight.setText(papers.get(0).getPaper_weight());
            editTextRuLength.setText(papers.get(0).getPaper_length());
            editTextRuCjbz.setText(papers.get(0).getPaper_chejian() + "/" + papers.get(0).getPaper_banzu());
            editTextRuShijian.setText(papers.get(0).getPaper_code_time());
            editTextRuYonghu.setText(papers.get(0).getPaper_user());
        } else {
            showToast("该条码不存在!");
        }
    }

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
    protected void onDestroy() {
        super.onDestroy();
        MyClient.cancleClient(Activity_Ru_off_detail.this);
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
