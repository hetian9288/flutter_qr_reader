package me.hetian.flutter_qr_reader.views;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.zxing.client.android.camera.CameraManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;
import me.hetian.flutter_qr_reader.readerView.QRCodeReaderView;

public class QrReaderView implements PlatformView, QRCodeReaderView.OnQRCodeReadListener, MethodChannel.MethodCallHandler {

    private final MethodChannel mMethodChannel;
    private final Context mContext;
    private Map<String, Object> mParams;
    private PluginRegistry.Registrar mRegistrar;
    QRCodeReaderView _view;

    public static String EXTRA_FOCUS_INTERVAL = "extra_focus_interval";
    public static String EXTRA_TORCH_ENABLED = "extra_torch_enabled";
    public static String EXTRA_NEED_PHOTO = "extra_need_photo";
    private boolean needPhoto = false;//是否需要拍照

    public QrReaderView(Context context, PluginRegistry.Registrar registrar, int id, Map<String, Object> params){
        this.mContext = context;
        this.mParams = params;
        this.mRegistrar = registrar;

        // 创建视图
        int width = (int) mParams.get("width");
        int height = (int) mParams.get("height");
        _view = new QRCodeReaderView(mContext);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(width, height);
        _view.setLayoutParams(layoutParams);
        _view.setOnQRCodeReadListener(this);
        _view.setQRDecodingEnabled(true);
        _view.forceAutoFocus();
        int interval = mParams.containsKey(EXTRA_FOCUS_INTERVAL) ? (int) mParams.get(EXTRA_FOCUS_INTERVAL) : 2000;
        _view.setAutofocusInterval(interval);
        _view.setTorchEnabled((boolean)mParams.get(EXTRA_TORCH_ENABLED));
        if(mParams.get(EXTRA_NEED_PHOTO)!=null){
            needPhoto = (boolean) mParams.get(EXTRA_NEED_PHOTO);
        }
        _view.setNeedPhoto(needPhoto);//增加是否需要拍照

        // 操作监听
        mMethodChannel = new MethodChannel(registrar.messenger(), "me.hetian.flutter_qr_reader.reader_view_" + id);
        mMethodChannel.setMethodCallHandler(this);
    }

    @Override
    public View getView() {
        return _view;
    }

    @Override
    public void dispose() {
        _view = null;
        mParams = null;
        mRegistrar = null;
    }

//    @Override
//    public void onQRCodeRead(String text, PointF[] points) {
//        HashMap<String, Object> rest = new HashMap<String, Object>();
//        rest.put("text", text);
//        ArrayList<String> poi = new ArrayList<String>();
//        for (PointF point : points) {
//            poi.add(point.x + "," + point.y);
//        }
//        rest.put("points", poi);
//        mMethodChannel.invokeMethod("onQRCodeRead", rest);
//    }

    @Override
    public void onQRCodeRead(String text,byte[] photoBytes,int rotation, PointF[] points) {
        Log.i("二维码识别",text);
        Log.i("图片角度",rotation+"");
        synchronized (this) {
            if (needPhoto && photoBytes != null && photoBytes.length > 0) {
                try {
                    Bitmap bm = bm = bitmapRotation(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length), rotation);
                    File file = null;
                    if (bm != null) {
                        String FILE_NAME = "qrcode.jpg";
//                        if (Build.VERSION.SDK_INT>=29){
//                            //Android10之后
//                            file = new File(mContext.getExternalCacheDir(), FILE_NAME);
//                        }else {
//                            file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
//                        }

                        file = new File(mContext.getExternalFilesDir(null), FILE_NAME);
                        if (!file.exists()) {
                            File dir = file.getParentFile();
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            file.createNewFile();
                        }
                        Log.i("文件路径", file.getAbsolutePath());
                        FileOutputStream fos = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.flush();
                        fos.close();
                        bm.recycle();
                    }


                    HashMap<String, Object> rest = new HashMap<String, Object>();
                    rest.put("text", text);
                    ArrayList<String> poi = new ArrayList<String>();
                    for (PointF point : points) {
                        poi.add(point.x + "," + point.y);
                    }
                    rest.put("points", poi);
                    if (file != null) {
                        rest.put("photoPath", file.getAbsolutePath());
                        mMethodChannel.invokeMethod("onQRCodeRead", rest);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                HashMap<String, Object> rest = new HashMap<String, Object>();
                rest.put("text", text);
                ArrayList<String> poi = new ArrayList<String>();
                for (PointF point : points) {
                    poi.add(point.x + "," + point.y);
                }
                rest.put("points", poi);
                mMethodChannel.invokeMethod("onQRCodeRead", rest);
            }
        }
    }
    /*
     *
     * @Title: bitmapRotation
     * @Description: 图片旋转
     * @param bm
     * @param orientationDegree
     * @return Bitmap
     * @throws
     */
    public Bitmap bitmapRotation(Bitmap bm, final int orientationDegree) {
        if(bm!=null){
            Matrix m = new Matrix();
            m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
                    (float) bm.getHeight() / 2);
            float targetX, targetY;
            if (orientationDegree == 90) {
                targetX = bm.getHeight();
                targetY = 0;
            } else if (orientationDegree == 270) {
                targetX = 0;
                targetY = bm.getWidth();
            } else {
                targetX = bm.getHeight();
                targetY = bm.getWidth();
            }

            final float[] values = new float[9];
            m.getValues(values);

            float x1 = values[Matrix.MTRANS_X];
            float y1 = values[Matrix.MTRANS_Y];

            m.postTranslate(targetX - x1, targetY - y1);

            Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
                    Bitmap.Config.ARGB_8888);

            Paint paint = new Paint();
            Canvas canvas = new Canvas(bm1);
            canvas.drawBitmap(bm, m, paint);
            bm.recycle();//释放
            return bm1;
        }else{
            return null;
        }



    }

    boolean flashlight;
    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "flashlight":
                _view.setTorchEnabled(!flashlight);
                flashlight = !flashlight;
                result.success(flashlight);
                break;
            case "startCamera":
                _view.startCamera();
                break;
            case "stopCamera":
                _view.stopCamera();
                break;
        }

    }
}
