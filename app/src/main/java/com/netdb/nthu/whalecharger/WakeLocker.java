package com.netdb.nthu.whalecharger;

/**
 * Created by user on 2016/7/30.
 */
import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, MainActivity.APP_OPS_SERVICE);
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}
