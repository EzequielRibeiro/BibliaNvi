package com.projeto.biblianvi;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "https://github.com/EzequielRibeiro/link/raw/master/XXX",
            downloadFileName;
    private String packageName;
    private SharedPreferences sharedPref;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    public DownloadTask(Context context, ProgressDialog progressDialog, SharedPreferences sharedPref) {
        packageName = context.getPackageName();
        this.sharedPref = sharedPref;
        this.progressDialog = progressDialog;
        runTask(sharedPref.getString("language", "en"));
    }


    public DownloadTask(Context context, ProgressBar progressBar, SharedPreferences sharedPref) {
        packageName = context.getPackageName();
        this.sharedPref = sharedPref;
        this.progressBar = progressBar;
        runTask(sharedPref.getString("language", "en"));

    }

    private void runTask(String language) {

        switch (language) {
            case "pt":
                downloadUrl = downloadUrl.replace("XXX", Utils.NVI_PT_BR_ZIP);
                downloadFileName = Utils.NVI_PT_BR_ZIP;
                break;
            case "es":
                downloadUrl = downloadUrl.replace("XXX", Utils.SSE_ES_ZIP);
                downloadFileName = Utils.SSE_ES_ZIP;
                break;
            default:
                downloadUrl = downloadUrl.replace("XXX", Utils.NVI_EN_ZIP);
                downloadFileName = Utils.NVI_EN_ZIP;
                break;

        }

        Log.e(TAG, downloadFileName);
        Log.e(TAG, downloadUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new DownloadingTask(progressBar, sharedPref).execute(packageName);
        } else {
            new DownloadingTask(progressDialog, sharedPref).execute(packageName);
        }


    }

    private class DownloadingTask extends AsyncTask<String, Context, Void> {

        File folderStorage = null;
        File outputFile = null;
        String packageName;
        ProgressDialog progressDialog;
        ProgressBar progressBar;
        SharedPreferences sharedPref;

        public DownloadingTask(ProgressBar progressBar, SharedPreferences sharedPref) {
            this.sharedPref = sharedPref;
            this.progressBar = progressBar;

        }

        public DownloadingTask(ProgressDialog progressDialog ,SharedPreferences sharedPref){

            this.sharedPref = sharedPref;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {

            try {
                if (outputFile != null) {
                    Log.e(TAG, "Download Completed and Unzip");
                    Log.e(TAG,"sharedPrefPatch: " + sharedPref.getString("dataBasePatch","invalid"));
                } else {
                    //If download failed change button text
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Change button text again after 3sec
                        }
                    }, 3000);

                    Toast.makeText(context,"failed to download",Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //download again enable button
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... arg0) {

            packageName = arg0[0];
            //Get File if SD card is present
            if (new CheckForSDCard().isSDCardPresent()) {

                folderStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + Utils.DOWNLOAD_FOLDER_NAME);

                //If File is not present create directory
                if (!folderStorage.exists()) {
                    folderStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

            } else {

                folderStorage = new File(
                        Environment.getDataDirectory() + "/"
                                + Utils.DOWNLOAD_FOLDER_NAME);

                //If File is not present create directory
                if (!folderStorage.exists()) {
                    folderStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }
            }

            outputFile = new File(folderStorage, downloadFileName);//Create Output file in Main File

            //Create New File if not present
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "File Created");

            }
            Log.e(TAG, outputFile.getAbsolutePath());

            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {

                OkHttpClient  client = new OkHttpClient().newBuilder()
                        .readTimeout(60, TimeUnit.SECONDS) //set the read timeout
                        .connectTimeout(60, TimeUnit.SECONDS) //set the connect timeout
                        .build();

                Request request = new Request.Builder().url(downloadUrl).
                        build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {

                    Thread thread = new Thread(){
                        public void run(){
                            Looper.prepare();//Call looper.prepare()

                            Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    progressDialog.setMessage("Failed to download file Bible");

                                }
                            };
                            Looper.loop();
                        }
                    };
                    thread.start();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (progressDialog != null) progressDialog.dismiss();
                    Toast.makeText(context, R.string.download_fail, Toast.LENGTH_LONG).show();
                   throw new IOException("Failed to download file: " + response);
                }else{
                    Log.e(TAG, "input:" + response.body().contentLength());
                    Log.e(TAG, "response msg: " + response.message());

                }

                inputStream = response.body().byteStream();

                outputStream = new FileOutputStream(outputFile);
                int totalCount = (int) response.body().contentLength();
                Log.e(TAG, "totalCount: " + totalCount);
                byte[] buffer = new byte[2 * 1024];
                int len;
                int readLen = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                    readLen += len;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setProgress((readLen * 100) / totalCount, true);
                            } else {
                                progressDialog.setProgress((readLen * 100) / totalCount);
                            }
                        }catch (ArithmeticException a){
                            Log.e(TAG,a.getMessage());
                        }
                }


            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputFile != null) {
                Log.e(TAG, "Unzip Started: " + outputFile.getAbsolutePath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    new UnZip(outputFile.getAbsolutePath(), packageName, sharedPref, progressBar);
                } else {
                    new UnZip(outputFile.getAbsolutePath(), packageName, sharedPref, progressDialog);
                }

            }


            return null;
        }
    }


    public static class Utils {

        public static final String downloadZipUrlNviPt = "1q3MB9Hr5pqOGEoG_a6kV-XW6lgvKBfqf";
        public static final String NVI_PT_BR_ZIP = "nvi_pt_br.zip";
        public static final String DATABASE_NAME_PT = "nvi_pt_br.db3";
        public static final String NVI_EN_ZIP = "nvi_en.zip";
        public static final String DATABASE_NAME_EN = "nvi_en.db3";
        public static final String SSE_ES_ZIP = "sse_es.zip";
        public static final String DATABASE_NAME_ES = "sse_es.db3";
        public static final String DOWNLOAD_FOLDER_NAME = "Download";


    }

    public static class CheckForSDCard {
        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        }
    }


}
