package com.songhaoxiang.myfirstapp.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import com.songhaoxiang.myfirstapp.MainActivity;
import java.io.IOException;
import java.io.InputStream;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by user on 2016/7/6.
 */
public class BitmapUtil{
    private Activity mainActivity;
    private String imageUrl;

    public BitmapUtil(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public BitmapUtil(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getScare(String imageUrl) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imageUrl);
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (200 == code) {
                InputStream is = response.getEntity().getContent();
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, opts);

                int imageWidth = opts.outWidth;
                int imageHeight = opts.outHeight;
                int screenWidth = 128;
                int screenHeight = 128;
                if (mainActivity != null) {
                    Display display = this.mainActivity.getWindowManager()
                            .getDefaultDisplay();
                    screenWidth = display.getWidth();
                    screenHeight = display.getHeight();
                }
                int widthscale = imageWidth / screenWidth;
                int heightscale = imageHeight / screenHeight;
                int scale = widthscale > heightscale ? widthscale : heightscale;

                return scale;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;//网络连接失败时默认返回1
    }

    /**
     * 获取网络图片
     */
    public Bitmap getNetImage(String imageUrl) {
        Bitmap bm = null;
        HttpClient client = new DefaultHttpClient();
        System.out.println("imageUrl=="+imageUrl);
        HttpGet httpGet = new HttpGet(imageUrl);
        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int code = response.getStatusLine().getStatusCode();

        if (200 == code) {
            InputStream is = null;
            try {
                is = response.getEntity().getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();

            //根据计算出的比例进行缩放
            int scale = getScare(imageUrl);
            System.out.println("缩放比例是：" + scale);
            opts.inSampleSize = scale;
            bm = BitmapFactory.decodeStream(is, null, opts);
        }
        return bm;
    }
    public Bitmap getGaosiBitmap(Bitmap bitmap){
        Bitmap outBitmap = bitmap;
        for(int i = 0;i<3;i++){
            outBitmap = blurBitmap(outBitmap);
        }
        return outBitmap;
    }


    public Bitmap blurBitmap(Bitmap bitmap){
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(mainActivity);
        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        //Set the radius of the blur
        blurScript.setRadius(25.f);
        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        //recycle the original bitmap
        bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;


    }

}