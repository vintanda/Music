package com.example.lzd.music;

/**
 * Created by LZD on 2016/10/6.
 */
public class Constant {
    //广播注册状态
    public static String MUSIC_SERVICE = "com.lzd.action.MUSIC_SERVICE";
    public static String MUSIC_MAIN = "com.lzd.action.MUSIC_MAIN";
    public static String MUSIC_PLAY = "com.lzd.action.MUSIC_PLAY";

    //动作状态
    public static int ALLREPEAT_ACTION = 0;  //全部循环
    public static int ALLPLAY_ACTION = 1;  //顺序播放
    public static int CURRENTREPEAT_ACTION = 2;  //单曲循环
    public static int SHUFFLE_ACTION = 3;  //随机播放

    public static int PAUSE_ACTION = 4;  //暂停
    public static int PLAY_ACTION = 5;  //播放
    public static int PROIR_ACTION = 6;  //上一首
    public static int NEXT_ACTION = 7;  //下一首

    public static int AUTO_NEXT = 8;  //播放完毕播放下一首
}
