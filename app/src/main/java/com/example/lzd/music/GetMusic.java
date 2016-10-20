package com.example.lzd.music;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by LZD on 2016/10/8.
 */
//获取信息
public class GetMusic {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static ArrayList<MusicData> getMusicData(Activity activity) {
        ArrayList<MusicData> MediaList = new ArrayList<>();
        //创建Cursor对象
        Cursor cursor = null;
        cursor = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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
}
