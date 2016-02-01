package com.wx.app.fx.others;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by darren foung on 2016/1/19.
 */
public class BitmapCache {

    private LruCache<String, Bitmap> mCache;

    public BitmapCache(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = maxMemory / 8;
        mCache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public Bitmap getBitmap(String url){
        return mCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap){
        mCache.put(url, bitmap);
    }
}
