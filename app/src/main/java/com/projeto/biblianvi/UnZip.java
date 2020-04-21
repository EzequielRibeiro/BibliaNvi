package com.projeto.biblianvi;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {

    private String packageName;
    private String  TAG = "UnZip";
    private SharedPreferences.Editor editor;

    public UnZip(String zipFilePath, String packageName, SharedPreferences sharedPref, ProgressDialog progressDialog) {

        editor = sharedPref.edit();
        this.packageName = packageName;

        File dir = new File(getFileDestDirectory());
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(getFileDestDirectory() + File.separator + fileName);
                Log.e(TAG,"Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len, lenCurrent = 0;
                long totalSize = ze.getSize();
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    lenCurrent += len;
                    progressDialog.setProgress((lenCurrent * 100) /(int) totalSize);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
                progressDialog.dismiss();
                editor.putString("dataBasePatch", newFile.getAbsolutePath());
                editor.commit();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getFileDestDirectory(){

        File folderStorage;
        String folderDest = "Android/data/"+packageName+"/databases";

        //Get File if SD card is present
        if (new DownloadTask.CheckForSDCard().isSDCardPresent()) {

            folderStorage = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + folderDest);

            //If File is not present create directory
            if (!folderStorage.exists()) {
                folderStorage.mkdir();
            }

        } else {

            folderStorage = new File(
                    Environment.getDataDirectory() + "/"
                            + folderDest);

            //If File is not present create directory
            if (!folderStorage.exists()) {
                folderStorage.mkdir();

            }
        }

        Log.e(TAG,"Directory Created: "+ folderStorage.getAbsolutePath());
        return folderStorage.getAbsolutePath();




    }
}
