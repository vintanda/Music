package com.example.lzd.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LZD on 2016/10/6.
 */
public class PlayActivity extends AppCompatActivity {
    //Handler handler = new Handler();
    ArrayList<MusicData> musiclist = new ArrayList<>();

    MediaPlayer mediaPlayer = new MediaPlayer();

    private int i = 0;
    private boolean isPlaying = false;
    private int playway = 0;

    //初始化控件
    private TextView textView;
    private ImageView imageView;
    private ImageView playWay;
    private ImageView toPrior;
    private ImageView toPlayPause;
    private ImageView toNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.play_ui);

        //自定义标题栏
//        Button button = (Button) findViewById(R.id.back);
//        button.setOnClickListener(new View.OnClickListener() {
//            //点击事件，back按钮返回到MainActivity
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PlayActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        //控件实例化
        textView = (TextView) findViewById(R.id.musicname_play);
        imageView = (ImageView) findViewById(R.id.image);
        playWay = (ImageView) findViewById(R.id.playway);
        toPrior = (ImageView) findViewById(R.id.to_prior);
        toPlayPause = (ImageView) findViewById(R.id.to_play);
        toNext = (ImageView) findViewById(R.id.to_next);

        //获取信息
        musiclist = GetMusic.getMusicData(this);

        //点击事件
        MyOnClickListener onClickListener = new MyOnClickListener();
        playWay.setOnClickListener(onClickListener);
        toPrior.setOnClickListener(onClickListener);
        toPlayPause.setOnClickListener(onClickListener);
        toNext.setOnClickListener(onClickListener);

        //注册接收广播对象
        PlayReceiver playReceiver = new PlayReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MUSIC_PLAY);
        registerReceiver(playReceiver,intentFilter);

        //启动服务
        Intent intent = new Intent(PlayActivity.this,PlayerService.class);
        startService(intent);
    }

    private class MyOnClickListener implements View.OnClickListener {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playway:
                    if(playway == 0) {
                        intent = new Intent(Constant.MUSIC_SERVICE);
                        playWay.setImageResource(R.drawable.allplay);
                        intent.putExtra("command",Constant.ALLPLAY_ACTION);
                        intent.putExtra("position",i);
                    } else if(playway == 1) {
                        intent = new Intent(Constant.MUSIC_SERVICE);
                        playWay.setImageResource(R.drawable.currentrepeat);
                        intent.putExtra("command",Constant.CURRENTREPEAT_ACTION);
                        intent.putExtra("position",i);
                    } else if(playway == 2) {
                        intent = new Intent(Constant.MUSIC_SERVICE);
                        playWay.setImageResource(R.drawable.shuffle);
                        intent.putExtra("command",Constant.SHUFFLE_ACTION);
                        intent.putExtra("position",i);
                    } else if(playway == 3) {
                        intent = new Intent(Constant.MUSIC_SERVICE);
                        playWay.setImageResource(R.drawable.allrepeat);
                        intent.putExtra("command",Constant.ALLREPEAT_ACTION);
                        intent.putExtra("position",i);
                    }
                    sendBroadcast(intent);
                    break;
                case R.id.to_prior:
                    intent = new Intent(Constant.MUSIC_SERVICE);
                    intent.putExtra("command",Constant.PROIR_ACTION);
                    intent.putExtra("position",i);
                    sendBroadcast(intent);
                    break;
                case R.id.to_play:
                    intent = new Intent(Constant.MUSIC_SERVICE);
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
                    intent = new Intent(Constant.MUSIC_SERVICE);
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

            textView.setText(musiclist.get(i).getName() + "  " + musiclist.get(i).getArtist());

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
                    toPlayPause.setImageResource(R.drawable.play);
                    break;
                case 5:
                case 6:
                case 7:
                    toPlayPause.setImageResource(R.drawable.pause);
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
