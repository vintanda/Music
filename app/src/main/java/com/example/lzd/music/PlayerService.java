package com.example.lzd.music;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LZD on 2016/10/6.
 */
public class PlayerService extends Service {
    Handler handle = new Handler();
    private ArrayList<MusicData> musicList = new ArrayList<>();

    private boolean isPlaying = false;
    private int i = 0;
    private int playway = 0;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();

        //获取歌曲信息
        musicList = getMusicData();
        //接收广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MUSIC_SERVICE);
        ServiceReceiver serviceReceiver = new ServiceReceiver();
        registerReceiver(serviceReceiver,intentFilter);

        initMediaPlayer(i);
        musicCompletion();
    }

    //获取歌曲信息
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
                MusicData d = new MusicData();
                d.setName(title);
                d.setArtist(artist);
                d.setAlbum(album);
                d.setDuration(duration);
                d.setId(id);
                d.setPath(path);
                d.setDisplay_name(display_name);
                musicList.add(d);
            } while (cursor.moveToNext());
        }
        //释放资源
        cursor.close();
        return musicList;
    }

    //判断是否结束当前曲目并作出反应
    private void musicCompletion() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(playway == 0 || playway == 1) {
                    if(i == musicList.size()-1) {
                        i = 0;
                    }else {
                        i++;
                    }
                } else if(playway == 2) {
                } else if(playway == 3) {
                    i = ((int) (Math.random()*100) % musicList.size());
                }
                initMediaPlayer(i);
                mediaPlayer.start();
                isPlaying = true;
                ServiceToMain(Constant.AUTO_NEXT,i,isPlaying);
                ServiceToPlay(Constant.AUTO_NEXT,i,isPlaying,playway);
            }
        });
    }

    //通知栏
    private void nitification() {
        Media media = musicList.get(position);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(PlayerService.this);
        builder.setSmallon(R.drawble.pic_cd);
        builder.setContentTitle("Music");
        Intent intent = new Intent(PlayerService.this,MainActivity.class);
        PendingIntent pendingIntent = new PendingIntent.getActivity(PlayerService.this,
                0,intent,pendingIntent.FLAG_UPDATE_CURREMT);
        builder.serContentIntent(pendingIntent);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(1,notification);
    }

    private void initMediaPlayer(int i) {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicList.get(i).getPath());
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ServiceToMain(int state,int i,boolean isPlaying) {
        Intent intent = new Intent(Constant.MUSIC_MAIN);
        intent.putExtra("command",state);
        intent.putExtra("position",i);
        intent.putExtra("isPlaying",isPlaying);
        sendBroadcast(intent);
    }

    public void ServiceToPlay(int state,int current,boolean isPlaying,int playway) {
        Intent intent = new Intent(Constant.MUSIC_PLAY);
        intent.putExtra("command",state);
        intent.putExtra("position",current);
        intent.putExtra("isPlaying",isPlaying);
        intent.putExtra("playway",playway);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int command = intent.getIntExtra("command",-1);
            int position = intent.getIntExtra("position",-1);
            switch (command) {
                case 4:      //暂停
                    mediaPlayer.pause();
                    isPlaying = false;
                    i = position;
                    ServiceToMain(Constant.PAUSE_ACTION,i,isPlaying);
                    ServiceToPlay(Constant.PAUSE_ACTION,i,isPlaying,playway);
                    break;
                case 5:      //播放
                    if(i == position) {
                        mediaPlayer.start();
                    }else {
                        mediaPlayer.reset();
                        try {
                            i = position;
                            mediaPlayer.setDataSource(musicList.get(i).getPath());
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                    isPlaying = true;
                    i = position;
                    ServiceToMain(Constant.PLAY_ACTION,i,isPlaying);
                    ServiceToPlay(Constant.PLAY_ACTION,i,isPlaying,playway);
                    break;
                case 6:
                    if(playway==3) {
                        i = (int)(Math.random()*100) % musicList.size();
                    }else {
                        if(i == 0) {
                            i = musicList.size()-1;
                        }else {
                            i--;
                        }
                    }
                    initMediaPlayer(i);
                    mediaPlayer.start();
                    isPlaying = true;
                    ServiceToMain(Constant.PROIR_ACTION,i,isPlaying);
                    ServiceToPlay(Constant.PROIR_ACTION,i,isPlaying,playway);
                    break;
                case 7:
                    if(playway==3) {
                        i = (int)(Math.random()*100) % musicList.size();
                    }else {
                        if(i == musicList.size()-1) {
                            i = 0;
                        }else {
                            i++;
                        }
                    }
                    initMediaPlayer(i);
                    mediaPlayer.start();
                    isPlaying = true;
                    ServiceToMain(Constant.NEXT_ACTION,i,isPlaying);
                    ServiceToPlay(Constant.NEXT_ACTION,i,isPlaying,playway);
                    break;
                case 0:
                    playway++;
                    ServiceToPlay(Constant.ALLREPEAT_ACTION,i,isPlaying,playway);
                    break;
                case 1:
                    playway++;
                    ServiceToPlay(Constant.ALLPLAY_ACTION,i,isPlaying,playway);
                    break;
                case 2:
                    playway++;
                    ServiceToPlay(Constant.CURRENTREPEAT_ACTION,i,isPlaying,playway);
                    break;
                case 3:
                    playway = 0;
                    ServiceToPlay(Constant.SHUFFLE_ACTION,i,isPlaying,playway);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
