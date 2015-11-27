package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.device.DeviceManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.litepal.tablemanager.Connector;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;
//添加绑定序列号功能

/**
 * 登录操作
 * 1.帐号
 * 2.密码
 * 3.登陆系统，获取用户名（返回信息不为空，校验成功）
 */
public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.edit_text_name)
    EditText editTextName;
    @InjectView(R.id.edit_text_password)
    EditText editTextPassword;
    @InjectView(R.id.btnLogin)
    Button btnLogin;
    @InjectView(R.id.btnCancle)
    Button btnCancle;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    NetState receiver;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    String username = "";
    String password = "";
    //设备管理器
    DeviceManager deviceManager;
    //在这里设置绑定的设备序列号数组,添加需要绑定的设备编号
    final static private String[] deviceIDs = {"67221511246764",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化设备管理器
        deviceManager = new DeviceManager();
        //获取设备序列号
        final String deviceID = deviceManager.getDeviceId();
        Log.d("deviceID", deviceID);
        //id﹕ 67221511246764
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = preferences.edit();
        username = preferences.getString("username", "");
        password = preferences.getString("password", "");
        ButterKnife.inject(this);
        if (preferences.getBoolean("isSave", false)) {
            editTextName.setText(username);
            editTextPassword.setText(password);
        }
        //检查网络
        receiver = new NetState();
        IntentFilter filter = new IntentFilter();

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver, filter);
        receiver.onReceive(this, null);
        //创建数据库
        Connector.getDatabase();
//        List<Papers> papers = DataSupport.where("paper_status='0' and paper_chejian=" + "'02'").find(Papers.class);
//        Log.d("zhang", DataSupport.findAll(Pan.class).toString());
//        Log.d("zhang", DataSupport.findAll(Bianma.class).toString());
//        Log.d("zhang", DataSupport.findAll(Tiaoma.class).toString());
//        DataSupport.deleteAll(Pan.class);
//        DataSupport.deleteAll(Tiaoma.class);
//        DataSupport.deleteAll(Bianma.class);
//        Log.d("zhang", DataSupport.findAll(Pan.class).toString());
//        Log.d("zhang", DataSupport.findAll(Bianma.class).toString());
//        Log.d("zhang", DataSupport.findAll(Tiaoma.class).toString());
//        showToast(papers.toString());
//        Reader reader=new Reader();
//       reader.setName("zhangq");
//        reader.setPrice(12);
//        if (reader.save()){
//            Toast.makeText(this,reader.save()+"保存成功！",Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(this,reader.save()+"保存失败！",Toast.LENGTH_LONG).show();
//        }
//        Toast.makeText(this, DataSupport.findAll(Papers.class).toString() + "\n查询成功！！", Toast.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_login);
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
//        client.setMaxRetriesAndTimeout(3, 2000);
//        client.setTimeout(5 * 1000);
//        client.setResponseTimeout(2*1000);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                client.cancelRequests(LoginActivity.this, true);
                showToast("登录已取消");
            }
        });

//        TextInputLayout textInputEmail =(TextInputLayout)findViewById(R.id.textInputEmail);
//        textInputEmail.setErrorEnabled(true);
//        textInputEmail.setError("Error Message");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取设备序列号
                DeviceManager deviceManager = new DeviceManager();
                String id = deviceManager.getDeviceId();
//                Log.d("id", id+"\n"+deviceIDs.toString().contains(id));
//                Log.d("ids",deviceIDs.toString());
                boolean isDevice = false;
                for (String ids : deviceIDs
                        ) {
//                    Log.d("ids",ids);
                    if (ids.equals(id)) {
                        isDevice = true;
                    }
                }
                if (isDevice){
                    if (TextUtils.isEmpty(editTextName.getText())) {
                        showToast("用户名不能为空");
                    } else if (TextUtils.isEmpty(editTextPassword.getText())) {
                        showToast("密码不能为空");
                    } else if (!TextUtils.isEmpty(editTextName.getText()) && !TextUtils.isEmpty(editTextPassword.getText())) {
                        progressDialog.setTitle("登录中...");
                        progressDialog.setMessage("正在登录，请稍后...");
                        progressDialog.setCancelable(true);
                        progressDialog.show();

//                    client.setTimeout(2000);
                        RequestParams params = new RequestParams();
                        params.put("ID", editTextName.getText().toString());
                        params.put("pwd", editTextPassword.getText().toString());
                        client.post(LoginActivity.this, "http://" + preferences.getString("ip", "192.168.0.187") + ":8092/Service1.asmx/GetUserInfo", params, new AsyncHttpResponseHandler() {

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
                                    if (info.length() > 2) {
                                        Log.d("zhang", info + ">>>>>zhang");
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("user", info.substring(1, info.length() - 1));
                                        edit.putString("user", info.substring(1, info.length() - 1));
                                        edit.putString("username", editTextName.getText().toString());
                                        edit.putString("password", editTextPassword.getText().toString());
                                        edit.commit();
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        showToast("用户名或密码有误");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                progressDialog.dismiss();
                                Log.d("zhang", error.toString());
                                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setIcon(R.mipmap.circle);
                    builder.setTitle("系统提示");
                    builder.setMessage("设备序列号不正确，请联系万和科技！");
                    builder.setCancelable(false);
                    builder.setNeutralButton("确定", null);
                    builder.create().show();
                    showToast("设备序列号不正确，请联系万和科技！");
                }
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setText("");
                editTextPassword.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                intent = new Intent(LoginActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
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
