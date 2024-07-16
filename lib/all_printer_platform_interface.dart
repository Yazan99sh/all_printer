import 'dart:io';

import 'package:all_printer/utils/logger.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'all_printer_method_channel.dart';

abstract class AllPrinterPlatform extends PlatformInterface {
  /// Constructs a AllPrinterPlatform.
  AllPrinterPlatform() : super(token: _token);

  static final Object _token = Object();

  static AllPrinterPlatform _instance = MethodChannelAllPrinter();

  /// The default instance of [AllPrinterPlatform] to use.
  ///
  /// Defaults to [MethodChannelAllPrinter].
  static AllPrinterPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AllPrinterPlatform] when
  /// they register themselves.
  static set instance(AllPrinterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> print(dynamic invoice){
    throw UnimplementedError('print() has not been implemented.');
  }

  Future<String?> printLine(String line){
    throw UnimplementedError('printLine() has not been implemented.');
  }

  Future<String?> printImage(String imagePath){
    throw UnimplementedError('printImage() has not been implemented.');
  }
  Future<String?> getDeviceSerial(){
    throw UnimplementedError('getDeviceSerial() has not been implemented.');
  }
  Future<void> openDrawer(){
    throw UnimplementedError('openDrawer() has not been implemented.');
  }
  Future download(Dio dio, String url, String savePath) async {
    throw UnimplementedError('download() has not been implemented.');
  }

  void showDownloadProgress(received, total) {
    throw UnimplementedError('showDownloadProgress() has not been implemented.');
  }

  Future<String> getDownloadPath(String? uniqueId)async {
    throw UnimplementedError('getDownloadPath() has not been implemented.');
  }

  Future printFinish() {
    throw UnimplementedError('printFinish() has not been implemented.');
  }

  Future<String?> printQrCode(String? qrData) {
    throw UnimplementedError('printQrCode() has not been implemented.');
  }

}
