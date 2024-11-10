import 'package:all_printer/models/InvoiceListModel.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:all_printer/all_printer.dart';
import 'package:dio/dio.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _allPrinterPlugin = AllPrinter();

  var dio = Dio();

  InvoiceListModel? invoiceListModel;
  dynamic invoice = [];
  String invoiceText = '';
  String merchantId = "50608101";

  getInvoice() async {
    // invoiceListModel = InvoiceListModel(invoice: [
    //   Invoice(
    //       key: 'text',
    //       value: "The Quick Brown fox jumped over The Lazy Dog test"),
    //   Invoice(key: 'date', value: "2022-01-30 10:25:35"),
    //   Invoice(key: 'merchent', value: "Merchent ID: $merchantId"),
    //   Invoice(key: 'terminal', value: "Terminal ID: 11111111"),
    //   Invoice(key: 'star1', value: "****************&&**************"),
    // ]);

    //
    try {
      // var response = await Dio().get('http://213.159.5.155:410/invoice.json');
      var index = 0;
      setState(() {
        invoice = {
          "$index": "align:1",
          "${++index}": "The Best Company In The World",
          "${++index}": "align:0",
          // "${++index}": "Date:2022-01-30 10:25:35",
          // "${++index}": "Name: Altkamul Printer Test",
          // "${++index}": "Merchent ID: $merchantId",
          // "${++index}": "Terminal ID: 667766776",
          // "${++index}": "Transaction ID: 10000001",
          // "${++index}": "Voucher No: 22-003111",
          // "${++index}": "Car No: 1001k",
          "${++index}": "اسم الزبون: يزن شيخ محمد",
          "${++index}": "Customer Name: يزن شيخ محمد",
          // "${++index}": "Customer No: 971512345678",
          // "${++index}": "******************************",
          // "${++index}": "size:34",
          // "${++index}": "Tax Invoice",
          // "${++index}": "size:24",
          // "${++index}": "******************************",
          // "${++index}": "Title: Exterir Wash Small Car",
          // "${++index}": "service: Wash",
          // "${++index}": "price: 35.00",
          // "${++index}": "qty: 2",
          // "${++index}": "size:17",
          // "${++index}": "------ add ones ------",
          // "${++index}": "-> Kutchab + (4.0 AED X 5.0) = 20.00 AED",
          // "${++index}": "----------------------",
          // "${++index}": "size:24",
          "${++index}": "Title: Exterir Wash - غسلة كاملة",
          "${++index}": "service: Wash غسيل",
          "${++index}": "price: 35.00",
          "${++index}": "qty: 2",
          // "${++index}": "",
          // "${++index}": "Total Qty: 2",
          // "${++index}": "Total Befor Vat: 70.00 AED",
          // "${++index}": "Vat: @5%: 11.00 AED",
          // "${++index}": "-------------------------------",
          // "${++index}": "Total: 71.00 AED",
          // "${++index}": "******************************",
          // "${++index}": "Hi There",
          // "${++index}": "******************************",
          // "${++index}": "City: Dubai UAE Call Us : 05123456789",
          // "${++index}": "-------------------------------",
          // "${++index}": "Thanks you for try our Flutter base POS"
        };
      });
    } catch (e) {
      print("Error : ${e.toString()}");
    }
    //
    //
    // for (Invoice item in invoiceListModel?.invoice ?? []) {
    //   invoice[item.key] = item.value;
    // }
  }

  @override
  void initState() {
    // _allPrinterPlugin.getPermission();
    _allPrinterPlugin.getDeviceSerial();
    checkPermission();
    super.initState();
  }

  Future<void> checkPermission() async {
    var status = await Permission.manageExternalStorage.status;
    if (status != PermissionStatus.granted) {
      await Permission.manageExternalStorage.request();
    }
    var status2 = await Permission.storage.status;

    if (status2 != PermissionStatus.granted) {
      await Permission.storage.request();
    }
    var installPackage = await Permission.requestInstallPackages.status;
    if (installPackage != PermissionStatus.granted) {
      await Permission.requestInstallPackages.request();
    }
    var phone = await Permission.phone.status;
    if (phone != PermissionStatus.granted) {
      await Permission.phone.request();
    }
    return;
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion = 'starting ... ';

    String fullPath = await _allPrinterPlugin.getDownloadPath(merchantId);

    bool isDone = await _allPrinterPlugin.download(
        dio,
        "https://www.altkamul.net/content/merchants/$merchantId/$merchantId/logo.bmp",
        fullPath);

    await getInvoice();
    //invoice['logoPath'] = "storage/emulated/0/download/printing.bmp";

    if (isDone) {
      // invoice['logoPath'] = fullPath;
      // platformVersion =
      //     await _allPrinterPlugin.printImage(imagePath: fullPath) ?? '';
    }
    //var ii = convertToFastPrint(invoice);
    platformVersion = await _allPrinterPlugin.print(invoice: invoice) ?? '';

    // platformVersion =
    //     await _allPrinterPlugin.printSingleLine(line: "this normal text !") ??
    //         '';
    _allPrinterPlugin.printQrCode(qrData: "data");

    _allPrinterPlugin.printReyFinish();

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
        // floatingActionButton: FloatingActionButton(
        //     onPressed: () => initPlatformState(),
        //     child: const Icon(Icons.print)),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(15),
          child: Column(
            children: [
              const SizedBox(
                height: 20,
              ),
              Center(
                child: Text('print result: $_platformVersion\n'),
              ),
              ElevatedButton(
                onPressed: () => initPlatformState(),
                child: const SizedBox(
                  width: double.infinity,
                  child:
                      Text("Print Full invoice", textAlign: TextAlign.center),
                ),
              ),
              ElevatedButton(
                onPressed: () => printText(),
                child: const SizedBox(
                  width: double.infinity,
                  child: Text("Print Text", textAlign: TextAlign.center),
                ),
              ),
              ElevatedButton(
                onPressed: () => printTextAr(),
                child: const SizedBox(
                  width: double.infinity,
                  child: Text("Print Arabic Text", textAlign: TextAlign.center),
                ),
              ),
              ElevatedButton(
                onPressed: () => printImage(),
                child: const SizedBox(
                  width: double.infinity,
                  child: Text("Print Image", textAlign: TextAlign.center),
                ),
              ),
              ElevatedButton(
                onPressed: () => printQrCode(),
                child: const SizedBox(
                  width: double.infinity,
                  child: Text("Print QrCode", textAlign: TextAlign.center),
                ),
              ),
              ElevatedButton(
                onPressed: () => _allPrinterPlugin.printReyFinish(),
                child: const SizedBox(
                  width: double.infinity,
                  child: Text("Print Finish", textAlign: TextAlign.center),
                ),
              ),
              // ElevatedButton(
              //   onPressed: () => getPlatformVersion(),
              //   child: const SizedBox(
              //     width: double.infinity,
              //     child: Text("Print invoice as image testing",
              //         textAlign: TextAlign.center),
              //   ),
              // ),
              ElevatedButton(
                onPressed: () => getPlatformVersion(),
                child: const SizedBox(
                  width: double.infinity,
                  child: Text("Check Platform Version",
                      textAlign: TextAlign.center),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  printText() async {
    String platformVersion = 'starting ... ';
    await getInvoice();
    platformVersion = await _allPrinterPlugin.printSingleLine(
            line:
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.") ??
        '';
    _allPrinterPlugin.printReyFinish();
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  printTextAr() async {
    String platformVersion = 'starting ... ';
    await getInvoice();
    platformVersion = await _allPrinterPlugin.printSingleLine(
            line:
                """ هنالك العديد من الأنواع المتوفرة لنصوص لوريم إيبسوم، ولكن الغالبية تم تعديلها بشكل ما عبر إدخال بعض النوادر أو الكلمات العشوائية إلى النص. إن كنت تريد أن تستخدم نص لوريم إيبسوم ما، عليك أن تتحقق أولاً أن ليس هناك أي كلمات أو عبارات محرجة أو غير لائقة مخبأة في هذا النص. بينما تعمل جميع مولّدات نصوص لوريم إيبسوم على الإنترنت على إعادة تكرار مقاطع من نص لوريم إيبسوم نفسه عدة مرات بما تتطلبه الحاجة، يقوم مولّدنا هذا باستخدام كلمات من قاموس يحوي على أكثر من 200 كلمة لا تينية، مضاف إليها مجموعة من الجمل النموذجية، لتكوين نص لوريم إيبسوم ذو شكل منطقي قريب إلى النص الحقيقي. وبالتالي يكون النص الناتح خالي من التكرار، أو أي كلمات أو عبارات غير لائقة أو ما شابه. وهذا ما يجعله أول مولّد نص لوريم إيبسوم حقيقي على الإنترنت.  """) ??
        '';
    _allPrinterPlugin.printReyFinish();
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  printImage() async {
    String platformVersion = 'starting ... ';

    String fullPath = '/storage/emulated/0/download/rendered_image.jpg';

    // bool isDone = await _allPrinterPlugin.download(
    //     dio,
    //     "http://smartepaystaging.altkamul.ae/Content/Merchants/$merchantId/$merchantId/printing.bmp",
    //     fullPath);

    platformVersion =
        await _allPrinterPlugin.printImage(imagePath: fullPath) ?? '';
    _allPrinterPlugin.printReyFinish();
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  printQrCode() async {
    String platformVersion = 'starting ... ';
    platformVersion = await _allPrinterPlugin.printQrCode(
            qrData: "https://stg.catalogak.info/index.html") ??
        '';
    _allPrinterPlugin.printReyFinish();
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  getPlatformVersion() async {
    String? platformVersion = 'starting ... ';
    platformVersion = await _allPrinterPlugin.getPlatformVersion();
    setState(() {
      _platformVersion = platformVersion!;
    });
  }

  Map convertToFastPrint(Map inv) {
    Map shortCutMap = {};
    int index = 0;
    String line = '';
    for (var e in inv.entries) {
      if (e.key.toString().contains('logoPath')) {
        shortCutMap['${e.key}'] = e.value;
        continue;
      } else if (e.value.toString().contains('align') ||
          e.value.toString().contains('size')) {
        /// assign line to index and clear it .
        if (line.isNotEmpty) {
          shortCutMap['$index'] = line;
          line = '';
          index++;
        }

        /// new index .
        shortCutMap['$index'] = e.value;
        index++;
      } else if (line.length >= 700) {
        shortCutMap['$index'] = line + e.value;
        line = '';
        index++;
      } else {
        if (e.value.toString() != '' || e.value != null) {
          line = '${line + e.value} \n';
        }
      }
    }
    if (line.isNotEmpty) {
      shortCutMap['$index'] = line;
    }
    return shortCutMap;
  }
}

extension StringExtensions on String {
  bool get isContainArabicLatter {
    final arabicRegex = RegExp(r'[\u0600-\u06FF]');
    return arabicRegex.hasMatch(this);
  }

  bool get isContainEnglishLatter {
    final englishRegex = RegExp(r'[a-zA-Z]');
    return englishRegex.hasMatch(this);
  }
}
