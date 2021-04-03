import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_qr_reader/flutter_qr_reader.dart';
import 'package:flutter_qr_reader_example/qrcode_reader_view.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();

  bool _flashlightState = false;
  bool _showScanView = false;
  QrReaderViewController? _controller;

  @override
  void initState() {
    super.initState();
  }

  void alert(String tip) {
    ScaffoldMessenger.of(scaffoldKey.currentContext!).showSnackBar(SnackBar(content: Text(tip)));
  }

  void openScanUI(BuildContext context) async {
    if (_showScanView) {
      await stopScan();
    }
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return new Scaffold(
        body: QrcodeReaderView(
          onScan: (result) async {
            Navigator.of(context).pop();
            alert(result);
          },
          headerWidget: AppBar(
            backgroundColor: Colors.transparent,
            elevation: 0.0,
          ),
        ),
      );
    }));
  }

  Future<bool> permission() async {
    if (_openAction) return false;
    _openAction = true;
    var status = await Permission.camera.status;
    print(status);
    if (status.isDenied || status.isPermanentlyDenied) {
      status = await Permission.camera.request();
      print(status);
    }

    if (status.isRestricted) {
      alert("请必须授权照相机权限");
      await Future.delayed(Duration(seconds: 3));
      openAppSettings();
      _openAction = false;
      return false;
    }

    if (!status.isGranted) {
      alert("请必须授权照相机权限");
      _openAction = false;
      return false;
    }
    _openAction = false;
    return true;
  }

  bool _openAction = false;

  Future openScan(BuildContext context) async {
    if (false == await permission()) {
      return;
    }

    setState(() {
      _showScanView = true;
    });
  }

  Future startScan() async {
    assert(_controller != null);
    _controller?.startCamera((String result, _) async {
      await stopScan();
      showDialog(
        context: scaffoldKey.currentContext!,
        builder: (context) {
          return AlertDialog(
            title: Text('扫码结果'),
            content: Text(result),
          ).build(context);
        },
      );
    });
  }

  Future stopScan() async {
    assert(_controller != null);
    await _controller?.stopCamera();
    setState(() {
      _showScanView = false;
    });
  }

  Future flashlight() async {
    assert(_controller != null);
    final state = await _controller?.setFlashlight();
    setState(() {
      _flashlightState = state ?? false;
    });
  }

  Future imgScan() async {
    var image = await ImagePicker().getImage(source: ImageSource.gallery);
    if (image == null) return;
    final rest = await FlutterQrReader.imgScan(image.path);

    showDialog(
      context: scaffoldKey.currentContext!,
      builder: (context) {
        return AlertDialog(
          title: Text('扫码结果'),
          content: Text(rest),
        ).build(context);
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        key: scaffoldKey,
        appBar: AppBar(
          title: const Text('二维码扫描演示'),
        ),
        body: Builder(builder: (context) {
          return Column(
            children: [
              TextButton(onPressed: () => openScanUI(context), child: Text('打开扫描界面')),
              TextButton(
                onPressed: imgScan,
                child: Text("识别图片"),
              ),
              Container(
                height: 1,
                margin: EdgeInsets.symmetric(vertical: 12),
                color: Colors.black12,
              ),
              _showScanView == false ? TextButton(onPressed: () => openScan(context), child: Text('启动扫描视图')) : Text('扫描视图已经启动'),
              TextButton(onPressed: flashlight, child: Text(_flashlightState == false ? '打开手电筒' : '关闭手电筒')),
              Container(
                height: 12,
                color: Colors.black12,
              ),
              _showScanView == true
                  ? Container(
                      width: 320,
                      height: 350,
                      child: QrReaderView(
                        width: 320,
                        height: 350,
                        callback: (container) {
                          this._controller = container;
                          this.startScan();
                        },
                      ),
                    )
                  : Container()
            ],
          );
        }),
      ),
    );
  }
}
