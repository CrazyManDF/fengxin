package com.wx.app.fx.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.protocol.HttpService;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by darren foung on 2016/1/19.
 */
public class LoadUserAvatar {

    private static final int MAX_THREAD_NUM = 5;
    private BitmapCache bitmapCache;
    private final FileUtil fileUtil;
    private final ExecutorService executorService;

    public LoadUserAvatar(Context context, String local_image_path){
        bitmapCache = new BitmapCache();
        fileUtil = new FileUtil(context,local_image_path);
        executorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }

    public Bitmap loadImage(final ImageView imageView, final String imageUrl,
                            final ImageDownloadedCallBack imageDownloadedCallBack){
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        String filepath =  fileUtil.getAbsolutePath() + filename;

        Bitmap bitmap = bitmapCache.getBitmap(imageUrl);
        if (bitmap != null){
            Log.i("aaaa", "image exists in memory");
            return bitmap;
        }
        if(fileUtil.isBitmapExists(filename)){
            Log.i("aaaa", "image exists in file" + filename);
            bitmap = BitmapFactory.decodeFile(filepath);
            bitmapCache.putBitmap(imageUrl, bitmap);
            return bitmap;
        }

        if(imageUrl != null && !imageUrl.equals("")){

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 111 && imageDownloadedCallBack != null){
                        Bitmap bitmap = (Bitmap) msg.obj;
                        imageDownloadedCallBack.onImageDownloaded(imageView,
                                bitmap);
                    }
                }
            };

            Thread thread = new Thread(){
                @Override
                public void run() {
                    Log.i("aaaa", Thread.currentThread().getName()
                            + " is running");
                    // 网络加载图片
                    InputStream inputStream = HTTPService.getInstance()
                            .getStream(imageUrl);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 5;
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream,
                            null,options);

                    if(bitmap != null){
                        bitmapCache.putBitmap(imageUrl, bitmap);
                        fileUtil.saveBitmap(imageUrl, bitmap);
                        Message msg = handler.obtainMessage();
                        msg.what = 111;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }

                }
            };
            executorService.execute(thread);
        }
        return null;
    }

    /**
     * 图片下载完成回调接口
     *
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);
    }
}
