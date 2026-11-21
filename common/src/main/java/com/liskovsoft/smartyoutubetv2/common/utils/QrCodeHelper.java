package com.liskovsoft.smartyoutubetv2.common.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

/**
 * SmartFin: QR codes are generated on the device (ZXing).<br/>
 * Upstream sent the payload (shared links, even the sign-in link) to an online QR service.
 */
public final class QrCodeHelper {
    private QrCodeHelper() {
    }

    /**
     * @return black-on-white QR code bitmap or null if the text can't be encoded
     */
    @Nullable
    public static Bitmap create(String text, int sizePx) {
        if (text == null || text.isEmpty() || sizePx <= 0) {
            return null;
        }

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2); // quiet zone (modules)

        try {
            BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            // RGB_565: half the memory of ARGB_8888, no alpha needed
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException | IllegalArgumentException | OutOfMemoryError e) {
            return null;
        }
    }
}
