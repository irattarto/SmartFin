package com.liskovsoft.smartyoutubetv2.tv.util;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.liskovsoft.smartyoutubetv2.common.utils.Utils;

/**
 * https://bumptech.github.io/glide/doc/configuration.html#disk-cache<br/>
 * https://stackoverflow.com/questions/46108915/how-to-increase-the-cache-size-in-glide-android
 */
@GlideModule
public class GlideCachingModule extends AppGlideModule {
    // SmartFin: 10 MB was too small - thumbnails were downloaded again all the time
    private final static long CACHE_SIZE = 50 * 1024 * 1024; // 50 MB
    private final static long CACHE_SIZE_LOW_RAM = 25 * 1024 * 1024; // 25 MB (low-end boxes usually have little storage too)

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //if (MyApplication.from(context).isTest())
        //    return; // NOTE: StatFs will crash on robolectric.

        boolean isLowRam = Utils.isLowRamDevice(context);

        // Limit cache size
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, isLowRam ? CACHE_SIZE_LOW_RAM : CACHE_SIZE));

        if (isLowRam) {
            // SmartFin low-RAM mode: one screen of memory cache/bitmap pool and 16-bit thumbnails (half the memory)
            builder.setMemorySizeCalculator(new MemorySizeCalculator.Builder(context)
                    .setMemoryCacheScreens(1)
                    .setBitmapPoolScreens(1)
                    .build());
            builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
        }
    }
}
