package com.wx.app.fx.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by darren foung on 2016/1/19.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    private String local_image_path;

    public FileUtil(Context context,String local_image_path) {
        this.local_image_path = local_image_path;
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public String getAbsolutePath(){
        File root = new File(local_image_path);
        if(!root.exists()){
            root.mkdirs();
        }
        return local_image_path;
    }

    public boolean isBitmapExists(String filename){
        File dir = new File(local_image_path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        return file.exists();
    }

    public void saveBitmap(String filename, Bitmap bitmap){
        if(!isExternalStorageWritable()){
            Log.i(TAG, "SD卡不可用，保存失败");
            return;
        }
        if(bitmap == null){
            return;
        }
        try {
            File file = new File(local_image_path, filename);
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if((filename.indexOf("png")!=-1) || (filename.indexOf("PNG") != -1)){
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
