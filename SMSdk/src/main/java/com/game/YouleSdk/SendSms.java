package com.game.YouleSdk;

import static android.text.TextUtils.isEmpty;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class SendSms {
    private String TAG = "YouleSdkMgr_SendSms";
    private Activity var =  null;
    private static final int SEND_SMS = 100;
    private static final String SMS_SENT_ACTION = "SMS_SENT";
    public SendSms (Activity var1)
    {
        var = var1;
        var.registerReceiver(smsSentReceiver, new IntentFilter(SMS_SENT_ACTION));
        this.requestPermission();
    }
    private BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"MyBroadcastReceiver"+ "Received broadcast: " + intent.getAction());
            Log.d(TAG,"code"+ String.valueOf(getResultCode()));
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    // 短信发送成功
                    Log.e(TAG,"signal"+"成功");
                    Toast.makeText(context, "短信发送成功yyyyy", Toast.LENGTH_SHORT).show();
                    YouleSdkMgr.smeResult.onResult(true);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    // 短信发送失败
                    Log.e(TAG,"signal"+"失败");
                    Toast.makeText(context, "短信发送失败", Toast.LENGTH_SHORT).show();
                    YouleSdkMgr.smeResult.onResult(false);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    // 手机没有信号，无法发送短信
                    Log.e(TAG,"signal"+"失败");
                    Toast.makeText(context, "手机无信号，无法发送短信", Toast.LENGTH_SHORT).show();
                    YouleSdkMgr.smeResult.onResult(false);
                    break;
                default:
                    // 更多其他错误码可根据需要进行处理
                    Log.e(TAG,"signal"+"更多其他错误码可根据需要进行处理");
                    Toast.makeText(context, "更多其他错误码可根据需要进行处理", Toast.LENGTH_SHORT).show();
                    YouleSdkMgr.smeResult.onResult(false);
                    break;
            }
        }
    };

    private void requestPermission() {
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission( this.var, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this.var, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS);
                return;
            } else {
                this.var.registerReceiver(smsSentReceiver, new IntentFilter(SMS_SENT_ACTION));
                //已有权限
                Log.e(TAG,"已有权限");
            }
        } else {
            //API 版本在23以下
            Log.e(TAG,"API 版本在23以下");
        }
    }

    //发送短信
    public void sendSMSS(String content,String phone) {

        Log.e(TAG,"手机"+phone);

        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission( this.var, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this.var, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS);
                return;
            } else {
                this.var.registerReceiver(smsSentReceiver, new IntentFilter(SMS_SENT_ACTION));
            }
        }

        if (!isEmpty(content) && !isEmpty(phone)) {
            SmsManager manager = SmsManager.getDefault();

            Intent sentIntent = new Intent(SMS_SENT_ACTION);
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this.var, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE);


            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                boolean isMessageSent = false;
                try {
                    manager.sendTextMessage(phone, null, content, sentPendingIntent, null);
                    isMessageSent = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isMessageSent) {
                    Log.d(TAG,"发送状态"+ "成功");
                    // 短信发送成功
                } else {
                    Log.d(TAG,"发送状态"+ "失败");
                    // 短信发送失败
                }
            }


        } else {
            Toast.makeText(this.var, "手机号或内容不能为空", Toast.LENGTH_SHORT).show();
            YouleSdkMgr.smeResult.onResult(false);
            return;
        }

    }

}
