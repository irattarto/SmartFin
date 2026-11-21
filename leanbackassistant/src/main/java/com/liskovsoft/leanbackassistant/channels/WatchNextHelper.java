package com.liskovsoft.leanbackassistant.channels;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.tvprovider.media.tv.TvContractCompat;
import androidx.tvprovider.media.tv.TvContractCompat.WatchNextPrograms;
import androidx.tvprovider.media.tv.WatchNextProgram;

import com.liskovsoft.sharedutils.mylogger.Log;
import com.liskovsoft.sharedutils.prefs.GlobalPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SmartFin: keeps the Android TV home screen "Continue watching" (Watch Next) row in sync with playback.<br/>
 * A video watched for at least a minute and not finished is added (or updated) with its progress; a finished or
 * barely started video is removed. Android 8.0+ only, follows the "home screen channels" setting.
 */
public final class WatchNextHelper {
    private static final String TAG = WatchNextHelper.class.getSimpleName();
    private static final long MIN_PROGRESS_MS = 60_000;
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();
    private static final String[] PROJECTION = {
            WatchNextPrograms._ID,
            WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID,
            WatchNextPrograms.COLUMN_BROWSABLE
    };

    private WatchNextHelper() {
    }

    public static void update(Context context, String videoId, String title, String author, String posterUrl,
                              long positionMs, long durationMs) {
        if (Build.VERSION.SDK_INT < 26 || context == null || TextUtils.isEmpty(videoId)) {
            return;
        }

        if (!GlobalPreferences.instance(context).isChannelsServiceEnabled()) {
            return;
        }

        // unknown duration ("mark as watched" stores -1) or reset to 0 -> handled as "not in progress" (entry removed)
        Context appContext = context.getApplicationContext();
        sExecutor.execute(() -> {
            try {
                updateInt(appContext, videoId, title, author, posterUrl, positionMs, durationMs);
            } catch (Exception e) { // no TV provider (non-TV devices), SecurityException, etc.
                Log.e(TAG, "Watch Next update failed: %s", e.getMessage());
            }
        });
    }

    /**
     * Removes one video from the row (e.g. its saved state was deleted)
     */
    public static void remove(Context context, String videoId) {
        update(context, videoId, null, null, null, 0, 0);
    }

    /**
     * Removes all SmartFin entries from the row (history cleared). Only this app's own rows are affected.
     */
    public static void removeAll(Context context) {
        if (Build.VERSION.SDK_INT < 26 || context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        sExecutor.execute(() -> {
            try {
                appContext.getContentResolver().delete(WatchNextPrograms.CONTENT_URI, null, null);
            } catch (Exception e) {
                Log.e(TAG, "Watch Next clear failed: %s", e.getMessage());
            }
        });
    }

    private static void updateInt(Context context, String videoId, String title, String author, String posterUrl,
                                  long positionMs, long durationMs) {
        ContentResolver resolver = context.getContentResolver();
        long existingId = -1;
        boolean existingBrowsable = true;

        try (Cursor cursor = resolver.query(WatchNextPrograms.CONTENT_URI, PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (videoId.equals(cursor.getString(1))) {
                        existingId = cursor.getLong(0);
                        existingBrowsable = cursor.getInt(2) != 0;
                        break;
                    }
                }
            }
        }

        boolean inProgress = durationMs > 0 && positionMs >= MIN_PROGRESS_MS && durationMs - positionMs >= MIN_PROGRESS_MS;

        if (!inProgress || !existingBrowsable) {
            // finished / not really started, or removed by the user from the row (browsable == 0): drop our entry
            if (existingId != -1) {
                resolver.delete(TvContractCompat.buildWatchNextProgramUri(existingId), null, null);
            }
            if (!inProgress) {
                return;
            }
            existingId = -1;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId))
                .setPackage(context.getPackageName()); // open in SmartFin, not in a chooser / another app

        WatchNextProgram.Builder builder = new WatchNextProgram.Builder();
        builder.setWatchNextType(WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE)
                .setLastEngagementTimeUtcMillis(System.currentTimeMillis())
                .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
                .setTitle(title)
                .setDescription(author)
                .setDurationMillis((int) Math.min(durationMs, Integer.MAX_VALUE))
                .setLastPlaybackPositionMillis((int) Math.min(positionMs, Integer.MAX_VALUE))
                .setInternalProviderId(videoId)
                .setIntent(intent);

        if (!TextUtils.isEmpty(posterUrl)) {
            builder.setPosterArtUri(Uri.parse(posterUrl))
                    .setPosterArtAspectRatio(TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9);
        }

        WatchNextProgram program = builder.build();

        if (existingId != -1) {
            resolver.update(TvContractCompat.buildWatchNextProgramUri(existingId), program.toContentValues(), null, null);
        } else {
            resolver.insert(WatchNextPrograms.CONTENT_URI, program.toContentValues());
        }
    }
}
