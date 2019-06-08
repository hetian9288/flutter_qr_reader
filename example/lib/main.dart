import 'package:flutter/material.dart';

import 'package:flutter_qr_reader/flutter_qr_reader.dart';
import 'package:flutter_qr_reader_example/scanViewDemo.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: HomePage());
  }
}

class HomePage extends StatefulWidget {
  HomePage({Key key}) : super(key: key);

  @override
  _HomePageState createState() => new _HomePageState();
}

class _HomePageState extends State<HomePage> {
  QrReaderViewController _controller;
  bool isOk = false;
  String data;
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: SingleChildScrollView(
        child: Column(
          children: <Widget>[
            FlatButton(
              onPressed: () async {
                Map<PermissionGroup, PermissionStatus> permissions =
                    await PermissionHandler().requestPermissions([PermissionGroup.camera]);
                print(permissions);
                if (permissions[PermissionGroup.camera] == PermissionStatus.granted) {
                  showDialog(
                    context: context,
                    builder: (context) {
                      return Dialog(
                        child: Text("ok"),
                      );
                    },
                  );
                  setState(() {
                    isOk = true;
                  });
                }
              },
              child: Text("请求权限"),
              color: Colors.blue,
            ),
            FlatButton(
              onPressed: () async {
                Navigator.push(context, MaterialPageRoute(builder: (context) => ScanViewDemo()));
              },
              child: Text("独立UI"),
            ),
            FlatButton(
                onPressed: () async {
                  var image = await ImagePicker.pickImage(source: ImageSource.gallery);
                  if (image == null) return;
                  final rest = await FlutterQrReader.imgScan(image);
                  setState(() {
                    data = rest;
                  });
                },
                child: Text("识别图片")),
            FlatButton(
                onPressed: () {
                  assert(_controller != null);
                  _controller.setFlashlight();
                },
                child: Text("切换闪光灯")),
            FlatButton(
                onPressed: () {
                  assert(_controller != null);
                  _controller.startCamera(onScan);
                },
                child: Text("开始扫码（暂停后）")),
            if (data != null) Text(data),
            if (isOk)
              Container(
                width: 320,
                height: 350,
                child: QrReaderView(
                  width: 320,
                  height: 350,
                  callback: (container) {
                    this._controller = container;
                    _controller.startCamera(onScan);
                  },
                ),
              )
          ],
        ),
      ),
    );
  }

  void onScan(String v, List<Offset> offsets) {
    print([v, offsets]);
    setState(() {
      data = v;
    });
    _controller.stopCamera();
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
  }
}
