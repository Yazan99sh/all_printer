import 'package:all_printer/utils/logger.dart';
import 'package:dio/dio.dart';
import 'package:permission_handler/permission_handler.dart';
import 'dart:io';

import 'all_printer_platform_interface.dart';

class AllPrinter {
  Future<String?> getPlatformVersion() {
    return AllPrinterPlatform.instance.getPlatformVersion();
  }

  Future<String?> getDeviceSerial() {
    return AllPrinterPlatform.instance.getDeviceSerial();
  }

  Future<void> openDrawer() {
    return AllPrinterPlatform.instance.openDrawer();
  }

  Future<String?> printImage({required String imagePath}) {
    return AllPrinterPlatform.instance.printImage(imagePath);
  }

  Future<String?> print({required dynamic invoice}) {
    return AllPrinterPlatform.instance.print(invoice);
  }

  Future<String?> printSingleLine({required String line}) {
    return AllPrinterPlatform.instance.printLine(line);
  }

  Future printReyFinish() {
    return AllPrinterPlatform.instance.printFinish();
  }

  Future getPermission() async {
    if (!(await Permission.storage.isGranted)) {
      await Permission.storage.request();
    }
  }

  // # logo image # w=348  h=133 dep =24 dpi =120 B&W
  Future download(Dio dio, String url, String savePath) async {
    // if(!(await Permission.storage.isGranted)){
    //   await Permission.storage.request();
    // }
    if (!(await File(savePath).exists())) {
      return await AllPrinterPlatform.instance.download(dio, url, savePath);
    } else {
      AppLogger.logInfo("download(): $savePath File exists ! ");
      return true;
    }
  }

  Future<String> getDownloadPath(String? uniqueId) async {
    return await AllPrinterPlatform.instance.getDownloadPath(uniqueId);
  }

  Future<String?> printQrCode({required String? qrData}) async {
    return await AllPrinterPlatform.instance.printQrCode(qrData);
  }
}
