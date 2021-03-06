package com.act.videochat.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class FileUtils {


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    private String SDPATH;


    //构造函数，得到SD卡的目录，
    public FileUtils() {
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }


    //判断SD卡上的文件夹是否存在
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);

        return file.exists();
    }

    //将一个InputStream里面的数据写入到SD卡中
    //将input写到path这个目录中的fileName文件上
    public File write2SDFromInput(String fileName, String content) {
        File file = null;
        OutputStream output = null;
        try {
            file = new File(SDPATH + fileName);

            file.createNewFile();

            output = new FileOutputStream(file);

            output.write((content).getBytes());

            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    public String readInfo(String fileName) {

        String text = "";

        File file = new File(SDPATH + fileName);

        //如果文件存在，则读取
        if (file.exists()) {
            try {

                FileInputStream fin = new FileInputStream(file);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(fin));
                text = buffer.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return text;
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public  boolean deleteFile(String fileName) {
        File file = new File(SDPATH + fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
