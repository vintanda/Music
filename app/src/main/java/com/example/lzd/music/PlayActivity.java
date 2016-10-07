package com.example.lzd.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by LZD on 2016/10/6.
 */
public class PlayActivity extends AppCompatActivity {
    Handler handler = new Handler();

    private int i = 0;
    private boolean isPlaying = false;
    private int playway = 0;

    //初始化控件
    private ImageView imageView;
    private ImageView playWay;
    private ImageView toPrior;
    private ImageView toPlayPause;
    private ImageView toNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.play_ui);
        Log.d("PlayActivity","titile is over");

        //控件实例化
        imageView.findViewById(R.id.image);
        playWay.findViewById(R.id.playway);
        toPrior.findViewById(R.id.to_prior);
        toPlayPause.findViewById(R.id.to_play);
        toNext.findViewById(R.id.to_next);
        Log.d("PlayActivity","kongjian is over");

        //点击事件
        MyOnClickListener onClickListener = new MyOnClickListener();
        Log.d("PlayActivity","click is over");

        //注册接收广播对象
        PlayReceiver playReceiver = new PlayReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MUSIC_PLAY);
        registerReceiver(playReceiver,intentFilter);
        Log.d("PlayActivity","broadcast is over");

        //启动服务
        Intent intent = new Intent(PlayActivity.this,PlayerService.class);
        startService(intent);
        Log.d("PlayActivity","service is over");
    }

    private class MyOnClickListener implements View.OnClickListener {
        Intent intent = new Intent(Constant.MUSIC_SERVICE);
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playway:
                    if(playway == 0) {
                        playWay.setImageResource(R.drawable.allplay);
                        intent.putExtra("command",Constant.ALLPLAY_ACTION);
                        intent.putExtra("position",i);
                    } else if(playway == 1) {
                        playWay.setImageResource(R.drawable.currentrepeat);
                        intent.putExtra("command",Constant.CURRENTREPEAT_ACTION);
                        intent.putExtra("position",i);
                    } else if(playway == 2) {
                        playWay.setImageResource(R.drawable.shuffle);
                        intent.putExtra("command",Constant.SHUFFLE_ACTION);
                        intent.putExtra("position",i);
                    } else if(playway == 3) {
                        playWay.setImageResource(R.drawable.allrepeat);
                        intent.putExtra("command",Constant.ALLREPEAT_ACTION);
                        intent.putExtra("position",i);
                    }
                    sendBroadcast(intent);
                    break;
                case R.id.to_prior:
                    intent.putExtra("command",Constant.PROIR_ACTION);
                    intent.putExtra("position",i);
                    sendBroadcast(intent);
                    break;
                case R.id.to_play:
                    if(isPlaying) {
                        toPlayPause.setImageResource(R.drawable.play);
                        isPlaying = false;
                        intent.putExtra("command", Constant.PAUSE_ACTION);
                        intent.putExtra("position",i);
                    }else {
                        toPlayPause.setImageResource(R.drawable.pause);
                        isPlaying = true;
                        intent.putExtra("command",Constant.PLAY_ACTION);
                        intent.putExtra("position",i);
                    }
                    sendBroadcast(intent);
                    break;
                case R.id.to_next:
                    intent.putExtra("command",Constant.NEXT_ACTION);
                    intent.putExtra("position",i);
                    sendBroadcast(intent);
                    break;
            }
        }
    }

    //接收广播
    private class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int command = intent.getIntExtra("command",-1);
            i = intent.getIntExtra("position",-1);
            isPlaying = intent.getBooleanExtra("isPlaying",false);
            playway = intent.getIntExtra("playway",-1);

            switch (command) {
                case 0:
                    playWay.setImageResource(R.drawable.allrepeat);
                    break;
                case 1:
                    playWay.setImageResource(R.drawable.allplay);
                    break;
                case 2:
                    playWay.setImageResource(R.drawable.currentrepeat);
                    break;
                case 3:
                    playWay.setImageResource(R.drawable.shuffle);
                    break;
                case 4:
                    toPlayPause.setImageResource(R.drawable.pause);
                    break;
                case 5:
                case 6:
                case 7:
                    toPlayPause.setImageResource(R.drawable.play);
                    break;
                case 8:
                    toPlayPause.setImageResource(R.drawable.play);
                    if(playway == 0) {
                        playWay.setImageResource(R.drawable.allrepeat);
                    } else if (playway == 1) {
                        playWay.setImageResource(R.drawable.allplay);
                    } else if (playway == 2) {
                        playWay.setImageResource(R.drawable.currentrepeat);
                    } else if (playway == 3) {
                        playWay.setImageResource(R.drawable.shuffle);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
