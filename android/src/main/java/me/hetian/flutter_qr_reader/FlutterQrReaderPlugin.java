package me.hetian.flutter_qr_reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import java.io.File;

import androidx.annotation.NonNull;

import io.flutter.app.FlutterActivityEvents;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import me.hetian.flutter_qr_reader.factorys.QrReaderFactory;

/**
 * FlutterQrReaderPlugin
 */
public class FlutterQrReaderPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private static final String CHANNEL_NAME = "me.hetian.plugins/flutter_qr_reader";
    private static final String CHANNEL_VIEW_NAME = "me.hetian.plugins/flutter_qr_reader/reader_view";

    private MethodChannel channel;

    private Activity activity;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);

        binding.getPlatformViewRegistry().registerViewFactory(CHANNEL_VIEW_NAME, new QrReaderFactory(binding.getBinaryMessenger()));
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("imgQrCode")) {
            this.imgQrCode(call, result);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        this.activity = null;
    }

    @SuppressLint("StaticFieldLeak")
    class DecodeTask extends AsyncTask<String, Integer, String> {

        final private String filePath;
        final private Result result;

        private DecodeTask(String filePath, Result result) {
            super();
            this.filePath = filePath;
            this.result = result;
        }

        @Override
        protected String doInBackground(String... strs) {
            // 解析二维码/条码
            return QRCodeDecoder.syncDecodeQRCode(activity, filePath);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (null == s) {
                result.error("not data", null, null);
            } else {
                result.success(s);
            }
        }
    }

    void imgQrCode(MethodCall call, final Result result) {
        final String filePath = call.argument("file");
        if (filePath == null) {
            result.error("Not found data", null, null);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            result.error("File not found", null, null);
        }

        new DecodeTask(filePath, result).execute(filePath);
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
