package me.hetian.flutter_qr_reader.factorys;

import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import me.hetian.flutter_qr_reader.views.QrReaderView;

public class QrReaderFactory extends PlatformViewFactory {
    BinaryMessenger binaryMessenger;
    public QrReaderFactory(BinaryMessenger binaryMessenger) {
        super(StandardMessageCodec.INSTANCE);
        this.binaryMessenger = binaryMessenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object args) {
        Map<String, Object> params = (Map<String, Object>) args;
        return new QrReaderView(context, binaryMessenger, id, params);
    }
}
