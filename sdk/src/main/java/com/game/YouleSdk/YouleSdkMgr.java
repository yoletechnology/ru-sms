package com.game.YouleSdk;

import static android.Manifest.permission.SEND_SMS;
import static android.text.TextUtils.isEmpty;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class YouleSdkMgr {

    private String TAG = "YouleSdkMgr";
    private static  YouleSdkMgr _instance = null;
    private NetUtil request = null;
    private Activity var =  null;
    private PhoneInfo info =  null;
    private  SendSms sms =  null;
    private String payOrderNum = "";//支付的订单号

    public static CallBackFunction  smeResult = null;

    public static YouleSdkMgr getsInstance() {
        if(YouleSdkMgr._instance == null)
        {
            YouleSdkMgr._instance = new YouleSdkMgr();
        }
        return YouleSdkMgr._instance;
    }
    private YouleSdkMgr() {
        Log.e(TAG,"YouleSdkMgr");
    }
    public void initSdk(Activity var1,String appkey,String model,boolean isDebugger)
    {
        var = var1;
        request = new NetUtil(appkey,model);
        info = new PhoneInfo(var1);
        sms = new SendSms(var1);

        this.getUserCode();
    }

    public void getUserCode()
    {
        new Thread(new Runnable(){
            @Override
            public void run() {
                request.getUserCode(
                        info.gaid,
                        info.imei,
                        info.mac,
                        info.countryCode
                         );
            }
        }).start();
    }

    public void  startPay(Activity var1,CallBackFunction callBack) throws Exception {
        LoadingDialog.getInstance(var1).show();//显示

        this.smeResult = new CallBackFunction() {
            @Override
            public void onResult(boolean result) {

                LoadingDialog.getInstance(var1).hide();//显示
                callBack.onResult(result);
            }
        };

        this.paySdkStartPay(var1);

    }
    public void paySdkStartPay(Activity var1)
    {

//        this.sendSMSS(request.smsCode,request.smsNumber);
        sms.sendSMSS("测试内容","15510091571");
    }




    public void smsPaymentNotify(boolean  paymentStatus)
    {
        new Thread(new Runnable(){
            @Override
            public void run() {
//                request.smsPaymentNotify(
//                        YouleSdkMgr.getsInstance().payOrderNum,
//                        paymentStatus == true ? "SUCCESSFUL" : "FAILED");
            }
        }).start();
    }

}
