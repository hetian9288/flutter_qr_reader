package me.hetian.flutter_qr_reader.reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;

import java.nio.ByteBuffer;

public final class MyRGBLuminanceSource extends LuminanceSource {

    private final byte[] luminances;
    private final int dataWidth;
    private final int dataHeight;
    private final int left;
    private final int top;

    public MyRGBLuminanceSource(int width, int height, int[] pixels) {
        super(width, height);

        dataWidth = width;
        dataHeight = height;
        left = 0;
        top = 0;

        // In order to measure pure decoding speed, we convert the entire image to a greyscale array
        // up front, which is the same as the Y channel of the YUVLuminanceSource in the real app.
        //
        // Total number of pixels suffices, can ignore shape
        int size = width * height;
        luminances = new byte[size];
        for (int offset = 0; offset < size; offset++) {
            int pixel = pixels[offset];
            int r = (pixel >> 16) & 0xff; // red
            int g2 = (pixel >> 7) & 0x1fe; // 2 * green
            int b = pixel & 0xff; // blue
            // Calculate green-favouring average cheaply
            luminances[offset] = (byte) ((r + g2 + b) / 4);
        }

    }

    private MyRGBLuminanceSource(byte[] pixels,
                                 int dataWidth,
                                 int dataHeight,
                                 int left,
                                 int top,
                                 int width,
                                 int height) {
        super(width, height);
        if (left + width > dataWidth || top + height > dataHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
        this.luminances = pixels;
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        this.left = left;
        this.top = top;

    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        int offset = (y + top) * dataWidth + left;
        System.arraycopy(luminances, offset, row, 0, width);
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();

        // If the caller asks for the entire underlying image, save the copy and give them the
        // original data. The docs specifically warn that result.length must be ignored.
        if (width == dataWidth && height == dataHeight) {
            return luminances;
        }

        int area = width * height;
        byte[] matrix = new byte[area];
        int inputOffset = top * dataWidth + left;

        // If the width matches the full width of the underlying data, perform a single copy.
        if (width == dataWidth) {
            System.arraycopy(luminances, inputOffset, matrix, 0, area);
            return matrix;
        }

        // Otherwise copy one cropped row at a time.
        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            System.arraycopy(luminances, inputOffset, matrix, outputOffset, width);
            inputOffset += dataWidth;
        }
        return matrix;
    }

    /**
     * 旋转90角度
     *
     * @return
     */
    @Override
    public LuminanceSource rotateCounterClockwise() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return super.rotateCounterClockwise();
        }
        return new MyRGBLuminanceSource(luminances, dataWidth, dataHeight, 0, 0, dataWidth, dataHeight);
//        Matrix matrix = new Matrix(); //旋转图片 动作
//        matrix.setRotate(90);//旋转角度
////        Bitmap.(dataWidth, dataHeight, luminances);
//        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, dataHeight, dataWidth, matrix, true);
//        int bytes = resizedBitmap.getByteCount();
//        ByteBuffer buffer = ByteBuffer.allocate(bytes);
//        resizedBitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
//
//        byte[] data = buffer.array();
//        return new MyRGBLuminanceSource(data, dataHeight, dataWidth, 0, 0, dataHeight, dataWidth);
    }

    @Override
    public boolean isCropSupported() {
        return true;
    }

    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
        return new MyRGBLuminanceSource(luminances,
                dataWidth,
                dataHeight,
                this.left + left,
                this.top + top,
                width,
                height);
    }

}