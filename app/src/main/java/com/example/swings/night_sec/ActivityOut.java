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
    List<ChukuInfo> Chukus;

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

                                                    if (DataSupport.where("chepai=" + outChepaihao.getText().toString()+" and status = '2'").find(ChukuInfo.class).isEmpty()) {  //跳转下个页面出库
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

                if (Chukus.size() > 0) {
                    Intent intent = new Intent(ActivityOut.this, ActivityOut_Second.class);
                    //传入客户信息&&仓库信息
                    intent.putExtra("ghs", Chukus.get(0).getGhs());
                    intent.putExtra("ghsname", Chukus.get(0).getKehu());
                    intent.putExtra("chepaihao", Chukus.get(0).getChepai());
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

    @Override
    protected void onResume() {
        super.onResume();
        Chukus = DataSupport.where("status = 2").find(ChukuInfo.class);

        if (Chukus.size() > 0) {
            int strck_01 = 0, strck_02 = 0, strck_03 = 0, strck_04 = 0, strck_05 = 0;
            float numck_01 = 0, numck_02 = 0, numck_03 = 0, numck_04 = 0, numck_05 = 0;
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
            sb.append("挂起信息\n");
            sb.append("客户：" + Chukus.get(0).getKehu() + "\n");
            sb.append("车牌号：" + Chukus.get(0).getChepai() + "\n");
            sb.append((numck_01 > 0 ? "车间:01"   + " 总件数:"+strck_01+ " 总重量:" + numck_01+"\n" : "") + (numck_02 > 0 ? "车间:02"   + " 总件数:"+strck_02+ " 总重量:" + numck_02+ "\n" : "") + (numck_03 > 0 ? "车间:03"  + " 总件数:"+strck_03 + " 总重量:" + numck_03 + "\n" : "") + (numck_04 > 0 ? "车间:04" +  " 总件数:"+strck_04+" 总重量:" + numck_04  + "\n" : "") + (numck_05 > 0 ? "车间:05" +  " 总件数:"+strck_05 +" 总重量:" + numck_05 + "\n" : ""));
            sb.append("总重量：" + (numck_01+numck_02+numck_03+numck_04+numck_05) + "\n");
            textView.setText(sb);
        }else{
            textView.setText("挂起信息为空!");
        }
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
