#import "FlutterQrReaderPlugin.h"
#import "QrReaderViewController.h"

@implementation FlutterQrReaderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    // 注册原生视图
    QrReaderViewFactory *viewFactory = [[QrReaderViewFactory alloc] initWithRegistrar:registrar];
    [registrar registerViewFactory:viewFactory withId:@"me.hetian.flutter_qr_reader.reader_view"];
    
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"me.hetian.flutter_qr_reader"
                                     binaryMessenger:[registrar messenger]];
    FlutterQrReaderPlugin* instance = [[FlutterQrReaderPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"imgQrCode" isEqualToString:call.method]) {
        [self scanQRCode:call result:result];
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)scanQRCode:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSString *path = call.arguments[@"file"];
    UIImage *image = [UIImage imageWithContentsOfFile:path];
    CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:nil options:@{ CIDetectorAccuracy : CIDetectorAccuracyHigh }];
    
    NSArray *features = [detector featuresInImage:[CIImage imageWithCGImage:image.CGImage]];
    if (features.count > 0) {
        CIQRCodeFeature *feature = [features objectAtIndex:0];
        NSString *qrData = feature.messageString;
        result(qrData);
    } else {
        result(NULL);
    }
}

@end
