package com.act.videochat;

import android.app.Application;

import java.util.ArrayList;
import java.util.Random;


public class MyApp extends Application {

    static int index;
    public static String[] userInfo;

    @Override
    public void onCreate() {
        super.onCreate();

        index = new Random().nextInt(10);
        ArrayList<String[]> userInfos = new ArrayList<>();


        String[] para1 = new String[]{"97728", "37cee58a4e2cff47233363ee437289ed"};
        String[] para2 = new String[]{"1533706", "cfe97056c4f31cc44f55890c5095e111"};
        String[] para3 = new String[]{"1533759", "0a23900d9221d61a537d103ff97b5eff"};
        String[] para4 = new String[]{"1533772", "e2156aa5a1f901582187f17d6365e4f3"};
        String[] para5 = new String[]{"1533783", "0aaede1380c38b79283fbb09f1fe0515"};
        String[] para6 = new String[]{"1533793", "a35624524952202c0cef004806c35bae"};
        String[] para7 = new String[]{"1533803", "8ca1a8aaa85a7a28861816a14da99310"};
        String[] para8 = new String[]{"1533813", "ec55bd55d1af933e9d1d9eed538729da"};
        String[] para9 = new String[]{"1533826", "edca5fef1ff1910f6731c9922209a392"};
        String[] para10 = new String[]{"1533838", "91e952d47cfaa5ad70303457ca585eb5"};
        String[] para11 = new String[]{"1533846", "145303adf93f4afc8da091535b287603"};


        userInfos.add(para1);
        userInfos.add(para2);
        userInfos.add(para3);
        userInfos.add(para4);
        userInfos.add(para5);
        userInfos.add(para6);
        userInfos.add(para7);
        userInfos.add(para8);
        userInfos.add(para9);
        userInfos.add(para10);
        userInfos.add(para11);

        userInfo = userInfos.get(index);

    }
}



