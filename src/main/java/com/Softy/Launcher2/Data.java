package com.Softy.Launcher2;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;


import com.android.launcher3.DropTarget;
import com.android.launcher3.ShortcutInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by mcom on 1/22/17.
 */

public class Data {

    public static String PRO_NAME = "pro-features";
    public static String ADS_NAME = "ads-prefs";

    public static String HAS_PAID = "has-user-paid";
    public static String FIRST_TIME_PAID = "first-time-payer";

    public static String PRO_FEATURES = "pro-features";


    public static String BASE_64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiPRzej7x8dPMVmsfPQL44Bv83+FHzs/jDhmjeuBHC9T97RhWhlVP9oJHM7ohNeVul19TA6AfCwZxfECbGI2Zbl4e94ncJev8OKzw66G39V3AUGKnKGqUJiCD9FJ2tzBVvCgS81j5mqYwlBmASce2sPCIldOHxLVastorD1g/GHTZGtELvElooAG3+W0YEWi+cGXrJeXr77did8E96eIjQA16SwXez1TAFhbWtncHZvWeRuMU5BKDNi4jzAqBA37MbU+UPrcKaY1mpj2rMp9yPy5lkOtOs6LJzfhzjMj+j61abZ0C5q/XXJIt0B5ctwSZkK3mse5MWrSq2CFumXO+RwIDAQAB";
    public static String NAME = "data";
    final static String sd = "/sdcard";

    public static String BACKUP_PATH = sd + "/Softy/backup";
    public static String USER_NAME = "user_name";
    public static String PASS_WORD = "pass_word";
    public static String TEMP_PASS = "temp_pass";

    public static String DRAWER_TEXT_COLOR = "drawer.text_color";

    public static String TEMP_THEME = "temp_theme";

    public static final String DARK = "dark";
    public static final String LIGHT = "light";

    public static int LAUNCH_PICKER_CODE = 1900;
    public static String SWIPE_DOWN_PACKAGE_NAME = "swipeDownPackageName";
    public static String SWIPE_UP_PACKAGE_NAME = "swipeUpPackageName";
    public static String hh;
    public static String INSTALLER = "extras";

    public static boolean isAppInstalled(String appPackage, PackageManager packageManager) { return isPackageInstalled(appPackage, packageManager); }
    private static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static class Drawer
    {
        public static String DRAWER_BACKGROUND = "drawer_background";
        public static String DRAWER_FOREGROUND = "drawer_foreground";
    }

    public static class Dock
    {
        public static String DOCK_ON = "dock.visible";
    }

    public static class Qsb
    {
        public static String QSB_ON = "qsb.visible";
    }

    static void move(String inputPath, String outputPath){
        try {
            File f1 = new File(inputPath);
            File f2 = new File(outputPath);
            InputStream in = new FileInputStream(f1);

            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void delete(String path, String file){
        try{
            //delete
            new File(path + file).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static void copy(String inputPath, String inputFile, String outputPath){
        InputStream is = null;
        OutputStream os = null;
        try{
            File out = new File(outputPath);
            if(!out.exists()){
                out.mkdirs();
            }

            is = new FileInputStream(inputPath +"/"+ inputFile);
            os = new FileOutputStream(outputPath +"/"+inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while((read = is.read(buffer)) != -1){
                os.write(buffer, 0 ,read);
            }
            is.close();
            is = null;

            os.flush();
            os.close();

            os = null;

        }catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    static long download(Context c, Uri uri, String type, String path, String fileName){
        long reference;

        DownloadManager dm = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request dmr = new DownloadManager.Request(uri);

        dmr.setTitle("Downloading "+type);
        dmr.setDescription(path);

        if((new File(path)).exists()){
            new File(path).mkdir();
        }

        dmr.setDestinationInExternalPublicDir(path,"");

        reference = (long) dm.enqueue(dmr);

        return reference;
    }

    static void delete(String path){
        File dir = new File(path);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    public static void zip(String[] files, String outputZip) {
        try{
            int buff = 60;
            BufferedInputStream input = null;
            FileOutputStream out = new FileOutputStream(outputZip);
            ZipOutputStream to = new ZipOutputStream(new BufferedOutputStream(out));
            byte[] b= new byte[buff];
            for(int i = 0; i < files.length; i++){
                FileInputStream fi = new FileInputStream(files[i]);
                input = new BufferedInputStream(fi, buff);

                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                to.putNextEntry(entry);
                int c = 0;

                while((c = input.read(b, 0, c)) != -1){
                    out.write(b, 0 ,c);
                }
                input.close();

            }
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void unzip(String zip, String target){
        checkDir(target);
        try{
            FileInputStream mIn = new FileInputStream(zip);
            ZipInputStream mZip = new ZipInputStream(mIn);
            ZipEntry mEntry = null;
            while((mEntry = mZip.getNextEntry()) != null){
                if(mEntry.isDirectory()){

                }else{
                    FileOutputStream fout = new FileOutputStream(target + mEntry.getName());
                    for (int c = mZip.read(); c != -1; c = mZip.read()) {
                        fout.write(c);
                    }

                    mZip.closeEntry();
                    fout.close();
                }
            }
            mZip.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean checkDir(String target) {
        if(new File(target).exists()){
            return true;
        }
        else
            return false;
    }

    public static void moveFolder(String input, String output) {
        File main = new File(input);
        File[] list = main.listFiles();
        for(File f : list){
            if(f.isFile()){
                move(input, output);
            }else{
                moveFolder(f.getPath(), output+f.getName());
            }
        }
    }

    public static class AppsClass {
        private static Context mContext;
        private  List<ApplicationInfo> mList;
        public static void setContext(Context c){
            mContext = c;
        }


        public static void showAndRemove(DropTarget.DragObject d) {
            ShortcutInfo info = (ShortcutInfo) d.dragInfo;
            Intent i = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:"+info.getTargetComponent().getPackageName()));
            mContext.startActivity(i);
        }


        public String getApplicationTitle(DropTarget.DragObject d) {
            ApplicationInfo ai = null;
            try{
                ai = mContext.getPackageManager().getApplicationInfo(((ShortcutInfo)d.dragInfo).getTargetComponent().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return (String) (ai != null ? mContext.getPackageManager().getApplicationLabel(ai) : "unknown");
        }
    }


    private void playWithCode(){
        //test any code here
    }

    private static Activity mActivity;
    public static String set(Activity mActivity) {
        Data.mActivity = mActivity;
        return "Set the context";
    }
    public static String initFileManager(String fileType, String message){
        String result = "Have not initialized File Manager";
        if(!(mActivity == null)){
            // init manager
            Intent fm = new Intent(Intent.ACTION_GET_CONTENT);
            fm.setType(fileType);
            fm.addCategory(Intent.CATEGORY_OPENABLE);

            mActivity.startActivityForResult(Intent.createChooser(fm, message),0);
        }else{
            result = "mActivity not init. Use Data.set to initialize it";
        }
        return result;
    }

    public static class Zipper{

        public boolean zipFileAtPath(String sourcePath, String toLocation) {
            final int BUFFER = 2048;

            File sourceFile = new File(sourcePath);
            try {
                BufferedInputStream origin = null;
                FileOutputStream dest = new FileOutputStream(toLocation);
                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                        dest));
                if (sourceFile.isDirectory()) {
                    zipSubFolder(out, sourceFile, sourceFile.getParent().length());
                } else {
                    byte data[] = new byte[BUFFER];
                    FileInputStream fi = new FileInputStream(sourcePath);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

/*
 *
 * Zips a subfolder
 *
 */

        private void zipSubFolder(ZipOutputStream out, File folder,
                                  int basePathLength) throws IOException {

            final int BUFFER = 2048;

            File[] fileList = folder.listFiles();
            BufferedInputStream origin = null;
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePathLength);
                } else {
                    byte data[] = new byte[BUFFER];
                    String unmodifiedFilePath = file.getPath();
                    String relativePath = unmodifiedFilePath
                            .substring(basePathLength);
                    FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(relativePath);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
        }

        /*
         * gets the last path component
         *
         * Example: getLastPathComponent("downloads/example/fileToZip");
         * Result: "fileToZip"
         */
        public String getLastPathComponent(String filePath) {
            String[] segments = filePath.split("/");
            if (segments.length == 0)
                return "";
            String lastPathComponent = segments[segments.length - 1];
            return lastPathComponent;
        }
    }

    public static class Settings{
        public String OK_GOOGLE = "okay_google";
        public String SWIPE_UP = "swipe_up_action";
        public String SWIPE_DOWN = "swipe_down_action";
        public String DOUBLE_TAP = "double_tap_action";
        public String TRIPLE_TAP = "tripe_tap_action";
    }


    public static class GestureDialogData{
        public static String OPEN_STATUS = "open_status_bar";
        public static String CLOSE_STATUS = "close_status_bar";
        public static String LAUNCH_APP = "launch_app";
    }


    public static int generateSSE(int value){
        //convert to float
        float in = (float) value;

        //generate "secure" code
        float code = (float) (((in * 55) + (in / 22)) / 3.5);
        value = (int) code;

        return (int) code;
    }

    public static int degenerateSSE(int value){
        //convert to float
        float in = (float) value;

        //degenerate "secure" code
        float code = (float)(((in / 55) - (in * 22)) * 3.5);
        value = (int) code;

        return (int) code;
    }

    public static class GoodHub
    {
        public static String USER = "user.name";
        public static String EMAIL = "user.email";
        public static String PASS = "user.pass";
    }

    public static class Environment extends android.os.Environment
    {
        public static String DIRECTORY_WALLPAPERS = DIRECTORY_PICTURES;
    }

    public static class Folder {
        public static String FOLDER_BACKGROUND = "folder_back";
        public static String FOLDER_HINT = "folder_hint";
        public static String MAX_APPS = "max_folder_items";
    }

    //Use Broddy to securely backup data


    public static void exportPreferences()
    {
        copy("/data/data/com.Softy.Launcher.shared_prefs", Data.NAME+".xml", "/sdcard/Softy/backups");
    }

    public static void importPreferences()
    {
        copy("/sdcard/Softy/backups","decoded_data.xml","/data/data/com.Softy.Launcher.shared_prefs");
        new File("/sdcard/Softy/backups/decoded_data.xml").delete();
    }

}