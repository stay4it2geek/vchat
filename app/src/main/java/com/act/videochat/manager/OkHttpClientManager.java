package com.act.videochat.manager;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.act.videochat.Constants;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpClientManager {

    static OkHttpClient singleton;

    private OkHttpClientManager() {

    }

    public static OkHttpClient newInstance(Context context) {
        if (singleton == null) {
            synchronized (OkHttpClientManager.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                }
            }
        }
        return singleton;
    }

    public static void parseRequest(Context context, String url, Handler handler, int what) {
        Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).build());
        call.enqueue(new MyCallBack(handler, what));
    }

    public static void parseRequestGirlHomePage(Context context, String url, Handler handler, int what, String categoryId , String startPage) {
//      for(int userId=476869 ;userId<477469;userId++){
//          RequestBody formBody = new FormBody.Builder()
//                  .add("tagId", categoryId)
//                  .add("page","1")
//                  .build();
//          Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).post(formBody).build());
//          call.enqueue(new MyCallBack2(handler, userId,what));
//      }
        RequestBody formBody = new FormBody.Builder()
                .add("tagId", categoryId)
                .add("page",startPage+"")
                .build();
        Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).post(formBody).build());
        call.enqueue(new MyCallBack(handler, what));
    }
    //生成随机数字和字母,
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    //根据指定长度生成纯数字的随机数
    public static String createNumData(int length) {
        StringBuilder sb=new StringBuilder();
        Random rand=new Random();
        for(int i=0;i<length;i++)
        {
            sb.append(rand.nextInt(10));
        }
       return sb.toString();
    }
    //52个字母与6个大小写字母间的符号；范围为91~96
    public static String createChart(int length){
        String randomcode = "";
        for(int i=0;i<length;i++)
        {
            int value = (int)(Math.random()*58+65);
            while(value>=91 && value<=96)
                value = (int)(Math.random()*58+65);
            randomcode = randomcode + (char)value;
        }
        return randomcode;
    }
    public static void parseRequestGirlSmallVideoOne(Context context, String url, Handler handler, String videoId) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId","0")
                .add("userKey", "")
                .add("macid",createChart(6)+"-"+getStringRandom(4)+"-"+getStringRandom(4)+"-"+createNumData(4)+"-"+createNumData(6)+getStringRandom(6))
                .add("videoId",videoId)
                .build();
        Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).post(formBody).build());
        call.enqueue(new MyCallBack(handler, Constants.REFRESH));
    }
    public static void parseRequestGirlBigVideoOne(Context context, String url, Handler handler, String videoId) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId","0")
                .add("userKey", "")
                .add("vid",videoId)
                .build();
        Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).post(formBody).build());
        call.enqueue(new MyCallBack(handler, Constants.INFO));
    }
    public static void parseRequestGirlSmallVideoList(Context context, String url, Handler handler, int what, String vid , String startPage) {
        RequestBody formBody = new FormBody.Builder()
                .add("vid",vid)
                .add("page",startPage+"")
                .build();
        Call call = OkHttpClientManager.newInstance(context).newCall(new Request.Builder().url(url).post(formBody).build());
        call.enqueue(new MyCallBack(handler, what));
    }
    static class MyCallBack implements Callback {
        Handler handler;
        int what;

        public MyCallBack(Handler handler, int what) {
            this.handler = handler;
            this.what = what;
        }


        @Override
        public void onFailure(Call call, IOException e) {
            handler.sendEmptyMessage(Constants.NetWorkError);

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Message message = handler.obtainMessage();
            message.obj = response.body().string();
            message.what = what;
            handler.sendMessage(message);
        }
    }


    static class MyCallBack2 implements Callback {
        Handler handler;
        int what;
        int userId;
        public MyCallBack2(Handler handler, int userId, int what) {
            this.handler = handler;
            this.what = what;
            this.userId = userId;

        }


        @Override
        public void onFailure(Call call, IOException e) {
            handler.sendEmptyMessage(Constants.NetWorkError);

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Message message = handler.obtainMessage();
            message.obj = response.body().string();
            message.arg1 = userId;

            message.what = what;
            handler.sendMessage(message);
        }
    }
}
