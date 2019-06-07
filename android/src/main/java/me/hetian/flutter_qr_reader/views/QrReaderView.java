package me.hetian.flutter_qr_reader.views;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.PointF;
import android.hardware.Camera;
import android.view.View;

import com.google.zxing.client.android.camera.CameraManager;

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

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        HashMap<String, Object> rest = new HashMap<String, Object>();
        rest.put("text", text);
        ArrayList<String> poi = new ArrayList<String>();
        for (PointF point : points) {
            poi.add(point.x + "," + point.y);
        }
        rest.put("points", poi);
        mMethodChannel.invokeMethod("onQRCodeRead", rest);
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
