import 'package:flutter/material.dart';

import 'package:permission_handler/permission_handler.dart';
import 'package:flutter/cupertino.dart';

import 'package:super_qr_reader/qrcode_reader_controller.dart';
import 'package:super_qr_reader/qrcode_reader_view.dart';

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
  bool isOk = false;
  String data = '';

  var scanResult;
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
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            RaisedButton(
              onPressed: () async {
//                Map<PermissionGroup, PermissionStatus> permissions =
//                    await PermissionHandler()
//                        .requestPermissions([PermissionGroup.camera]);
//                print(permissions);
//
//                if (permissions[PermissionGroup.camera] ==
//                    PermissionStatus.granted) {
//                  isOk = true;
//                } else {
//                  isOk = false;
//                }
//                if (isOk) {
//                  print('permission granted');
//                  String results = await Navigator.push(context,
//                      MaterialPageRoute(builder: (context) => ScanViewDemo()));
//
//                  if (results != null) {
//                    setState(() {
//                      data = results;
//                    });
//                  }
//                } else {
//                  print('no permissions!!!!!!!');
//                }

                String results = await Navigator.push(context,
                    MaterialPageRoute(builder: (context) => ScanViewDemo()));

                if (results != null) {
                  setState(() {
                    data = results;
                  });
                }
              },
              child: Text("独立UI"),
            ),
            Text(data),
          ],
        ),
      ),
    );
  }
}

class ScanViewDemo extends StatefulWidget {
  ScanViewDemo({Key key}) : super(key: key);

  @override
  _ScanViewDemoState createState() => new _ScanViewDemoState();
}

class _ScanViewDemoState extends State<ScanViewDemo> {
  GlobalKey<QrcodeReaderViewState> _key = GlobalKey();
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: QrcodeReaderView(
        key: _key,
        onScan: onScan,
        headerWidget: AppBar(
          backgroundColor: Colors.transparent,
          elevation: 0.0,
        ),
      ),
    );
  }

  Future onScan(String data) async {
    Navigator.of(context).pop(data);
  }

  @override
  void dispose() {
    super.dispose();
  }
}
