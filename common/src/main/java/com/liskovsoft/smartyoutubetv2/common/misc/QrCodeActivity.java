package com.liskovsoft.smartyoutubetv2.common.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liskovsoft.smartyoutubetv2.common.utils.QrCodeHelper;

/**
 * SmartFin: full-screen QR code generated on the device. Any OK/Back key closes it.
 */
public class QrCodeActivity extends Activity {
    private static final String EXTRA_DATA = "qr_data";
    private static final String EXTRA_TITLE = "qr_title";

    public static void show(Context context, String data, String title) {
        if (context == null || data == null || data.isEmpty()) {
            return;
        }

        Intent intent = new Intent(context, QrCodeActivity.class)
                .putExtra(EXTRA_DATA, data)
                .putExtra(EXTRA_TITLE, title);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String data = getIntent().getStringExtra(EXTRA_DATA);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int qrSize = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) * 0.6f);
        Bitmap qr = QrCodeHelper.create(data, qrSize);

        if (qr == null) {
            finish();
            return;
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.BLACK);
        int pad = dp(24);
        root.setPadding(pad, pad, pad, pad);

        if (title != null && !title.isEmpty()) {
            root.addView(createText(title, 22, Color.WHITE));
        }

        ImageView image = new ImageView(this);
        image.setImageBitmap(qr);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(qrSize, qrSize);
        imageParams.topMargin = dp(16);
        imageParams.bottomMargin = dp(16);
        root.addView(image, imageParams);

        root.addView(createText(data, 14, Color.LTGRAY));

        setContentView(root);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            finish();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private TextView createText(String text, int sizeSp, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        view.setGravity(Gravity.CENTER);
        view.setMaxLines(3);
        return view;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
