package me.hetian.flutter_qr_reader.reader;

import com.google.zxing.BarcodeFormat;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;


public class DecodeFormatManager {
    public static final int BARCODE_MODE = 2;
    public static final int QRCODE_MODE = 1;
    public static final int ALL_MODE = 0;

    // 1D解码
    static final Set<BarcodeFormat> PRODUCT_FORMATS;
    static final Set<BarcodeFormat> INDUSTRIAL_FORMATS;
    static final Set<BarcodeFormat> ONE_D_FORMATS;

    // 二维码解码
    static final Set<BarcodeFormat> QR_CODE_FORMATS;

    static {
        PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED);
        INDUSTRIAL_FORMATS = EnumSet.of(BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128, BarcodeFormat.ITF, BarcodeFormat.CODABAR);
        ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS);
        ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);

        QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
    }

    public static Collection<BarcodeFormat> getQrCodeFormats() {
        return QR_CODE_FORMATS;
    }

    public static Collection<BarcodeFormat> getBarCodeFormats() {
        return ONE_D_FORMATS;
    }
}
