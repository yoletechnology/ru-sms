package com.game.YouleSdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetUtil{
    public String TAG = "YouleSdkMgr_NetUtil";
    private String appkey = "";
    private String model = "";
    public String userCode = "";
    public String paymentId = "";
    public String smsNumber = "";
    public String smsCode = "";

    NetUtil(String _appkey,String _model)
    {
        appkey = _appkey;
        model = _model;
        Log.d(TAG, "NetUtil init:appkey="+appkey+";model="+model);
    }
    public String get(String url1,String state) {
        Log.d(TAG, "NetUtil get:"+url1);
//        System.out.printf("NetUtil get:%s",url1);
        try {
            URL url = new URL("https://api.yolewallet.com/"+url1);
            HttpURLConnection Connection = (HttpURLConnection) url.openConnection();
            Connection.setRequestMethod("GET");
            Connection.setConnectTimeout(5000);
            Connection.setReadTimeout(5000);
            if(state.indexOf("getUserCode") != -1)
            {
                Connection.setRequestProperty("appkey", this.appkey);
                Connection.setRequestProperty("model", this.model);
            }
            else if(state.indexOf("getPaymentSms") != -1 || state.indexOf("saveSmsPayRecord") != -1)
            {
                Connection.setRequestProperty("appkey", this.appkey);
                Connection.setRequestProperty("userCode", this.userCode);
            }
            else if(state.indexOf("sms") != -1)
            {
            }
//            Log.d(TAG, "NetUtil get:"+url1);
            int responseCode = Connection.getResponseCode();
//            Log.d(TAG, "responseCode:"+responseCode);
            if (responseCode == Connection.HTTP_OK) {
                InputStream inputStream = Connection.getInputStream();
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(bytes)) != -1) {
                    arrayOutputStream.write(bytes, 0, length);
                    arrayOutputStream.flush();//强制释放缓冲区
                }
                String s = arrayOutputStream.toString();
                return s;
            } else {
                return "-1";
            }
        } catch (Exception e) {
            Log.d(TAG, "catch:"+e);
            return "-1";
        }
    }
    /**获取用户code*/
    /**
     *
     * @param gaid          googleADid        87f20949-4880-44ca-a902-ee3d3b1e7d72
     * @param imei          手机imei          358015970828188
     * @param mac           手机mac           02:00:00:00:00:00
     * @param countryCode   国家码             CH
     */
    public void getUserCode(String gaid,String imei,String mac,String countryCode)  {
        String data = "";
//        data += ("gaid="+gaid);
//        data += ("&imei="+imei);
//        data += ("&mac="+mac);
//        data += ("&countryCode="+countryCode);
//        Log.d(TAG, "getUserCode gaid:"+gaid);
//        Log.d(TAG, "getUserCode imei:"+imei);
//        Log.d(TAG, "getUserCode mac:"+mac);
//        Log.d(TAG, "getUserCode countryCode:"+countryCode);
//        Log.d(TAG, "getUserCode:"+data);
        String res = this.get("api/user/getUserCode?"+data,"getUserCode");
        Log.d(TAG, "getUserCode"+res);
        if(res.indexOf("-1") != -1)
        {
            return;
        }

        //响应结果:  {"status":"SUCCESS","errorCode":null,"message":null,"content":"a866a46a7ea24bc989b27d73092fc698"}  content 就是 userCode
        try {
            JSONObject jsonObject = new JSONObject(res);
            String status = jsonObject.getString("status");
            String content = jsonObject.getString("content");
            if(status.indexOf("SUCCESS") ==  -1)
            {
                Log.d(TAG, "getUserCode error:"+status);
            }
            else
            {
                userCode = content;
                Log.d(TAG, "userCode:"+userCode);
                getPaymentSms(countryCode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**获取支付策略*/
    /**
     *
     * @param countryCode   国家码         CH
     */
    public void getPaymentSms(String countryCode) {
        String data = "";
        data += ("paymentId=5");
        String res = this.get("api/payment/getPaymentSms?"+data,"getPaymentSms");
        Log.d(TAG, "getPaymentSms"+res);

        try {
            JSONObject jsonObject = new JSONObject(res);
            String status = jsonObject.getString("status");
            String content = jsonObject.getString("content");
            if(status.indexOf("SUCCESS") ==  -1)
            {
                Log.d(TAG, "getUserCode error:"+status);
            }
            else
            {
                Log.d(TAG, "content:"+content);
                JSONObject jsonObject1 = new JSONObject(content);

                String id = jsonObject1.getString("id");
                Log.d(TAG, "id:"+id);
                String paymentId = jsonObject1.getString("paymentId");
                Log.d(TAG, "paymentId:"+paymentId);
                String mnc = jsonObject1.getString("mnc");
                Log.d(TAG, "mnc:"+mnc);
                String smsNumber = jsonObject1.getString("smsNumber");
                Log.d(TAG, "smsNumber:"+smsNumber);
                String smsCode = jsonObject1.getString("smsCode");
                Log.d(TAG, "smsCode:"+smsCode);
                String smsPrice = jsonObject1.getString("smsPrice");
                Log.d(TAG, "smsCode:"+smsPrice);
                String status1 = jsonObject1.getString("status");
                Log.d(TAG, "status1:"+status1);
                this.smsNumber = smsNumber;
                this.smsCode = smsCode;
                Log.d(TAG, "smsNumber:"+smsNumber +";smsCode:"+smsCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String saveRUSmsPayRecord(String countryCode, String currency, String amount, String orderNumber ) {
        String data = "";
        data += ("countryCode="+countryCode);
        data += ("&currency="+currency);
        data += ("&amount="+amount);
        data += ("&paymentId="+paymentId);
        data += ("&orderNumber="+orderNumber);
        String res = this.get("api/payment/saveRUSmsPayRecord?"+data,"saveRUSmsPayRecord");
        Log.d(TAG, "saveSmsPayRecord"+res);

        try {
            JSONObject jsonObject = new JSONObject(res);
            String status = jsonObject.getString("status");
            String content = jsonObject.getString("content");
            if(status.indexOf("SUCCESS") ==  -1)
            {
                Log.d(TAG, "getUserCode error:"+status);
                return "";
            }
            else
            {
                Log.d(TAG, "content:"+content);
                JSONObject jsonObject1 = new JSONObject(content);
                String billingNumber = jsonObject1.getString("billingNumber");
                Log.d(TAG, "billingNumber:"+billingNumber);
                return billingNumber;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
