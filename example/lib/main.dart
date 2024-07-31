import 'package:all_printer/models/InvoiceListModel.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:all_printer/all_printer.dart';
import 'package:dio/dio.dart';
import 'package:screenshot/screenshot.dart';

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
          "$index": "The Quick Brown fox jumped over The Lazy Dog",
          "${++index}": "Name: Potato - ",
          "${++index}": "السلام عليكم ورحمة الله",
          "${++index}": "Date:2022-01-30 10:25:35",
          "${++index}": "Name: Altkamul Printer Test",
          "${++index}": "Merchent ID: $merchantId",
          "${++index}": "Terminal ID: 667766776",
          "${++index}": "Transaction ID: 10000001",
          "${++index}": "Voucher No: 22-003111",
          "${++index}": "Car No: 1001k",
          "${++index}": "Customer No: 971512345678",
          "${++index}": "******************************",
          "${++index}": "size:2",
          "${++index}": "Tax Invoice",
          "${++index}": "size:1",
          "${++index}": "",
          "${++index}": "******************************",
          "${++index}": "Title: Exterir Wash Small Car",
          "${++index}": "service: Wash",
          "${++index}": "price: 35.00",
          "${++index}": "qty: 2",
          "${++index}": "Total Qty: 2",
          "${++index}": "Total Befor Vat: 70.00 AED",
          "${++index}": "Vat: @5%: 11.00 AED",
          "${++index}": "-------------------------------",
          "${++index}": "Total: 71.00 AED",
          "${++index}": "******************************",
          "${++index}": "نص تاني بالعربي",
          "${++index}": "******************************",
          "${++index}": "City: Dubai UAE Call Us : 05123456789",
          "${++index}": "-------------------------------",
          "${++index}": "Thanks you for try our Flutter base POS"
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
    super.initState();
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
    invoice['logoPath'] = "storage/emulated/0/download/printing.bmp";

    if (isDone) {
      invoice['logoPath'] = fullPath;
      // platformVersion =
      //     await _allPrinterPlugin.printImage(imagePath: fullPath) ?? '';
    }

    platformVersion = await _allPrinterPlugin.print(invoice: invoice) ?? '';

    platformVersion =
        await _allPrinterPlugin.printSingleLine(line: "this normal text !") ??
            '';
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

    String fullPath = await _allPrinterPlugin.getDownloadPath(merchantId);

    bool isDone = await _allPrinterPlugin.download(
        dio,
        "http://smartepaystaging.altkamul.ae/Content/Merchants/$merchantId/$merchantId/printing.bmp",
        fullPath);

    if (isDone) {
      // fullPath="storage/emulated/0/download/printing.bmp";
      platformVersion =
          await _allPrinterPlugin.printImage(imagePath: fullPath) ?? '';
      _allPrinterPlugin.printReyFinish();
    }
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  printQrCode() async {
    String platformVersion = 'starting ... ';
    platformVersion =
        await _allPrinterPlugin.printQrCode(qrData: "https://stg.catalogak.info/index.html") ?? '';
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
}
