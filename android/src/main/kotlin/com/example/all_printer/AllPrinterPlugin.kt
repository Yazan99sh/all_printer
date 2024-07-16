package com.example.all_printer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import androidx.core.content.ContextCompat as Compat
import com.imin.library.SystemPropManager


/** AllPrinterPlugin */
class AllPrinterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    var mContext: Context? = null
    var aContext: Context? = null

    private var activity: Activity? = null

    private var printerObject: PrintingMethods? = null


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "all_printer")
        channel.setMethodCallHandler(this)
        printerObject = PrintingMethods(flutterPluginBinding.applicationContext)
        printerObject?.initializePrinters()
        mContext = flutterPluginBinding.applicationContext

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {

                val deviceModel: String = SystemPropManager.getModel()
                val brand = SystemPropManager.getBrand()

                result.success(
                        "Android ${android.os.Build.VERSION.RELEASE} \n" +
                                " Device Name : ${getDeviceName()} \n device Model :   $deviceModel \n Brand : $brand "
                )

            }
            "printReyFinish" -> {
                try {
                    printerObject?.printReyFinish()
                    result.success("success !")
                } catch (e: Exception) {
                    result.success("${e.message}");
                }

            }
            "printQrCode" -> {
                try {
                    printerObject?.printQrCode(null, "\n ${call.arguments} \n")
                    result.success("success !")
                } catch (e: Exception) {
                    result.success("${e.message}");
                }

            }
            "printLine" -> {

                if (call.arguments != null) {
                    try {
                        printerObject?.printRey("${call.arguments}", 1,0,0)
                        result.success("success !")
                    } catch (e: Exception) {
                        result.success("${e.message}");
                    }
                } else {
                    result.success("line not found !")
                }
            }
            "printImage" -> {

                if (call.arguments != null) {
                    try {
                        printerObject?.printReyBitmap("${call.arguments}")
//                        printerObject?.printRey("\n")
                        result.success("success ! ")
                    } catch (e: Exception) {
                        result.success("${e.message}");
                    }
                } else {
                    result.success("image not found !")
                }

            }
            "print" -> {

                try {

//                    result.success("printer device Name : none")

                    val hashMap = call.arguments as HashMap<*, *>

                    val logoPath = call.argument<String>("logoPath")

                    var loremX500 = ""
                    var textSize = 1
                    var textAlign = 0
                    var textDirection = 1
                    var index = 0

                    if (logoPath != null) {
                        printerObject?.printReyBitmap(logoPath)
                    }
                    hashMap.forEach {
                        if (it.key != "logoPath") {
                            if ("${hashMap["$index"]}".startsWith(prefix = "align")) {
                                printRey(loremX500, null, textSize,textAlign,textDirection)
                                textAlign = "${hashMap["$index"]}".split(":").last().toInt()
                                loremX500 = ""
                            }else if ("${hashMap["$index"]}".startsWith(prefix = "dir")) {
                                printRey(loremX500, null, textSize,textAlign,textDirection)
                                textDirection = "${hashMap["$index"]}".split(":").last().toInt()
                                loremX500 = ""
                            } else if ("${hashMap["$index"]}".startsWith(prefix = "size")) {
                                printRey(loremX500, null, textSize,textAlign,textDirection)
                                textSize = "${hashMap["$index"]}".split(":").last().toInt()
                                loremX500 = ""
                            } else if (printerObject?.isProbablyArabic("${hashMap["$index"]}") == true) {
                                printRey(loremX500, null, textSize,textAlign,textDirection)
                                printRey("${hashMap["$index"]}", null, textSize,textAlign,textDirection)
                                loremX500 = ""
                            } else {
                                loremX500 += "\n${hashMap["$index"]}"
                            }
                            index++
                        }
                    }


                    printRey(loremX500, null, textSize,textAlign,textDirection)
                    loremX500 = ""

                    index = 0

//                    Log.d("loremX500", loremX500)
//                    val deviceName = printRey(loremX500, logoPath);
//                    result.success("printer device Name : $deviceName");
                    result.success("success !");
                } catch (e: Exception) {
                    result.success(" Print Exception : ${e.message}");
                }
            }

            "serial" -> {
                try {
                    result.success(printerObject?.getDevicePos())
                } catch (e: Exception) {
                    result.success("${e.message}");
                }
            }
            "openDrawer" -> {
                try {
                    result.success(printerObject?.openDrawer())
                } catch (e: Exception) {
                    result.success("${e.message}");
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun printRey(loremX500: String, logoPath: String?, textSize: Int, textAlign: Int,textDirection: Int=1): String {
        Log.d("PosType", Constant.posType)

        return try {
            if (logoPath != null)
                printerObject?.printReyBitmap(logoPath)

            printerObject?.printRey(loremX500, textSize,textAlign,textDirection,)
            "print success"
        } catch (e: Exception) {
            "${e.message}"
        }

    }


    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        return Build.MODEL
    }

    private fun checkPermission() {
        if (mContext != null)
            if (Compat.checkSelfPermission(
                            mContext!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) !== PackageManager.PERMISSION_GRANTED
            ) {
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                            activity!!, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 0
                    )
                }
            } else {
                Toast.makeText(mContext, "Permission already granted", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

    }

    override fun onDetachedFromActivity() {

    }

}
