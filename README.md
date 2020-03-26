*This is a forked package from @hetian9288*

# super_qr_reader

QR code (scan code &#x2F; picture) recognition （AndroidView&#x2F;UiKitView）


## Getting Started

```dart
import 'package:super_qr_reader/scan_view.dart';

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
                String results = await Navigator.push( //waiting for the scan results
                  context,
                  MaterialPageRoute(
                    builder: (context) => ScanView(), // open the can view
                  ),
                );

                if (results != null) {
                  setState(() {
                    data = results;
                  });
                }
              },
              child: Text("扫码/tap to scan"),
            ),
            Text(data), // display the scan results
          ],
        ),
      ),
    );
  }
} 
```

### For IOS
Opt-in to the embedded views preview by adding a boolean property to the app's Info.plist file with the key io.flutter.embedded_views_preview and the value YES.

	<key>io.flutter.embedded_views_preview</key>
	<string>YES</string>

And you will need provide the description of camera's permission to work properly, otherwise will crash your app.
``` 
  <key>NSCameraUsageDescription</key>
	<string>The porpuse explaining why you will use the camera</string>
```

