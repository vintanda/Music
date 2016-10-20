package com.example.lzd.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<MusicData> MediaList = new ArrayList<>();
    private boolean isPlaying = false;
    private int state = 0;
    private int i = 0;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //初始化控件
    private ImageView imageview;
    private TextView textview;
    private ImageView toPrior;
    private ImageView toPlayPause;
    private ImageView toNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //获取信息，加载适配器，ListView点击事件
        MediaList = GetMusic.getMusicData(this);
        MediaAdapter adapter = new MediaAdapter(MainActivity.this, R.layout.listview, MediaList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        press(adapter,listView);

        //按钮实例化
        imageview = (ImageView) findViewById(R.id.pic_small);
        textview = (TextView) findViewById(R.id.nametext);
        toPrior = (ImageView) findViewById(R.id.pic_prior);
        toPlayPause = (ImageView) findViewById(R.id.pic_play);
        toNext = (ImageView) findViewById(R.id.pic_next);

        //textview初始化
        textview.setText(MediaList.get(0).getName() + "  " + MediaList.get(0).getArtist());

        //按钮点击事件
        MyOnClickListener myonclicklistener = new MyOnClickListener();
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        textview.setOnClickListener(myonclicklistener);
        toPrior.setOnClickListener(myonclicklistener);
        toPlayPause.setOnClickListener(myonclicklistener);
        toNext.setOnClickListener(myonclicklistener);

        //接收广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MUSIC_MAIN);
        MainReceiver mainReceiver = new MainReceiver();
        registerReceiver(mainReceiver,intentFilter);

        //启动服务
        Intent intent = new Intent(MainActivity.this,PlayerService.class);
        startService(intent);
    }

    //ListView点击事件
    private void press(final MediaAdapter adapter, final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicData musicdata = MediaList.get(position);
                textview.setText(musicdata.getName() + " " + musicdata.getArtist());
                Intent intent = new Intent(Constant.MUSIC_SERVICE);
                intent.putExtra("command",Constant.PLAY_ACTION);
                intent.putExtra("position",position);
                sendBroadcast(intent);
            }
        });
    }

    //按钮点击事件
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.List_text:
                    break;
                case R.id.pic_prior:
                    intent = new Intent(Constant.MUSIC_SERVICE);
                    intent.putExtra("command",Constant.PROIR_ACTION);
                    intent.putExtra("position",i);
                    sendBroadcast(intent);
                    break;
                case R.id.pic_play:
                    intent = new Intent(Constant.MUSIC_SERVICE);
                    if(isPlaying) {
                        isPlaying = mediaPlayer.isPlaying();
                        intent.putExtra("command", Constant.PAUSE_ACTION);
                        intent.putExtra("position",i);
                    }else {
                        isPlaying = mediaPlayer.isPlaying();
                        intent.putExtra("command",Constant.PLAY_ACTION);
                        intent.putExtra("position",i);
                    }
                    sendBroadcast(intent);
                    break;
                case R.id.pic_next:
                    intent = new Intent(Constant.MUSIC_SERVICE);
                    intent.putExtra("command",Constant.NEXT_ACTION);
                    intent.putExtra("position",i);
                    sendBroadcast(intent);
                    break;
            }
        }
    }

    //接收广播
    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int command = intent.getIntExtra("command",-1);
            i = intent.getIntExtra("position",-1);
            isPlaying = intent.getBooleanExtra("isPlaying",false);

            MusicData musicdata = MediaList.get(i);
            textview.setText(musicdata.getName() + " " + musicdata.getArtist());
            switch (command) {
                case 4:
                    toPlayPause.setImageResource(R.drawable.play);
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    toPlayPause.setImageResource(R.drawable.pause);
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
