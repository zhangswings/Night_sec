package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swings.night_sec.module.ChukuInfo;
import com.example.swings.night_sec.module.KeHu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class ActivityOut extends AppCompatActivity {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.out_editText)
    AutoEditText outEditText;

    @InjectView(R.id.out_ll_content)
    LinearLayout outLlContent;
    @InjectView(R.id.out_btn_guaqi)
    Button outBtnGuaqi;
    @InjectView(R.id.out_btn_cancle)
    Button outBtnCancle;
    @InjectView(R.id.out_btn_ok)
    Button outBtnOk;
    @InjectView(R.id.out_ll_bottom)
    LinearLayout outLlBottom;
    @InjectView(R.id.out_gongyingshang_list)
    ListView outGongyingshangList;
    @InjectView(R.id.outChepaihao)
    ClearEditText outChepaihao;
    @InjectView(R.id.textView)
    TextView textView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> lists;
    private AsyncHttpClient client;
    SharedPreferences preferences;
    private String ghs = "";
    private String ghsname = "";
    List<ChukuInfo> chukuInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_out);
        ButterKnife.inject(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_ghs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        client = new AsyncHttpClient();
        //设置超时
        client.setMaxRetriesAndTimeout(5, 2000);
        client.setTimeout(5 * 1000);
        lists = new ArrayList<Map<String, String>>();
        chukuInfos = DataSupport.where("status = 2").find(ChukuInfo.class);
        if (chukuInfos.size() > 0) {
            StringBuilder strbuilder = new StringBuilder();
            strbuilder.append("挂起信息\n");
            strbuilder.append("客户：" + chukuInfos.get(0).getKehu() + "\n");
            strbuilder.append("车牌号：" + chukuInfos.get(0).getChepai() + "\n");
            float weight = 0;
            for (int i = 0; i < chukuInfos.size(); i++) {
                strbuilder.append("条码：" + chukuInfos.get(i).getTiaoma());
                strbuilder.append("车间：" + chukuInfos.get(i).getChejian() );
                strbuilder.append("重量：" + chukuInfos.get(i).getWeight() + "\n");
                weight += Float.parseFloat(chukuInfos.get(i).getWeight());
            }

            strbuilder.append("总重量：" + weight + "\n");
            textView.setText(strbuilder);
        }
        adapter = new SimpleAdapter(this, lists, R.layout.item_ghs, new String[]{"name", "id"
        }, new int[]

                {
                        R.id.ghs_name, R.id.ghs_id
                }

        );
        outGongyingshangList.setAdapter(adapter);
        outGongyingshangList.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                                    {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            showToast(lists.get(position).get("name"));
//                outEditText.setText(lists.get(position).get("name"));
                                                            ghs = lists.get(position).get("id");
                                                            ghsname = lists.get(position).get("name");
                                                            outEditText.setText(ghsname);
                                                            outGongyingshangList.setVisibility(View.GONE);
                                                            client.cancelRequests(ActivityOut.this, true);
                                                        }
                                                    }

        );
        outBtnCancle.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View v) {
                                                onBackPressed();
                                            }
                                        }

        );
        outBtnOk.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            if (TextUtils.isEmpty(outEditText.getText()) || ghs.length() < 1 || ghsname.length() < 1) {
                                                outEditText.setShakeAnimation();
                                                //设置提示
                                                showToast("客户信息不能为空!");
                                                return;
                                            } else {
                                                if (outEditText.getText().toString().contains(",")) {
                                                    showToast("输入信息有误，请不要输入,(逗号)");
                                                } else if (!ghsname.equals(outEditText.getText().toString().trim())) {
                                                    showToast("客户信息有误，请在列表中点击选择");
                                                } else if (!TextUtils.isEmpty(outChepaihao.getText())) {

                                                    if (DataSupport.where("chepai=" + outChepaihao.getText().toString()).find(ChukuInfo.class).isEmpty()) {  //跳转下个页面出库
                                                        Intent intent = new Intent(ActivityOut.this, ActivityOut_Second.class);
                                                        //传入客户信息&&仓库信息
                                                        intent.putExtra("ghs", ghs);
                                                        intent.putExtra("ghsname", ghsname);
                                                        intent.putExtra("chepaihao", outChepaihao.getText().toString());
                                                        startActivity(intent);
                                                        //清空
                                                        outEditText.setText("");
                                                    } else {
                                                        showToast("该车辆已经挂起，请在点击查看挂起信息继续操作！");
                                                    }
                                                } else {
                                                    outChepaihao.setShakeAnimation();
                                                    showToast("车牌号信息不能为空!");
                                                }
                                            }

                                        }
                                    }

        );
        outBtnGuaqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chukuInfos.size() > 0) {
                    Intent intent = new Intent(ActivityOut.this, ActivityOut_Second.class);
                    //传入客户信息&&仓库信息
                    intent.putExtra("ghs", chukuInfos.get(0).getGhs());
                    intent.putExtra("ghsname", chukuInfos.get(0).getKehu());
                    intent.putExtra("chepaihao", chukuInfos.get(0).getChepai());
                    intent.putExtra("guaqi", "guaqi");
                    startActivity(intent);
                } else {
                    showToast("挂起信息不存在！");
                }
            }
        });
        //输入两个字符开始提示
        outEditText.setThreshold(2);

        outEditText.addTextChangedListener(new TextWatcher() {
                                               @Override
                                               public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                               }

                                               @Override
                                               public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                   String str = s.toString();
                                                   if (str.length() > 0) {
                                                       RequestParams params = new RequestParams();
                                                       params.put("Id", str);
                                                       params.put("top", "10");
                                                       client.cancelRequests(ActivityOut.this, true);
                                                       client.post(ActivityOut.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/GetSupplier", params, new AsyncHttpResponseHandler() {
                                                                   @Override
                                                                   public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                       if (statusCode == 200) {
                                                                           String info = "";
                                                                           String text = new String(responseBody);
                                                                           Log.d("zhang", text + ">>>>>zhang");
                                                                           try {
                                                                               Document document = DocumentHelper.parseText(text);
                                                                               Element element = document.getRootElement();
                                                                               info = element.getText();
                                                                               Gson gson = new Gson();
                                                                               Type type = new TypeToken<List<KeHu>>() {
                                                                               }.getType();
                                                                               List<KeHu> keHus = gson.fromJson(info, type);
                                                                               String[] temp = new String[keHus.size()];
                                                                               if (keHus.size() > 0) {
                                                                                   outGongyingshangList.setVisibility(View.VISIBLE);
                                                                                   lists.clear();
                                                                                   for (int i = 0; i < keHus.size(); i++) {
                                                                                       temp[i] = keHus.get(i).getSupplierName();
//                                                                                       Log.d("coun", temp[i]);
                                                                                       Map<String, String> map = new HashMap<String, String>();
                                                                                       map.put("name", keHus.get(i).getSupplierName());
                                                                                       map.put("id", keHus.get(i).getSupplierId());
                                                                                       lists.add(map);
                                                                                   }
                                                                                   adapter.notifyDataSetChanged();
                                                                               } else {
                                                                                   showToast("所查询客户信息不存在");
                                                                                   outGongyingshangList.setVisibility(View.GONE);
                                                                               }

                                                                           } catch (DocumentException de) {
                                                                               Log.e("de", de.toString());
                                                                           }
                                                                       }

                                                                   }

                                                                   @Override
                                                                   public void onFailure(
                                                                           int statusCode, Header[] headers,
                                                                           byte[] responseBody, Throwable error) {
                                                                       showToast(error.toString());
                                                                   }
                                                               }

                                                       );
                                                   } else {
                                                       outGongyingshangList.setVisibility(View.GONE);
                                                   }
                                               }

                                               @Override
                                               public void afterTextChanged(Editable s) {

                                               }
                                           }

        );


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitbuilder = new AlertDialog.Builder(ActivityOut.this);
        exitbuilder.setTitle("系统提示");
        exitbuilder.setMessage("是否继续退出?");
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
