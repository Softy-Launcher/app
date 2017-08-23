package com.Softy.Launcher2.Classes;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.Softy.Launcher2.Data;
import com.Softy.Templates.Creator;
import com.Softy.Templates.Reader;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Created by mcom on 2/21/17.
 */

public class Templates {
    private Context mContext;
    private static String PATH;
    public Templates(Context mContext){
        this.mContext = mContext;
    }

    public Templates read(String path)
    {
        this.PATH = path;
        return this;
    }

    public Templates writeTemplate()
    {
        try {
            new Creator("/sdcard/Softy/Templates/Softy.xml")
                    .init()
                    .setName("Softy")
                    .addExternalNode("Softy.BACK", "#282828")
                    .addExternalNode("Softy.FORE", "#282828")
                    .setSummary("Sample template")
                    .setMinVersion(1)
                    .setTheme(Data.DARK)
                    .build();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static String getTemplateName(String path){
        try {
            Reader mReader = new Reader(path).init();
            return mReader.getName();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSummary(String path){
        try {
            Reader mReader = new Reader(path).init();
            return mReader.getSummary();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getMin(String path){
        try {
            Reader mReader = new Reader(path).init();
            return mReader.getMin();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getTheme(String path){
        try {
            Reader mReader = new Reader(path).init();
            return mReader.getTheme();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Data.DARK;
    }

    public static String getDrawerBackground(String path){
        try {
            Reader mReader = new Reader(path).init();
            return mReader.getBack();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.toString(Color.BLACK);
    }

    public static String getDrawerForeground(String path){
        try {
            Reader mReader = new Reader(path).init();
            return mReader.getFore();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.toString(Color.WHITE);
    }

    public static String getWallpaperPath(String path)
    {
        try {
            ExtendedReader mReader = new ExtendedReader(path).init();
            return mReader.getWallpaperLocation();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "no_wall";
    }

    public static String getWallpaperName(String path)
    {
        try {
            ExtendedReader mReader = new ExtendedReader(path).init();
            return mReader.getWallpaperName();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "no_wall";
    }

    public static String getIconPackPackage(String path)
    {
        try {
            ExtendedReader mReader = new ExtendedReader(path).init();
            return mReader.getIconPackPackage();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "com.natewren.linesfree";
    }
    public void showMessage(String message) { Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();}

    public void download(Context mContext, String url) throws IOException {
    }
    static class ExtendedReader extends Reader
    {
        private static String PATH;
        private static String WALLPAPER_LOC;
        private static String WALLPAPER_NAME;
        private static String ICON_PACK_PACKAGE;
        private File newFile;
        private DocumentBuilderFactory newFactory;
        private DocumentBuilder newBuilder;
        private static Document newDocument;
        public ExtendedReader(String PATH) {
            super(PATH);
            this.PATH = PATH;
        }

        @Override
        public ExtendedReader init() throws ParserConfigurationException, SAXException, IOException
        {
            super.init();
            newFile = new File(PATH);
            newFactory = DocumentBuilderFactory.newInstance();
            newBuilder = newFactory.newDocumentBuilder();
            newDocument = newBuilder.parse(newFile);
            try {
                WALLPAPER_LOC = newDocument.getElementsByTagName("Softy.WALLPAPER_PATH").item(0).getTextContent();
                WALLPAPER_NAME = newDocument.getElementsByTagName("Softy.WALLPAPER_NAME").item(0).getTextContent();
                ICON_PACK_PACKAGE = newDocument.getElementsByTagName("Softy.ICON_PACK").item(0).getTextContent();
            }catch(NullPointerException mNull)
            {
                WALLPAPER_LOC = "no-wall";
                WALLPAPER_NAME = "no-wall";
                ICON_PACK_PACKAGE = "com.natewren.linesfree";
            }
            return this;
        }

        public static String getWallpaperLocation()
        {
            return WALLPAPER_LOC;
        }

        public static String getWallpaperName()
        {
            return WALLPAPER_NAME;
        }

        public static String getIconPackPackage() { return ICON_PACK_PACKAGE; }
    }
}