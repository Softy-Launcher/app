package com.Softy.Launcher2.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * Created by mcom on 1/29/17.
 */

public class Blurify {
    public static Blurify instance;
    public static RenderScript renderScript;

    public static void init(Context mContext){
        if(instance != null){
            return;
        }
        instance = new Blurify();
        instance.renderScript = RenderScript.create(mContext);
    }

   public static Bitmap blur(Bitmap src, int radius){
        final Allocation input = Allocation.createFromBitmap(renderScript, src);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        final ScriptIntrinsicBlur script;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
        }
        output.copyTo(src);
        return src;
    }

   public static Bitmap blur(View src,int radius){
        Bitmap bitmap = getBitmapForView(src, 1f);
        return blur(bitmap, radius);
    }

    public static Bitmap fastBlur(View src, int radius, float downScaleFactor){
        Bitmap bitmap = getBitmapForView(src, radius);
        return blur(bitmap, radius);
    }

    public static Bitmap getBitmapForView(View src, float downScaleFactor){
        Bitmap bitmap = Bitmap.createBitmap((int) (src.getWidth() * downScaleFactor),
                (int) (src.getHeight() * downScaleFactor),
                Bitmap.Config.ARGB_4444
        );

        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.preScale(downScaleFactor, downScaleFactor);
        canvas.setMatrix(matrix);
        src.draw(canvas);

        return bitmap;
    }

    public static Blurify get(){
        if(instance == null){
            throw new RuntimeException("Did you really not initialize Blurify?");
        }
        return instance;
    }

    public static Blurify setAndBlur(Bitmap src, int radius){
        if(instance == null){
            throw new RuntimeException("Did you really not initialize Blurify?");
        }else{
            blur(src, radius);
        }
        return instance;
    }

    public static Blurify setAndBlur(View src, int radius){
        if(instance == null){
            throw new RuntimeException("Did you really not initialize Blurify?");
        }else{
            blur(src, radius);
        }
        return instance;
    }

    public static Blurify immediatelyBlur(View src, int radius, float downScaleFactor){
        if(instance == null){
            throw new RuntimeException("Did you really not initialize Blurify?");
        }else{
            fastBlur(src, radius, downScaleFactor);
        }
        return instance;
    }
}
