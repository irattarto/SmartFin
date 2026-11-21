package com.liskovsoft.smartyoutubetv2.common.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.Video;
import com.liskovsoft.smartyoutubetv2.common.prefs.common.DataChangeBase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RemoteControlData extends DataChangeBase {
    private static final String DEVICE_LINK_DATA = "device_link_data";
    @SuppressLint("StaticFieldLeak")
    private static RemoteControlData sInstance;
    private final Context mContext;
    private final AppPrefs mAppPrefs;
    private boolean mIsDeviceLinkEnabled;
    private boolean mIsRunInBackgroundEnabled;
    private boolean mIsFinishOnDisconnectEnabled;
    private boolean mIsConnectMessagesEnabled;
    private boolean mIsRemoteHistoryDisabled;
    private Video mLastVideo;
    private boolean mIsConnectedBefore;
    private boolean mIsDialEnabled;
    private final List<String> mDialTrustedHosts = new ArrayList<>(); // SmartFin: phones allowed to cast without asking
    private String mDialUdn;

    private RemoteControlData(Context context) {
        mContext = context;
        mAppPrefs = AppPrefs.instance(mContext);
        restoreState();
    }

    public static RemoteControlData instance(Context context) {
        if (sInstance == null) {
            sInstance = new RemoteControlData(context.getApplicationContext());
        }

        return sInstance;
    }

    public void enableDeviceLink(boolean select) {
        mIsDeviceLinkEnabled = select;
        persistState();
    }

    public boolean isDeviceLinkEnabled() {
        // Merge device link and background service (saves memory)
        return mIsDeviceLinkEnabled;
    }

    public void enableFinishOnDisconnect(boolean enable) {
        mIsFinishOnDisconnectEnabled = enable;
        persistState();
    }

    public boolean isFinishOnDisconnectEnabled() {
        return mIsFinishOnDisconnectEnabled;
    }

    public void enableConnectMessages(boolean enable) {
        mIsConnectMessagesEnabled = enable;
        persistState();
    }

    public boolean isConnectMessagesEnabled() {
        return mIsConnectMessagesEnabled;
    }

    public void disableRemoteHistory(boolean disable) {
        mIsRemoteHistoryDisabled = disable;
        persistState();
    }

    public boolean isRemoteHistoryDisabled() {
        return mIsRemoteHistoryDisabled;
    }

    public Video getLastVideo() {
        return mLastVideo;
    }

    public void setLastVideo(Video video) {
        mLastVideo = video;
        persistState();
    }

    public void enableDial(boolean enable) {
        mIsDialEnabled = enable;
        persistState();
    }

    /**
     * SmartFin: casting (DIAL) phones the user chose to "Always allow", identified by their LAN address
     */
    public boolean isDialTrustedHost(String host) {
        return host != null && mDialTrustedHosts.contains(host);
    }

    public void addDialTrustedHost(String host) {
        if (host != null && !mDialTrustedHosts.contains(host)) {
            mDialTrustedHosts.add(host);
            persistState();
        }
    }

    public void clearDialTrustedHosts() {
        mDialTrustedHosts.clear();
        persistState();
    }

    public int getDialTrustedHostCount() {
        return mDialTrustedHosts.size();
    }

    public boolean isDialEnabled() {
        return mIsDialEnabled;
    }

    /**
     * The lounge listener (background service) is needed for "Link with TV code" and for casting over DIAL.
     */
    public boolean isRemoteListeningEnabled() {
        return mIsDeviceLinkEnabled || mIsDialEnabled;
    }

    /**
     * Stable unique device name for DIAL discovery.
     */
    public String getDialUdn() {
        if (mDialUdn == null) {
            mDialUdn = UUID.randomUUID().toString();
            persistState();
        }
        return mDialUdn;
    }

    public void setConnectedBefore(boolean connected) {
        mIsConnectedBefore = connected;
        persistState();
    }

    public boolean isConnectedBefore() {
        return mIsConnectedBefore;
    }

    private void restoreState() {
        String data = mAppPrefs.getData(DEVICE_LINK_DATA);

        String[] split = Helpers.splitData(data);

        // null
        // null
        mIsDeviceLinkEnabled = Helpers.parseBoolean(split, 2, false);
        mIsFinishOnDisconnectEnabled = Helpers.parseBoolean(split, 3, false);
        mIsConnectMessagesEnabled = Helpers.parseBoolean(split, 4, false);
        mIsRemoteHistoryDisabled = Helpers.parseBoolean(split, 5, false);
        mLastVideo = Helpers.parseItem(split, 6, Video::fromString);
        mIsConnectedBefore = Helpers.parseBoolean(split, 7, false);
        mIsDialEnabled = Helpers.parseBoolean(split, 8, false);
        mDialUdn = Helpers.parseStr(split, 9);
        mDialTrustedHosts.clear();
        mDialTrustedHosts.addAll(Helpers.parseStrList(split, 10));
    }

    private void persistState() {
        mAppPrefs.setData(DEVICE_LINK_DATA, Helpers.mergeData(
                null, null, mIsDeviceLinkEnabled, mIsFinishOnDisconnectEnabled, mIsConnectMessagesEnabled,
                mIsRemoteHistoryDisabled, mLastVideo, mIsConnectedBefore, mIsDialEnabled, mDialUdn, Helpers.mergeList(mDialTrustedHosts)
        ));

        onDataChange();
    }
}
