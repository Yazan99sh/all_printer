import 'dart:io';

import 'package:all_printer/utils/logger.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

import 'all_printer_platform_interface.dart';

/// An implementation of [AllPrinterPlatform] that uses method channels.
class MethodChannelAllPrinter extends AllPrinterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('all_printer');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    AppLogger.logDebug("getPlatformVersion() : ${version.toString()}");
    return version;
  }

  @override
  Future<String?> print(dynamic invoice) async {
    final printResult =
        await methodChannel.invokeMethod<String>('print', invoice);
    AppLogger.logDebug("print() : ${printResult.toString()}");
    return printResult;
  }

  @override
  Future<String?> printLine(String line) async {
    final printResult =
        await methodChannel.invokeMethod<String>('printLine', line);
    AppLogger.logDebug("printLine() : ${printResult.toString()}");
    return printResult;
  }

  @override
  Future<String?> printQrCode(String? qrData) async {
    final printResult =
        await methodChannel.invokeMethod<String>('printQrCode', qrData);
    AppLogger.logDebug("printQrCode() : ${printResult.toString()}");
    return printResult;
  }

  @override
  Future<String?> printFinish() async {
    final printResult =
        await methodChannel.invokeMethod<String>('printReyFinish');
    AppLogger.logDebug("printFinish() : ${printResult.toString()}");
    return printResult;
  }

  @override
  Future<String?> printImage(String imagePath) async {
    final printResult =
        await methodChannel.invokeMethod<String>('printImage', imagePath);
    AppLogger.logDebug("printImage() : ${printResult.toString()}");
    return printResult;
  }

  // get device information
  @override
  Future<String?> getDeviceSerial() async {
    final result = await methodChannel.invokeMethod<String>('serial');
    AppLogger.logDebug("getDeviceSerial() : ${result.toString()}");
    return result;
  }

// open drawer
  @override
  Future<String?> openDrawer() async {
    final result = await methodChannel.invokeMethod<String>('openDrawer');
    AppLogger.logDebug("openDrawer() : ${result.toString()}");
    return result;
  }

  @override
  Future download(Dio dio, String url, String savePath) async {
    try {
      Response response = await dio.get(
        url,
        onReceiveProgress: showDownloadProgress,
        //Received data with List<int>
        options: Options(
            responseType: ResponseType.bytes,
            followRedirects: false,
            validateStatus: (status) {
              return status! < 500;
            }),
      );

      // File(savePath)
      //  .writeAsBytesSync(response.data);

      // print(response.headers);
      File file = File(savePath);
      var raf = file.openSync(mode: FileMode.write);
      // response.data is List<int> type
      raf.writeFromSync(response.data);
      await raf.close();
      return true;
    } catch (e) {
      if (kDebugMode) {
        AppLogger.logError('Failed to get platform version. ${e.toString()}');
      }
      return false;
    }
  }

  @override
  void showDownloadProgress(received, total) {
    if (total != -1) {
      AppLogger.logInfo((received / total * 100).toStringAsFixed(0) + "%");
    }
  }

  @override
  Future<String> getDownloadPath(String? uniqueId) async {
    try {
      var tempDir = await getApplicationDocumentsDirectory();

      String path = "${tempDir.path}/$uniqueId";
      if (!(await Directory(path).exists())) {
        await Directory(path).create();
        AppLogger.logWarning("New Directory Created !");
      }

      String fullPath = "$path/printing.bmp";
      if (kDebugMode) {
        AppLogger.logInfo('full path $fullPath');
      }
      return fullPath;
    } on PlatformException catch (e) {
      if (kDebugMode) {
        AppLogger.logError('Failed to get platform version. ${e.message}');
      }
      return 'Failed to get platform version.';
    }
  }
}
