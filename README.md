# flutter_qr_reader

QR code (scan code &#x2F; picture) recognition （AndroidView&#x2F;UiKitView）

## DEMO

![demo](https://github.com/hetian9288/flutter_qr_reader/blob/master/ezgif-3-7c8bfe5fd68a.gif?raw=true)

## Getting Started
```import 'package:flutter_qr_reader/flutter_qr_reader.dart';```
```
Column(
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
                  var image = await ImagePicker.pickImage(source: ImageSource.gallery);
                  if (image == null) return ;
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
        )
```