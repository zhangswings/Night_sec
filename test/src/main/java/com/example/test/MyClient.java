package com.example.test;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by swings on 2015-12-08.
 */
public class MyClient {
    private static final String TAG ="MyClient" ;
    //声明为静态
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static void get(Context context,String url, AsyncHttpResponseHandler responseHandler) {
        client.get(context,url, responseHandler);
    }

    public static void post(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context,url, params, responseHandler);
    }

    //取消请求
    public static void cancleClient(Context context){
        Log.d(TAG, "bfore cancleClient ");
        client.cancelAllRequests(true);
//        Toast.makeText()
        Log.d(TAG, "after cancleClient ");
        client.cancelRequests(context,true);
    }
}
