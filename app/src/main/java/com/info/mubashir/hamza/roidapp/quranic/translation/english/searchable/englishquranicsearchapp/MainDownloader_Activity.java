package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.google.android.vending.expansion.downloader.Constants;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class MainDownloader_Activity extends Activity implements IDownloaderClient {

    private IDownloaderService mRemoteService;
    private IStub mDownloaderClientStub;
    private int mState;
    private boolean mCancelValidation;
    private static String TAG = "Quranic";
    private RelativeLayout mDownloadViewGroup;
    private ProgressBar mDownloadProgressBar;
    private TextView mProgressPercentTextView;
    private TextView mDownloadMenu;
    private TextView mDownloadTitle;
    private LinearLayout mLinearLayout;
    private static int EXPANSION_NUMBER = 9;
    private AlertDialog alertDialog;

    // region Expansion Downloader
    private static class XAPKFile {
        public final boolean mIsMain;
        public final int mFileVersion;
        public final long mFileSize;

        XAPKFile(boolean isMain, int fileVersion, long fileSize) {
            mIsMain = isMain;
            mFileVersion = fileVersion;
            mFileSize = fileSize;
        }
    }

    private static final XAPKFile[] xAPKS = {
            new XAPKFile(
                    true, // true signifies a main file
                    EXPANSION_NUMBER, // the version of the APK that the file was uploaded against
                    902760000L// the length of the file in bytes
            )
    };
    static private final float SMOOTHING_FACTOR = 0.005f;

    /**
     * Connect the stub to our service on start.
     */
    @Override
    protected void onStart() {
        if (null != mDownloaderClientStub) {
            mDownloaderClientStub.connect(this);
        }
        super.onStart();
    }

    /**
     * Disconnect the stub from our service on stop
     */
    @Override
    protected void onStop() {
        if (null != mDownloaderClientStub) {
            mDownloaderClientStub.disconnect(this);
        }
        super.onStop();
    }

    /**
     * Critical implementation detail. In onServiceConnected we create the
     * remote service and marshaler. This is how we pass the client information
     * back to the service so the client can be properly notified of changes. We
     * must do this every time we reconnect to the service.
     */
    @Override
    public void onServiceConnected(Messenger m) {
        mRemoteService = DownloaderServiceMarshaller.CreateProxy(m);
        mRemoteService.onClientUpdated(mDownloaderClientStub.getMessenger());
    }

    /**
     * The download state should trigger changes in the UI --- it may be useful
     * to show the state as being indeterminate at times. This sample can be
     * considered a guideline.
     */
    @Override
    public void onDownloadStateChanged(int newState) {
        setState(newState);
        boolean showDashboard = true;
        boolean showCellMessage = false;
        boolean paused;
        boolean indeterminate = true;
        switch (newState) {
            case IDownloaderClient.STATE_IDLE:
                // STATE_IDLE means the service is listening, so it's
                // safe to start making calls via mRemoteService.
                paused = false;
                indeterminate = true;
                break;
            case IDownloaderClient.STATE_CONNECTING:
            case IDownloaderClient.STATE_FETCHING_URL:
                showDashboard = true;
                paused = false;
                indeterminate = true;
                break;
            case IDownloaderClient.STATE_DOWNLOADING:
                paused = false;
                showDashboard = true;
                indeterminate = false;
                break;

            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
                final android.app.AlertDialog.Builder alertfail = new android.app.AlertDialog.Builder(MainDownloader_Activity.this);
                alertfail.setTitle(getResources().getString(R.string.error));
                alertfail.setMessage(getResources().getString(R.string.download_failed));
                alertfail.setNeutralButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                alertfail.show();
                break;
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
                paused = true;
                showDashboard = false;
                indeterminate = false;
                break;
            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
                showDashboard = false;
                paused = true;
                indeterminate = false;
                showCellMessage = true;
                break;

            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
                paused = true;
                indeterminate = false;
                break;
            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                paused = true;
                indeterminate = false;
                final android.app.AlertDialog.Builder alertfull = new android.app.AlertDialog.Builder(MainDownloader_Activity.this);
                alertfull.setTitle(getResources().getString(R.string.error));
                alertfull.setMessage(getResources().getString(R.string.state_paused_sdcard_unavailable));
                alertfull.setNeutralButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertfull.show();
                break;
            case IDownloaderClient.STATE_COMPLETED:
                showDashboard = false;
                paused = false;
                indeterminate = false;
                downloadCompleteStartAPP();
                break;
            default:
                paused = true;
                indeterminate = true;
                showDashboard = true;
        }
        int newDashboardVisibility = showDashboard ? View.VISIBLE : View.GONE;
        if (mDownloadViewGroup.getVisibility() != newDashboardVisibility) {
            mDownloadViewGroup.setVisibility(newDashboardVisibility);
            mLinearLayout.setVisibility(newDashboardVisibility);
        }
        mDownloadProgressBar.setIndeterminate(indeterminate);
    }

    /**
     * Sets the state of the various controls based on the progressinfo object
     * sent from the downloader service.
     */
    @Override
    public void onDownloadProgress(DownloadProgressInfo progress) {
        mDownloadProgressBar.setMax((int) (progress.mOverallTotal >> 8));
        mDownloadProgressBar.setProgress((int) (progress.mOverallProgress >> 8));
        mProgressPercentTextView.setText(Long.toString(progress.mOverallProgress * 100 / progress.mOverallTotal) + "%");
    }

    /**
     * Go through each of the Expansion APK files and open each as a zip file.
     * Calculate the CRC for each file and return false if any fail to match.
     *
     * @return true if XAPKZipFile is successful
     */
    void validateXAPKZipFiles() {
        AsyncTask<Object, DownloadProgressInfo, Boolean> validationTask = new AsyncTask<Object, DownloadProgressInfo, Boolean>() {

            @Override
            protected void onPreExecute() {
                mDownloadViewGroup.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                for (XAPKFile xf : xAPKS) {
                    String fileName = Helpers.getExpansionAPKFileName(MainDownloader_Activity.this, xf.mIsMain, xf.mFileVersion);
                    if (!Helpers.doesFileExist(MainDownloader_Activity.this, fileName, xf.mFileSize, false))
                        return false;
                    fileName = Helpers.generateSaveFileName(MainDownloader_Activity.this, fileName);
                    ZipResourceFile zrf;
                    byte[] buf = new byte[1024 * 256];
                    try {
                        zrf = new ZipResourceFile(fileName);
                        ZipResourceFile.ZipEntryRO[] entries = zrf.getAllEntries();
                        /**
                         * First calculate the total compressed length
                         */
                        long totalCompressedLength = 0;
                        for (ZipResourceFile.ZipEntryRO entry : entries) {
                            totalCompressedLength += entry.mCompressedLength;
                        }
                        float averageVerifySpeed = 0;
                        long totalBytesRemaining = totalCompressedLength;
                        long timeRemaining;
                        /**
                         * Then calculate a CRC for every file in the Zip file,
                         * comparing it to what is stored in the Zip directory.
                         * Note that for compressed Zip files we must extract
                         * the contents to do this comparison.
                         */
                        for (ZipResourceFile.ZipEntryRO entry : entries) {
                            if (-1 != entry.mCRC32) {
                                long length = entry.mUncompressedLength;
                                CRC32 crc = new CRC32();
                                DataInputStream dis = null;
                                try {
                                    dis = new DataInputStream(zrf.getInputStream(entry.mFileName));

                                    long startTime = SystemClock.uptimeMillis();
                                    while (length > 0) {
                                        int seek = (int) (length > buf.length ? buf.length : length);
                                        dis.readFully(buf, 0, seek);
                                        crc.update(buf, 0, seek);
                                        length -= seek;
                                        long currentTime = SystemClock.uptimeMillis();
                                        long timePassed = currentTime - startTime;
                                        if (timePassed > 0) {
                                            float currentSpeedSample = (float) seek / (float) timePassed;
                                            if (0 != averageVerifySpeed) {
                                                averageVerifySpeed = SMOOTHING_FACTOR * currentSpeedSample + (1 - SMOOTHING_FACTOR) * averageVerifySpeed;
                                            } else {
                                                averageVerifySpeed = currentSpeedSample;
                                            }
                                            totalBytesRemaining -= seek;
                                            timeRemaining = (long) (totalBytesRemaining / averageVerifySpeed);
                                            this.publishProgress(new DownloadProgressInfo(totalCompressedLength, totalCompressedLength - totalBytesRemaining, timeRemaining, averageVerifySpeed));
                                        }
                                        startTime = currentTime;
                                        if (mCancelValidation)
                                            return true;
                                    }
                                    if (crc.getValue() != entry.mCRC32) {
                                        Log.e(Constants.TAG, "CRC does not match for entry: " + entry.mFileName);
                                        Log.e(Constants.TAG, "In file: " + entry.getZipFileName());
                                        return false;
                                    }
                                } finally {
                                    if (null != dis) {
                                        dis.close();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onProgressUpdate(DownloadProgressInfo... values) {
                onDownloadProgress(values[0]);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    mDownloadViewGroup.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.GONE);
                } else {
                    mDownloadViewGroup.setVisibility(View.VISIBLE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                }
                super.onPostExecute(result);
            }

        };
        validationTask.execute(new Object());
    }

    boolean expansionFilesDelivered() {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(this, xf.mIsMain, xf.mFileVersion);
            if (!Helpers.doesFileExist(this, fileName, xf.mFileSize, false))
                return false;
        }
        return true;
    }

    private void setState(int newState) {
        if (mState != newState) {
            mState = newState;
        }
    }


    @Override
    protected void onDestroy() {
        this.mCancelValidation = true;
        super.onDestroy();
    }
// endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maindownloader_layout);

        mDownloadViewGroup = (RelativeLayout) findViewById(R.id.downloadViewGroup);
        mDownloadProgressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        mProgressPercentTextView = (TextView) findViewById(R.id.downloadProgressPercentTextView);
        mDownloadMenu = (TextView) findViewById(R.id.download_menu_text);
        mDownloadTitle = (TextView) findViewById(R.id.downloadTextView);
        mLinearLayout = (LinearLayout) findViewById(R.id.download_menu_layout);

        final Typeface menu_font = Typeface.createFromAsset(getAssets(), "list_textviews.otf");
        mProgressPercentTextView.setTypeface(menu_font);
        mDownloadMenu.setTypeface(menu_font);
        mDownloadTitle.setTypeface(menu_font);

/**
 * Before we do anything, are the files we expect already here and
 * delivered (presumably by Market) For free titles, this is probably
 * worth doing. (so no Market request is necessary)
 */
        if (!expansionFilesDelivered()) {

            try {
                Intent launchIntent = MainDownloader_Activity.this.getIntent();
                Intent intentToLaunchThisActivityFromNotification = new Intent(MainDownloader_Activity.this, MainDownloader_Activity.this.getClass());
                intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentToLaunchThisActivityFromNotification.setAction(launchIntent.getAction());

                if (launchIntent.getCategories() != null) {
                    for (String category : launchIntent.getCategories()) {
                        intentToLaunchThisActivityFromNotification.addCategory(category);
                    }
                }

                // Build PendingIntent used to open this activity from
                // Notification
                PendingIntent pendingIntent = PendingIntent.getActivity(MainDownloader_Activity.this, 0, intentToLaunchThisActivityFromNotification, PendingIntent.FLAG_UPDATE_CURRENT);
                // Request to start the download
                int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent, ExpansionDownloaderService.class);

                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
                    // The DownloaderService has started downloading the files, show progress
                    mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ExpansionDownloaderService.class);
                    return;
                } // otherwise, download not needed so we fall through to the app
                else {
                    // this is where you start the next activity
                    downloadCompleteStartAPP();

                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Cannot find package!", e);
            }
        } else {
            validateXAPKZipFiles(); // First check if files exist
            downloadCompleteStartAPP();
            //after checking of the file is complete this is where you start the next activity
        }

    }

    private void downloadCompleteStartAPP() {

        SharedPreferences firstAppRunEXPNum = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = firstAppRunEXPNum.edit();
        editor.putInt("EXP_NUM", EXPANSION_NUMBER);
        editor.commit();

        Intent mainActivity = new Intent(getBaseContext(), Activity_Splash.class);
        startActivity(mainActivity);
        finish();
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);

    }
}
