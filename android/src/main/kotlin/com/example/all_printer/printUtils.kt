package com.example.all_printer


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.os.RemoteException
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.example.all_printer.SunmiRestaurant.AidlUtil
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.imin.library.IminSDKManager
import com.imin.library.SystemPropManager
import com.imin.printerlib.IminPrintUtils
import com.mobiiot.androidqapi.api.CsDevice
import com.mobiiot.androidqapi.api.CsPrinter
import com.mobiiot.androidqapi.api.Utils.AndroidBmpUtil
import com.mobiiot.androidqapi.api.Utils.PrinterServiceUtil
import com.mobiiot.androidqapi.api.Utils.ServiceUtil
import com.nbbse.mobiprint3.Printer
import com.sagereal.printer.PrinterInterface
import com.sunmi.printerx.enums.Align
import com.sunmi.printerx.enums.ErrorLevel
import com.sunmi.printerx.enums.ImageAlgorithm
import com.sunmi.printerx.style.BaseStyle
import com.sunmi.printerx.style.BitmapStyle
import com.sunmi.printerx.style.QrStyle
import com.sunmi.printerx.style.TextStyle
import com.sunmi.printerx.utils.SystemPropertyUtil
import com.sunmi.printerx.utils.SystemPropertyUtil.*
import com.sunmi.samples.printerx.ui.vm.PrinterViewModel
import com.sunmi.samples.printerx.ui.vm.selectPrinter
import wangpos.sdk4.libbasebinder.c
import java.io.*
import java.net.URL
import java.nio.channels.Channels
import java.util.*


class PrintingMethods {

    private val printerX: PrinterViewModel = PrinterViewModel()

    companion object {
        @SuppressLint("StaticFieldLeak")
        var LoginActivity: Context? = null
        var sizeValue = 0
        var fontValue: kotlin.Int = 0
        var directionValue: kotlin.Int = 0
        var alignmentValue: kotlin.Int = 0

        var mIminPrintUtils: IminPrintUtils? = null


        var print: Printer? = null

        @SuppressLint("StaticFieldLeak")
        private var mPrinter: wangpos.sdk4.libbasebinder.Printer? = null
        private val bloop = false
        private var bthreadrunning = false
        private val printInterfaceService: PrinterInterface? = null


    }

    constructor(context: Context) {
        LoginActivity = context

        Constant.posType = getDeviceName()
    }

    fun initializePrinters() {

        Log.d("PosType", Constant.posType)
        Log.d("ALL PRINTER CHANGELOGS", "0.0.1")
        when (Constant.posType) {
            "MobiPrint" -> {
                print = Printer.getInstance()
            }

            "WISENET5" -> {
                object : Thread() {
                    override fun run() {
                        try {
                            mPrinter =
                                wangpos.sdk4.libbasebinder.Printer(LoginActivity)
                            mPrinter!!.setPrintFontType(
                                LoginActivity,
                                ""
                            ) //fonnts/PraduhhTheGreat.ttf
                        } catch (e: Exception) {
                            Log.e("Exception Found", "Abbey")
                        }
                    }
                }.start()

                if (!bthreadrunning) {
                    PrintThread().start()
                }
            }

            "MP3_Plus",
            "MobiPrint 4+",
            "MP4",
            "MobiPrint4_Plus",
            "Mobiwire MP4",
            "k80hd_bsp_fwv_512m"
            -> {

                try {
                    if (LoginActivity != null) {
                        PrinterServiceUtil.bindService(LoginActivity)
                        ServiceUtil.bindRemoteService(LoginActivity)
                        Log.d("success", "Printer Service Util Started Successfully")
                    } else {
                        Log.e("PrinterNotBound", LoginActivity.toString() + "")
                    }

//                LoginActivity.bindService(getPrintIntent(), serviceConnection, Service.BIND_AUTO_CREATE);
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey Muzamil MP3_Plus", ex.toString() + "")
                }
            }

            "T2mini",
            "T2mini_s",
            "T1mini-G",
            "D2mini", "T2s", "K2_PRO", "K2_MINI" -> {
                try {
                    // should add context here
                    AidlUtil.getInstance().connectPrinterService(LoginActivity)
                    AidlUtil.getInstance().initPrinter()
                } catch (ex: java.lang.Exception) {
                    Log.e("  Exception  ", ex.toString() + "")
                }
            }

            "V3_MIX_EDLA_GL" -> {
                try {
                    LoginActivity?.let { printerX.initPrinter(it) }
                } catch (ex: java.lang.Exception) {
                    Log.e("  Exception  ", ex.toString() + "")
                }
            }

            "D4-505",
            "D4",
            "D1",
            "M2-Max",
            "Swift 1",
            "S1",
            "D1-Pro" -> {
                Log.e("mIminPrintUtils", "detict")
                try {
                    if (mIminPrintUtils == null) {
                        mIminPrintUtils =
                            IminPrintUtils.getInstance(LoginActivity)
                    }
                    mIminPrintUtils?.initPrinter(
                        IminPrintUtils.PrintConnectType.USB
                    )
                    var status: Int =
                        mIminPrintUtils?.getPrinterStatus(
                            IminPrintUtils.PrintConnectType.USB
                        ) ?:
                        //针对S1， //0：打印机正常 1：打印机未连接或未上电 3：打印头打开 7：纸尽  8：纸将尽  99：其它错误
                        Log.d("XGH", " print USB status: ")
                    checkIminPrinter()
                } catch (exception: Exception) {
                    Log.e("mIminPrintUtils", exception.message + "")
                }

            }

            "M2-Pro" -> {
                Log.e("XGHXGH mIminPrintUtils", "detict")
                try {
                    if (mIminPrintUtils == null) {
                        mIminPrintUtils =
                            IminPrintUtils.getInstance(LoginActivity)
                    }
                    mIminPrintUtils?.initPrinter(
                        IminPrintUtils.PrintConnectType.SPI
                    )
                    Log.d("XGH", " print SPI status: success")
                } catch (exception: Exception) {
                    Log.e("mIminPrintUtils", exception.message + "")
                }
            }

            else -> print("I don't know anything about it")
        }
    }


    class PrintThread : Thread() {
        override fun run() {
            try {
                mPrinter = wangpos.sdk4.libbasebinder.Printer(LoginActivity)
                mPrinter!!.setPrintFontType(LoginActivity, "") //fonnts/PraduhhTheGreat.ttf
            } catch (e: java.lang.Exception) {
                Log.e("Exception Found", "Abbey")
            }
            bthreadrunning = true
            do {
                try {
                    mPrinter!!.printInit()
                    mPrinter!!.clearPrintDataCache()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            } while (bloop)
            bthreadrunning = false
        }
    }

    fun getDevicePos(): String {
        try {
            when (Constant.posType) {
                "MobiPrint",
                "WISENET5",
                "MP3_Plus",
                "MobiPrint 4+",
                "MP4",
                "MobiPrint4_Plus",
                "Mobiwire MP4",
                "k80hd_bsp_fwv_512m"
                -> {
                    val pos = CsDevice.getDeviceInformation()
                    Log.e("POS", pos.serial_number);
                    return pos.sim1_imei
                }

                "T2mini",
                "T2mini_s",
                "T1mini-G",
                "D2mini", "T2s", "K2_PRO", "K2_MINI" -> {
                    val pos = AidlUtil.getInstance().sn
                    Log.e("POS", pos);
                    return pos.replace(Regex("[^0-9]"), "")
                }
                "V3_MIX_EDLA_GL" -> {
                    try {
                        val pos = get("ro.sunmi.serial") as String
                        return pos.replace(Regex("[^0-9]"), "")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                "D4-505",
                "D4",
                "D1",
                "M2-Max",
                "Swift 1",
                "S1",
                "D1-Pro",
                "M2-Pro" -> {
                    val pos = SystemPropManager.getSn()
                    Log.e("POS", pos);
                    return pos.replace(Regex("[^0-9]"), "")
                }

                else -> return ""
            }
        } catch (e: Exception) {
            Log.e("POS", e.message ?: "SOME ERROR HAPPENED");
        }
        return "NO SERIAL OR SIM IMEI"
    }

    @Throws(RemoteException::class)
    fun printSingleLine(line: String) {
        if (line.length > 31 && !line.startsWith("--------------------------------")) {
            var i = 0
            while (i <= line.length) {
                val start = i
                var end = i + 31
                end = Math.min(end, line.length)
                val temp_line = line.substring(start, end)
                if (Constant.posType.equals("MP3_Plus") || Constant.posType.equals("MP4") || Constant.posType.equals(
                        "Mobiwire MP4"
                    ) || Constant.posType.equals("MobiPrint4_Plus") || Constant.posType.equals("k80hd_bsp_fwv_512m")
                ) {
//                    if(isProbablyArabic(temp_line))
                    if (Constant.isArabicPrintAllowed) CsPrinter.printText_FullParm(
                        temp_line,
                        0,
                        1,
                        2,
                        0,
                        false,
                        false
                    ) else CsPrinter.printText_FullParm(temp_line, 0, 0, 2, 0, false, false)
                    //                    CsPrinter.printText(temp_line);
                } else {
                    if (Constant.isArabicPrintAllowed) mPrinter!!.printString(
                        temp_line,
                        25,
                        wangpos.sdk4.libbasebinder.Printer.Align.RIGHT,
                        false,
                        false
                    ) else mPrinter!!.printString(
                        temp_line,
                        25,
                        wangpos.sdk4.libbasebinder.Printer.Align.LEFT,
                        false,
                        false
                    )
                }
                i += 31
            }
        } else {
            if (Constant.posType.equals("MP3_Plus") || Constant.posType.equals("MP4") || Constant.posType.equals(
                    "Mobiwire MP4"
                ) || Constant.posType.equals("MobiPrint4_Plus") || Constant.posType.equals("k80hd_bsp_fwv_512m")
            ) {
//                if(isProbablyArabic(line))
                if (Constant.isArabicPrintAllowed) CsPrinter.printText_FullParm(
                    line,
                    0,
                    1,
                    2,
                    0,
                    false,
                    false
                ) else CsPrinter.printText_FullParm(line, 0, 0, 2, 0, false, false)
                //                CsPrinter.printText(line);
            } else {
                if (Constant.isArabicPrintAllowed) mPrinter!!.printString(
                    line,
                    25,
                    wangpos.sdk4.libbasebinder.Printer.Align.RIGHT,
                    false,
                    false
                ) else mPrinter!!.printString(
                    line,
                    25,
                    wangpos.sdk4.libbasebinder.Printer.Align.LEFT,
                    false,
                    false
                )
            }
        }
    }


    fun isProbablyArabic(s: String): Boolean {
        var i = 0
        while (i < s.length) {
            val c = s.codePointAt(i)
            if (c >= 0x0600 && c <= 0x06E0) return true
            i += Character.charCount(c)
        }
        return false
    }

    fun preprocessLines(line: String): ArrayList<String> {
        val lines = ArrayList<String>()
        var start = 0
        while (line.substring(start).contains("\n")) {
            var end = start + line.substring(start).indexOf("\n") + 1
            end = Math.min(end, line.length)
            lines.add(line.substring(start, end))
            start = end
        }
        if (start < line.length) lines.add(line.substring(start))
        return lines
    }

    @Throws(RemoteException::class)
    fun printLine(line: String?) {
        if (Constant.posType.equals("WISENET5")) for (subline in preprocessLines(
            line!!
        )) printSingleLine(subline)
    }

    private fun checkSize(
        arrayList: ArrayList<*>,
        temp: java.lang.StringBuilder
    ): StringBuilder {
        if (arrayList.size > 100) {
            temp.setLength(0)
            for (i in 0..99) {
                temp.append(
                    """
                    ${arrayList[i]}
                    
                    """.trimIndent()
                )
                arrayList.removeAt(i)
            }
        } else for (i in arrayList.indices) temp.append(
            """
            ${arrayList[i]}
            
            """.trimIndent()
        )
        return temp
    }

    fun checkIminPrinter() {
        if (!Build.MANUFACTURER.equals("neostra")) {
            return
        }
        Log.e("MANUFACTURER", Build.MANUFACTURER)
        Log.e("BOARD", Build.BOARD)
        Log.e("BRAND", Build.BRAND)

        if (mIminPrintUtils!!.printPower) {
            Thread {
                try {
                    mIminPrintUtils!!.openPower()
                    Log.e(
                        "IMINY",
                        mIminPrintUtils!!.getPrinterStatus(IminPrintUtils.PrintConnectType.USB)
                            .toString()
                    )
                } catch (e: java.lang.Exception) {
                }
            }.start()
        }
//        if (mIminPrintUtils!!.getPrinterStatus(IminPrintUtils.PrintConnectType.USB) === -1) {
//            Thread {
//                try {
//                    mIminPrintUtils!!.resetDevice()
//                    mIminPrintUtils!!.initPrinter(IminPrintUtils.PrintConnectType.USB)
//                    Log.e("IMINY", mIminPrintUtils!!.getPrinterStatus(IminPrintUtils.PrintConnectType.USB).toString())
//                } catch (e: java.lang.Exception) {
//                }
//            }.start()
//        }
    }

    fun printRey(string: String, size: Int, textAlign: Int, textDirection: Int) {
        try {
            checkIminPrinter()
            Log.e("printReyprintRey", "$string size:$size")
            Log.e("Constant.posType", Constant.posType.toString() + " ")
            when (Constant.posType) {
                "MobiPrint" -> print!!.printText(string, size, Constant.isArabicPrintAllowed)
                "WISENET5" -> try {
                    if (size == 2) {
                        if (Constant.isArabicPrintAllowed) mPrinter!!.printString(
                            string,
                            35,
                            wangpos.sdk4.libbasebinder.Printer.Align.RIGHT,
                            true,
                            false
                        ) else mPrinter!!.printString(
                            string,
                            35,
                            wangpos.sdk4.libbasebinder.Printer.Align.LEFT,
                            true,
                            false
                        )
                    } else printLine(string)
                } catch (ex: java.lang.Exception) {
                    Log.e("1 Exception WiseNet", ex.toString() + "")
                }

                "MP3_Plus", "MobiPrint 4+", "MobiPrint4_Plus", "MP4", "Mobiwire MP4", "k80hd_bsp_fwv_512m" -> try {
                    if (Constant.isArabicPrintAllowed || isProbablyArabic(string))
                        CsPrinter.printText_FullParm(
                            string,
                            size - 1,
                            1,
                            1,
                            textAlign,
                            false,
                            false
                        ) else CsPrinter.printText_FullParm(
                        string,
                        size - 1,
                        0,
                        2,
                        textAlign,
                        false,
                        false
                    )

//                        Log.e("printReyprintRey 1", CsPrinter.getLastError() + " ");
//                        Log.e("printReyprintRey 2", CsPrinter.getPrinterStatus() + " ");
//                        Log.e("printReyprintRey 3", CsPrinter.getCurrentVoltageStatus() + " ");
//                        Log.e("printReyprintRey 4", CsPrinter.getPaperStatus() + " ");
//                        Log.e("printReyprintRey 5", CsPrinter.getPowerState() + " ");
//                        Log.e("printReyprintRey 6", CsPrinter.getTempStatus() + " ");
//                        Log.e("printReyprintRey 7", CsPrinter.printGetPrintedLength() + " ");
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey Exception MP3_Plus", ex.toString() + "")
                }

                "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI" -> try {
                    val alignment = if (textAlign == 0) {
                        (if (Constant.isArabicPrintAllowed) 2 else 0)
                    } else textAlign
                    if (size == 1) AidlUtil.getInstance()
                        .printText(string, 24F, false, false, alignment) else {
                        AidlUtil.getInstance()
                            .printText(string, 36F, true, false, alignment)
                    }
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey Exception Mobiwire", ex.toString() + "")
                }

                "V3_MIX_EDLA_GL" -> try {
                    val alignment = if (textAlign == 0) {
                        (if (Constant.isArabicPrintAllowed) Align.RIGHT else Align.LEFT)
                    } else if (textAlign == 1) {
                        Align.CENTER
                    } else {
                        Align.RIGHT
                    }
                    Log.e("V3_MIX_EDLA_GL Printing", "size $size")
                    if (size == 1) selectPrinter?.lineApi()?.run {
                        initLine(BaseStyle.getStyle())
                        printText(
                            string,
                            TextStyle.getStyle()
                                .setAlign(alignment).enableBold(false)
                        )
                        addText("\n", TextStyle.getStyle())
                        autoOut()
                    } else {
                        selectPrinter?.lineApi()?.run {
                            initLine(BaseStyle.getStyle())
                            printText(
                                string,
                                TextStyle.getStyle().setTextSize(34).enableBold(true)
                                    .setAlign(alignment)
                            )
                            //addText("\n", TextStyle.getStyle())
                            autoOut()
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey ExceptionNewSunmiGe", ex.toString() + "")
                }

                "D4-505", "D4", "D1", "M2-Max", "Swift 1", "S1", "M2-Pro", "D1-Pro" -> if (size == 2) {
                    Log.e("Printtt", "$size string:$string")
                    mIminPrintUtils!!.setAlignment(textAlign)
                    mIminPrintUtils?.setTextSize(26)
                    mIminPrintUtils?.setTextStyle(Typeface.BOLD)
                    mIminPrintUtils?.printText(
                        """
                        $string
                        
                        """.trimIndent()
                    )
                } else {
                    Log.e("Printtt", size.toString() + "")
                    mIminPrintUtils!!.setAlignment(textAlign)
                    mIminPrintUtils?.setTextSize(22)
                    mIminPrintUtils?.setTextStyle(Typeface.NORMAL)
                    mIminPrintUtils?.printText(
                        """
                        $string
                        
                        """.trimIndent()
                    )

                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("9 Exception Printtt", ex.toString() + "")
        }
    }

    fun printQrCode(bitmap: Bitmap?, string: String?) {
        try {
            checkIminPrinter()
            Log.e("Constant.posType", Constant.posType.toString() + " QRPint")
            when (Constant.posType) {
                "MobiPrint" -> {
                    Log.e("MobiPrint", Constant.posType.toString() + " QRPint")

                    // convert qr-link invoice to .bmp image file.
                    // read .bmp image file and print it by print class.
                    AndroidBmpUtil.save(
                        CsPrinter.createBarQrCode(string, BarcodeFormat.QR_CODE, 320, 380),
                        LoginActivity?.getExternalFilesDir(
                            Environment.DIRECTORY_DOWNLOADS
                        )?.getPath()
                            .toString() + "/unzipFolder/files/1/qr.bmp"
                    )
                    print?.printBitmap(
                        LoginActivity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                            ?.getPath()
                            .toString() + "/unzipFolder/files/1/qr.bmp"
                    )
                }

                "MP3_Plus", "MobiPrint 4+", "MP4", "MobiPrint4_Plus", "Mobiwire MP4", "k80hd_bsp_fwv_512m" -> try {
                    try {
                        CsPrinter.printBitmap(
                            CsPrinter.createBarQrCode(
                                string,
                                BarcodeFormat.QR_CODE,
                                384,
                                384
                            )
                        )
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                    CsPrinter.printEndLine()
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey Exception MP3_Plus", ex.toString() + "")
                }

                "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI" -> try {
                    if (bitmap != null) {
                        AidlUtil.getInstance().printBitmap(bitmap)
                        //                            AidlUtil.getInstance().printText("\n", 36, true, false, false);
//                            cutPaper();
                        Log.e("Qrcode", "Sucssess 01")
                    }
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey Exception Sunmi", ex.toString() + "")
                }

                "V3_MIX_EDLA_GL" -> try {
                    selectPrinter?.lineApi()?.run {
                        printQrCode(
                            string!!, QrStyle.getStyle().setAlign(Align.CENTER)
                                .setDot(9).setErrorLevel(ErrorLevel.L)
                        )
                        initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
                        //printText(string, TextStyle.getStyle().enableBold(true))
                        autoOut()
                    }
                } catch (ex: java.lang.Exception) {
                    Log.e("Rey Exception Sunmi", ex.toString() + "")
                }

                "D4-505", "D4", "D1", "M2-Max", "Swift 1", "S1", "M2-Pro", "D1-Pro" -> {
                    mIminPrintUtils!!.printQrCode(string, 1)
                    mIminPrintUtils!!.printAndFeedPaper(100)
                }
            }

        } catch (ex: java.lang.Exception) {
            Log.e("9 Exception Printtt", ex.toString() + "")
        }
    }

    fun printReyFinish() {
        Log.d("printReyFinish", "called")
        checkIminPrinter()
        when (Constant.posType) {
            "MobiPrint" -> {
                print!!.printText("\n\n")
                Log.d("printReyFinish", "hena")
            }

            "WISENET5" -> try {
                mPrinter!!.printPaper(50)
                mPrinter!!.printFinish()
            } catch (ex: java.lang.Exception) {
                Log.e("1 Exception WiseNet", ex.toString() + "")
            }

            "MP3_Plus", "MobiPrint 4+", "MobiPrint4_Plus", "MP4", "Mobiwire MP4", "k80hd_bsp_fwv_512m" -> try {
                CsPrinter.printText("\n\n\n\n")
                CsPrinter.printEndLine()
            } catch (ex: java.lang.Exception) {
                Log.e("Rey Exception MP3_Plus", ex.toString() + "")
            }

            "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI" -> try {
                AidlUtil.getInstance().printText("\n\n\n", 36F, true, false, 0)
                cutPaper()
            } catch (ex: java.lang.Exception) {
                Log.e("Rey Exception MP3_Plus", ex.toString() + "")
            }

            "V3_MIX_EDLA_GL" -> try {
                selectPrinter?.lineApi()?.run {
                    initLine(BaseStyle.getStyle())
                    printText(
                        "\n\n\n",
                        TextStyle.getStyle()
                    )
                    autoOut()
                }
            } catch (ex: java.lang.Exception) {
                Log.e("ReyV3_MIX_EDLA_GL", ex.toString() + "")
            }

            "D4-505", "D4", "D1", "M2-Max", "Swift 1", "S1", "M2-Pro", "D1-Pro" -> {


                //Log.d("imin int:",""+IminPrintUtils.getInstance(Login.mContext).getPrinterStatus(IminPrintUtils.PrintConnectType.USB));
                mIminPrintUtils!!.printAndLineFeed()
                mIminPrintUtils!!.printAndFeedPaper(50)
                mIminPrintUtils!!.partialCut()
            }
        }
    }


    private fun cutPaper() {
        if (Constant.posType == "T2mini" || Constant.posType == "T1mini-G" || Constant.posType == "T2mini_s" || Constant.posType == "D2mini" || Constant.posType == "T2s" || Constant.posType == "K2_PRO" || Constant.posType == "K2_MINI"
        ) {
            try {
                AidlUtil.woyouService.cutPaper(null)
            } catch (ex: java.lang.Exception) {
                Log.e("Drawer Exception", ex.toString() + "")
            }
        }
    }

    fun openDrawer() {
        try {
            Log.e("Constant.posType", Constant.posType.toString() + " ")
            when (Constant.posType) {
                "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI" -> try {
                    AidlUtil.woyouService.openDrawer(null)
                } catch (ex: java.lang.Exception) {
                    Log.e("Sunmi Exception Drawer", ex.toString() + "")
                }
                "V3_MIX_EDLA_GL" -> try {
                    selectPrinter?.cashDrawerApi()?.open(null)
                } catch (ex: java.lang.Exception) {
                    Log.e("Sunmi Exception Drawer", ex.toString() + "")
                }
                "D4-505", "D4", "D1", "M2-Max", "Swift 1", "S1", "M2-Pro", "D1-Pro" -> {
                    IminSDKManager.opencashBox()
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("10 Exception OpenDrawer", ex.toString() + "")
        }
    }

    fun printReyBitmap(string: String) {
        Log.d("printReyBitmap", "called")
        checkIminPrinter()
        try {
            when (Constant.posType) {
                "MobiPrint" -> print!!.printBitmap(string)
                "WISENET5" -> {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(string), null, options)
                    mPrinter!!.printImageBase(
                        bitmap,
                        400,
                        200,
                        wangpos.sdk4.libbasebinder.Printer.Align.CENTER,
                        0
                    )
                }

                "MP4" -> {
                    try {
//                    if(true)
//                        return;
//                    Image2();
                        var file = File(string)
                        if (!file.exists()) file = File(
                            LoginActivity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                ?.getPath()
                                .toString() + "/unzipFolder/files/10001002/logo.bmp"
                        )
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        val bitmap =
                            BitmapFactory.decodeStream(FileInputStream(file), null, options)
                        val arrayBitmap = ArrayList<Bitmap?>()
                        val empty: Bitmap =
                            drawText("", null, 20, true, false, 0, null)
                        arrayBitmap.add(empty)
                        arrayBitmap.add(bitmap)


//                    Bitmap last = createSingleImageFromMultipleImages(arrayBitmap);
//                    CsPrinter.printBitmap(file.getPath(),2);
                        val is_black = FileInputStream(string)
                        val input: ByteArray = InputStreamToByte(is_black)
                        CsPrinter.printBitmap(input)
                        val size = file.length().toInt()
                        val bytes = ByteArray(size)
                        try {
                            val buf = BufferedInputStream(FileInputStream(file))
                            buf.read(bytes, 0, bytes.size)
                            buf.close()
                        } catch (e: FileNotFoundException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        } catch (e: IOException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    } catch (ex: java.lang.Exception) {
                        Log.e("Rey Exception MP3_Plus", ex.toString() + "")
                    }
                }

                "MP3_Plus", "MobiPrint 4+", "MobiPrint4_Plus", "Mobiwire MP4", "k80hd_bsp_fwv_512m" -> {
                    try {
                        Log.d("printReyBitmap", "im in right place")
                        Log.d(
                            "printReyBitmap",
                            LoginActivity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                ?.getPath()
                                .toString() + "/unzipFolder/files/10001002/logo.bmp"
                        )
                        printRey("\n", 1, 0, 1)

//                    if(true)
//                        return;
//                    Image2();
                        var file = File(string)
                        if (!file.exists()) file = File(
                            LoginActivity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                ?.getPath()
                                .toString() + "/unzipFolder/files/10001002/logo.bmp"
                        )
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        val bitmap =
                            BitmapFactory.decodeStream(FileInputStream(file), null, options)
                        val arrayBitmap = ArrayList<Bitmap?>()
                        val empty: Bitmap =
                            drawText("", null, 20, true, false, 0, null)
                        arrayBitmap.add(empty)
                        arrayBitmap.add(bitmap)
                        val is_black = FileInputStream(string)
                        val input: ByteArray = InputStreamToByte(is_black)
                        CsPrinter.printBitmap(input)
                        val size = file.length().toInt()
                        val bytes = ByteArray(size)
                        try {
                            val buf = BufferedInputStream(FileInputStream(file))
                            buf.read(bytes, 0, bytes.size)
                            buf.close()
                        } catch (e: FileNotFoundException) {
                            Log.e("Rey Exception MP3_Plus", e.toString() + "")

                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        } catch (e: IOException) {
                            Log.e("Rey Exception MP3_Plus", e.toString() + "")

                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    } catch (ex: java.lang.Exception) {
                        Log.e("Rey Exception MP3_Plus", ex.toString() + "")
                    }
                    try {
                        Log.e("printReyprintRey 1", CsPrinter.getLastError().toString() + " ")
                        Log.e("printReyprintRey 2", CsPrinter.getPrinterStatus().toString() + " ")
                        Log.e(
                            "printReyprintRey 3",
                            CsPrinter.getCurrentVoltageStatus().toString() + " "
                        )
                        Log.e("printReyprintRey 4", CsPrinter.getPaperStatus().toString() + " ")
                        Log.e("printReyprintRey 5", CsPrinter.getPowerState().toString() + " ")
                        Log.e("printReyprintRey 6", CsPrinter.getTempStatus().toString() + " ")
                        Log.e(
                            "printReyprintRey 7",
                            CsPrinter.printGetPrintedLength().toString() + " "
                        )
                    } catch (ex: java.lang.Exception) {
                        Log.e("printReyprintRey", ex.message!!)
                    }
                    printRey("\n", 1, 0, 1)
                }

                "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI" -> {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(string), null, options)
                    AidlUtil.getInstance().printBitmap(bitmap, 0)
                }

                "V3_MIX_EDLA_GL" -> {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(string), null, options)
                    selectPrinter?.lineApi()?.run {
                        printBitmap(
                            bitmap, BitmapStyle.getStyle().setAlign(Align.CENTER).setAlgorithm(
                                ImageAlgorithm.BINARIZATION
                            ).setValue(120).setWidth(384).setHeight(225)
                        )
                        autoOut()
                    }
                }

                "D4-505",
                "D4",
                "D1",
                "M2-Pro",
                "M2-Max",
                "Swift 1",
                "S1",
                "D1-Pro" -> {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.RGB_565
                    var bitmap_black =
                        BitmapFactory.decodeStream(FileInputStream(string), null, options)
                    bitmap_black = getBlackWhiteBitmap(bitmap_black)
                    mIminPrintUtils!!.printSingleBitmap(bitmap_black, 1)
                    mIminPrintUtils!!.printAndLineFeed()
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("1 Exception WiseNet", ex.toString() + "")
        }
    }


    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        return Build.MODEL
    }

    fun getBlackWhiteBitmap(bitmap: Bitmap?): Bitmap? {
        val w = bitmap?.width
        val h = bitmap?.height
        val resultBitmap = Bitmap.createBitmap(w!!, h!!, Bitmap.Config.RGB_565)
        var color = 0
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var r1: Int
        var g1: Int
        var b1: Int
        val oldPx = IntArray(w * h)
        val newPx = IntArray(w * h)
        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h)
        for (i in 0 until w * h) {
            color = oldPx[i]
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)
            a = Color.alpha(color)
            //黑白矩阵
            r1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
            g1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
            b1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
            //检查各像素值是否超出范围
            if (r1 > 255) {
                r1 = 255
            }
            if (g1 > 255) {
                g1 = 255
            }
            if (b1 > 255) {
                b1 = 255
            }
            newPx[i] = Color.argb(a, r1, g1, b1)
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h)
        return getGreyBitmap(resultBitmap)
    }

    fun getGreyBitmap(bitmap: Bitmap?): Bitmap? {
        return if (bitmap == null) {
            null
        } else {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val gray = IntArray(height * width)
            var e: Int
            var i: Int
            var j: Int
            var g: Int
            e = 0
            while (e < height) {
                i = 0
                while (i < width) {
                    j = pixels[width * e + i]
                    g = j and 16711680 shr 16
                    gray[width * e + i] = g
                    ++i
                }
                ++e
            }
            i = 0
            while (i < height) {
                j = 0
                while (j < width) {
                    g = gray[width * i + j]
                    if (g >= 128) {
                        pixels[width * i + j] = -1
                        e = g - 255
                    } else {
                        pixels[width * i + j] = -16777216
                        e = g - 0
                    }
                    if (j < width - 1 && i < height - 1) {
                        gray[width * i + j + 1] += 3 * e / 8
                        gray[width * (i + 1) + j] += 3 * e / 8
                        gray[width * (i + 1) + j + 1] += e / 4
                    } else if (j == width - 1 && i < height - 1) {
                        gray[width * (i + 1) + j] += 3 * e / 8
                    } else if (j < width - 1 && i == height - 1) {
                        gray[width * i + j + 1] += e / 4
                    }
                    ++j
                }
                ++i
            }
            val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            mBitmap
        }
    }

    fun round(value: Double, places: Int): Double {
        var value = value
        require(places >= 0)
        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }

    fun downloadFile(url: URL, outputFileName: String) {
        url.openStream().use {
            Channels.newChannel(it).use { rbc ->
                FileOutputStream(outputFileName).use { fos ->
                    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                }
            }
        }
    }

    fun drawText(
        text: String?,
        textTwo: String?,
        textSize: Int,
        isBold: Boolean,
        isUnderline: Boolean,
        align: Int,
        ttf: Typeface?
    ): Bitmap {
        var text = text
        val textWidth = 384
        if (textTwo != null) {
            val llen = 20
            val rlen = 20
            val text1: String = padRight(text, rlen)
            val text2: String = padLeft(textTwo, llen)
            text = text1 + text2
        }
        val textPaint = TextPaint(
            Paint.ANTI_ALIAS_FLAG
                    or Paint.LINEAR_TEXT_FLAG
        )
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = textSize.toFloat()
        textPaint.isFakeBoldText = isBold
        textPaint.typeface = ttf
        if (isUnderline) textPaint.flags = Paint.UNDERLINE_TEXT_FLAG
        var mTextLayout = StaticLayout(
            text, textPaint,
            textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
        )
        if (align == 0) mTextLayout = StaticLayout(
            text, textPaint,
            textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
        ) else if (align == 1) mTextLayout = StaticLayout(
            text, textPaint,
            textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
        ) else if (align == 2) mTextLayout = StaticLayout(
            text, textPaint,
            textWidth, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false
        )
        val b = Bitmap.createBitmap(textWidth, mTextLayout.height, Bitmap.Config.RGB_565)
        val c = Canvas(b)
        val paint = Paint(
            (Paint.ANTI_ALIAS_FLAG
                    or Paint.LINEAR_TEXT_FLAG)
        )
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        c.drawPaint(paint)
        c.save()
        c.translate(0f, 0f)
        mTextLayout.draw(c)
        c.restore()
        return b
    }


    fun padRight(s: String?, n: Int): String {
        return String.format("%1$-" + n + "s", s)
    }

    fun padLeft(s: String?, n: Int): String {
        return String.format("%1$" + n + "s", s)
        //String.format("%10s", "foo");
    }

    fun FooterPrint() {
//        pDialog.hide();
    }

    @Throws(IOException::class)
    fun InputStreamToByte(`is`: InputStream): ByteArray {
        val bytestream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var ch: Int
        while (`is`.read(buffer).also { ch = it } != -1) {
            bytestream.write(buffer, 0, ch)
        }
        val data = bytestream.toByteArray()
        bytestream.close()
        return data
    }

    fun returnStars(): String {
        return when (Constant.posType) {
            "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI", "D4-505", "D4", "D1", "D1-Pro", "M2-Max", "Swift 1", "S1", "M2-Pro" -> "************************************************"
            "MP3_Plus", "MP4", "Mobiwire MP4", "MobiPrint4_Plus", "k80hd_bsp_fwv_512m" -> "******************************"
            else -> "******************************"
        }
    }

    fun returnLines(): String {
        return when (Constant.posType) {
            "T2mini", "T1mini-G", "T2mini_s", "D2mini", "T2s", "K2_PRO", "K2_MINI", "D4-505", "D4", "D1", "D1-Pro", "M2-Max", "Swift 1", "S1", "M2-Pro" -> "------------------------------------------------"
            "MP3_Plus", "MP4", "Mobiwire MP4", "MobiPrint4_Plus", "k80hd_bsp_fwv_512m" -> "--------------------------------"
            else -> "--------------------------------"
        }
    }


}