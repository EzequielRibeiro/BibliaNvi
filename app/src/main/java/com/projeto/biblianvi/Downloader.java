package com.projeto.biblianvi;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.Status;

import java.io.File;

public class Downloader {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "http://biblianvi.byethost17.com/database/XXX",
            downloadFileName;

    public Downloader(Context context){

        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(context.getApplicationContext(), config);

        this.context = context;
        downloadUrl = downloadUrl.replace("XXX", Utils.downloadFileName);
       // downloadUrl = "http://www.appsapk.com/downloading/latest/WeChat-6.5.7.apk";

        requestDownload();

    }

    private boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private void requestDownload(){

        File folderStorage = null;

        //Get File if SD card is present
        if (isSDCardPresent()) {

            folderStorage = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + Utils.downloadFolderName);

            //If File is not present create directory
            if (!folderStorage.exists()) {
                folderStorage.mkdir();
                Log.e(TAG, "Directory Created.");
            }

        } else {

            folderStorage = new File(
                    Environment.getDataDirectory() + "/"
                            + Utils.downloadFolderName);

            //If File is not present create directory
            if (!folderStorage.exists()) {
                folderStorage.mkdir();
                Log.e(TAG, "Directory Created.");
            }
        }


        final int downloadId = PRDownloader.download(downloadUrl, folderStorage.getAbsolutePath(), Utils.downloadFileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.e(TAG, "Bytes: " + (progress.currentBytes * 100) / progress.totalBytes + "%");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.e(TAG,"Download complete");
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e(TAG,"Download error: " + error.getServerErrorMessage());

                    }
                });


        Status status = PRDownloader.getStatus(downloadId);
        Log.e(TAG,"status: "+ status.toString());

    }

    private class Utils {

        public static final String downloadFileName = "nvi_pt_br.zip";
        public static final String downloadFolderName = "Download";

    }





}
