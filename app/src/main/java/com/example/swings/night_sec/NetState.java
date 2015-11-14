package com.example.swings.night_sec;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by swings on 2015-09-25.
 * 检测网络状态工具类
 * wifi网络为打开时，提示设置网络
 */
class NetState extends BroadcastReceiver {
    private int tag = 0;

    @Override
    public void onReceive(final Context context, Intent arg1) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!gprs.isConnected() && !wifi.isConnected() && tag == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.mipmap.circle).setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
                            if (android.os.Build.VERSION.SDK_INT > 10) {
                                // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                                context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                dialog.dismiss();
                            } else {
                                context.startActivity(
                                        new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                dialog.dismiss();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            tag++;
        } else if (tag == 0) {
            tag++;
            Toast.makeText(context,"网络连接成功",Toast.LENGTH_SHORT).show();
        }
    }
}