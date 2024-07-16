package com.example.all_printer.SunmiRestaurant;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.all_printer.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import woyou.aidlservice.jiuiv5.*;

public class AidlUtil {
    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
    private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";

    public static IWoyouService woyouService;
    private static AidlUtil mAidlUtil = new AidlUtil();
    private Context context;

    private AidlUtil() {
    }

    public static AidlUtil getInstance() {
        return mAidlUtil;
    }


    public void connectPrinterService(Context context) {
        this.context = context.getApplicationContext();
        Intent intent = new Intent();
        intent.setPackage(SERVICE＿PACKAGE);
        intent.setAction(SERVICE＿ACTION);
        context.getApplicationContext().startService(intent);
        context.getApplicationContext().bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    /**
     * 断开服务
     *
     * @param context context
     */
    public void disconnectPrinterService(Context context) {
        if (woyouService != null) {
            context.getApplicationContext().unbindService(connService);
            woyouService = null;
        }
    }

    public boolean isConnect() {
        return woyouService != null;
    }

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    public ICallback generateCB(final PrinterCallback printerCallback){
        return new ICallback.Stub(){

            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {

            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        };
    }

    /**
     * 设置打印浓度
     */
    private int[] darkness = new int[]{0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0,
            0xffff, 0xfeff, 0xfdff, 0xfcff, 0xfbff, 0xfaff};


    /**
     * 取得打印机系统信息，放在list中
     *
     * @return list
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public List<String> getPrinterInfo() {
        if (woyouService == null) {
            Toast.makeText(context, "The service has been disconnected!",Toast.LENGTH_LONG).show();
            return null;
        }

        List<String> info = new ArrayList<>();
        try {
            info.add(woyouService.getPrinterSerialNo());
            info.add(woyouService.getPrinterModal());
            info.add(woyouService.getPrinterVersion());
            info.add(woyouService.getPrintedLength()+"");
            info.add("");
            //info.add(woyouService.getServiceVersion());
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(SERVICE＿PACKAGE, 0);
                if(packageInfo != null){
                    info.add(packageInfo.versionName);
                    info.add(packageInfo.getLongVersionCode()+"");
                }else{
                    info.add("");info.add("");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 初始化打印机
     */
    public void initPrinter() {
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.printerInit(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印二维码
     */
    public void printQr(String data, int modulesize, int errorlevel) {
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }


        try {
			woyouService.setAlignment(1, null);
            woyouService.printQRCode(data, modulesize, errorlevel, null);
            woyouService.lineWrap(2, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印条形码
     */
    public void printBarCode(String data, int symbology, int height, int width, int textposition) {
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }


        try {
            woyouService.printBarCode(data, symbology, height, width, textposition, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印文字
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine, int align) {
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            /// align 0 left , 1 center , 2 right
            woyouService.setAlignment(align, null);
            if (isBold) {
                woyouService.sendRAWData(ESCUtil.boldOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (isUnderLine) {
                woyouService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
            } else {
                woyouService.sendRAWData(ESCUtil.underlineOff(), null);
            }

            woyouService.printTextWithFont(content, null, size, null);
            // woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /*
    *打印图片
     */
    public void printBitmap(Bitmap bitmap) {
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.setAlignment(1, null);
            woyouService.printBitmap(bitmap, null);
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  打印图片和文字按照指定排列顺序
     */
    public void printBitmap(Bitmap bitmap, int orientation) {
        if (woyouService == null) {
            Toast.makeText(context,"服务已断开！",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.setAlignment(1, null);
            if(orientation == 0){
                woyouService.printBitmap(bitmap, null);
//                woyouService.printText("横向排列\n", null);
//                woyouService.printBitmap(bitmap, null);
//                woyouService.printText("横向排列\n", null);
            }else{
                woyouService.printBitmap(bitmap, null);
//                woyouService.printText("\n纵向排列\n", null);
//                woyouService.printBitmap(bitmap, null);
//                woyouService.printText("\n纵向排列\n", null);
            }
            woyouService.lineWrap(1, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


//    /**
//     * 打印表格
//     */
//    public void printTable(LinkedList<TableItem> list) {
//        if (woyouService == null) {
//            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        try {
//            for (TableItem tableItem : list) {
//                Log.i("kaltin", "printTable: "+tableItem.getText()[0]+tableItem.getText()[1]+tableItem.getText()[2]);
//                woyouService.printColumnsText(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign(), null);
//            }
//            woyouService.lineWrap(3, null);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

    /*
    * 空打三行！
     */
    public void print3Line(){
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void sendRawData(byte[] data) {
        if (woyouService == null) {
            Toast.makeText(context,"The service has been disconnected!",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            woyouService.sendRAWData(data, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //获取当前的打印模式
    public int getPrintMode(){
        if(woyouService == null){
            Toast.makeText(context,"服务已断开！",Toast.LENGTH_LONG).show();
            return -1;
        }

        int res;
        try {
            res =  woyouService.getPrinterMode();
        } catch (RemoteException e) {
            e.printStackTrace();
            res = -1;
        }
        return res;
    }
    public String getSN() {
        String serial = null;
        Class<?> c = null;
        try {
            c = Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method get = null;
        try {
            get = c.getMethod("get", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                serial = (String)get.invoke(c, "ro.sunmi.serial");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return serial;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = Build.getSerial();
            return serial;
        } else {
            //安卓8以下使用Build.SERIAL相同方式
            //return Build.SERIAL;
            try {
                serial = (String) get.invoke(c, "ro.serialno");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return serial;
        }
    }
}
