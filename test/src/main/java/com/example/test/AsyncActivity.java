package com.example.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class AsyncActivity extends AppCompatActivity {

    TextView infoTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        infoTv= (TextView) findViewById(R.id.info_tv);
        infobtn= (Button) findViewById(R.id.info_btn);
        infobtn2= (Button) findViewById(R.id.info_btn2);
        infobtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyClient.cancleClient(AsyncActivity.this);
            }
        });
        infobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("zhang", "after cancleClient ");
//                Toast.makeText(getApplicationContext(),"Client",Toast.LENGTH_SHORT).show();
                infoTv.setText("");
                MyClient.get(AsyncActivity.this, "http://www.tuling123.com/openapi/api?key=1fc4fdc09214657484298362f05e3700&info=你好", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            infoTv.setText(new String(responseBody));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
                Log.d("zhang", "after cancleClient ");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MyClient.cancleClient(AsyncActivity.this);
            }
        });
    }
    Button infobtn;
    Button infobtn2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_async, menu);
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
}
