package com.example.lzd.music;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;
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
        setContentView(R.layout.activity_main);

        //获取信息，加载适配器，ListView点击事件
        MediaList = getMusicData();
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

        //按钮点击事件
        MyOnClickListener myonclicklistener = new MyOnClickListener();
        imageview.setOnClickListener(myonclicklistener);
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

    //获取信息
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ArrayList<MusicData> getMusicData() {
        //创建Cursor对象
        Cursor cursor = null;
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.TITLE, //歌曲名
                        MediaStore.Audio.Media.ARTIST, //歌手名
                        MediaStore.Audio.Media.ALBUM, //专辑名
                        MediaStore.Audio.Media.DURATION, //歌曲时长
                        MediaStore.Audio.Media._ID, //歌曲ID
                        MediaStore.Audio.Media.DATA, //歌曲路径 url
                        MediaStore.Audio.Media.DISPLAY_NAME //显示全部名字
                }, null, null, null, null);

        //光标指在第一行
        if (cursor.moveToFirst()) {
            do {
                //遍历Cursor对象取出数据
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                //将数据存入集合中
                MusicData md = new MusicData();
                md.setName(title);
                md.setArtist(artist);
                md.setAlbum(album);
                md.setDuration(duration);
                md.setId(id);
                md.setPath(path);
                md.setDisplay_name(display_name);
                MediaList.add(md);
            } while (cursor.moveToNext());
        }
        //释放资源
        cursor.close();
        return MediaList;
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

//                intent.putExtra("command",Constant.PLAY_ACTION);
//                intent.putExtra("position",position);
//                sendBroadcast(intent);
            }
        });
    }

    //按钮点击事件
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.pic_small:
                    //页面跳转，点击列表页左下角图案跳转到播放界面
                    intent = new Intent(MainActivity.this, PlayActivity.class);
                    startActivity(intent);
                    break;
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
                        isPlaying = false;
                        toPlayPause.setImageResource(R.drawable.pause);
                        intent.putExtra("command", Constant.PAUSE_ACTION);
                        intent.putExtra("position",i);
                    }else {
                        isPlaying = true;
                        toPlayPause.setImageResource(R.drawable.play);
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

            textview.setText(MediaList.get(i).getName() + " " + MediaList.get(i).getArtist());
            switch (command) {
                case 4:
                    toPlayPause.setImageResource(R.drawable.pause);
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    toPlayPause.setImageResource(R.drawable.play);
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
