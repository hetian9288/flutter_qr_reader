//
//  ReReaderViewController.m
//  flutter_qr_reader
//
//  Created by 王贺天 on 2019/6/7.
//

#import "QrReaderViewController.h"

@interface QrReaderViewController()<AVCaptureMetadataOutputObjectsDelegate>
@property (nonatomic, strong) AVCaptureSession *captureSession;
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *videoPreviewLayer;
@end

@implementation QrReaderViewController{
    UIView* _qrcodeview;
    int64_t _viewId;
    FlutterMethodChannel* _channel;
    NSObject<FlutterPluginRegistrar>* _registrar;
    NSNumber *height;
    NSNumber *width;
    BOOL isOpenFlash;
    BOOL _isReading;
    AVCaptureDevice *captureDevice;
}

- (instancetype)initWithFrame:(CGRect)frame
               viewIdentifier:(int64_t)viewId
                    arguments:(id _Nullable)args
              binaryRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar
{
    if ([super init]) {
        _registrar = registrar;
        _viewId = viewId;
        NSString *channelName = [NSString stringWithFormat:@"me.hetian.flutter_qr_reader.reader_view_%lld", viewId];
        _channel = [FlutterMethodChannel methodChannelWithName:channelName binaryMessenger:registrar.messenger];
        __weak __typeof__(self) weakSelf = self;
        [_channel setMethodCallHandler:^(FlutterMethodCall* call, FlutterResult result) {
            [weakSelf onMethodCall:call result:result];
        }];
        width = args[@"width"];
        height = args[@"height"];
        NSLog(@"%@,%@", width, height);
        _qrcodeview= [[UIView alloc] initWithFrame:CGRectMake(0, 0, width.floatValue, height.floatValue) ];
        _qrcodeview.opaque = NO;
        _qrcodeview.backgroundColor = [UIColor blackColor];
        isOpenFlash = NO;
        _isReading = NO;
    }
    return self;
}

- (void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result
{
    if ([call.method isEqualToString:@"flashlight"]) {
        [self setFlashlight];
    }else if ([call.method isEqualToString:@"startCamera"]) {
        [self startReading];
    } else if ([call.method isEqualToString:@"stopCamera"]) {
        [self stopReading];
    }
}

- (nonnull UIView *)view {
    return _qrcodeview;
}

- (BOOL)startReading {
    if (_isReading) return NO;
    _isReading = YES;
    NSError *error;
    _captureSession = [[AVCaptureSession alloc] init];
    captureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:captureDevice error:&error];
    if (!input) {
        NSLog(@"%@", [error localizedDescription]);
        return NO;
    }
    [_captureSession addInput:input];
    AVCaptureMetadataOutput *captureMetadataOutput = [[AVCaptureMetadataOutput alloc] init];
    [_captureSession addOutput:captureMetadataOutput];
    dispatch_queue_t dispatchQueue;
    dispatchQueue = dispatch_queue_create("myQueue", NULL);
    [captureMetadataOutput setMetadataObjectsDelegate:self queue:dispatchQueue];
    [captureMetadataOutput setMetadataObjectTypes:[NSArray arrayWithObject:AVMetadataObjectTypeQRCode]];
    _videoPreviewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:_captureSession];
    [_videoPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    [_videoPreviewLayer setFrame:_qrcodeview.layer.bounds];
    [_qrcodeview.layer addSublayer:_videoPreviewLayer];
    [_captureSession startRunning];
    return YES;
}


-(void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection{
    if (metadataObjects != nil && [metadataObjects count] > 0) {
        AVMetadataMachineReadableCodeObject *metadataObj = [metadataObjects objectAtIndex:0];
        if ([[metadataObj type] isEqualToString:AVMetadataObjectTypeQRCode]) {
            NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
            [dic setObject:[metadataObj stringValue] forKey:@"text"];
            [_channel invokeMethod:@"onQRCodeRead" arguments:dic];
            [self performSelectorOnMainThread:@selector(stopReading) withObject:nil waitUntilDone:NO];
            _isReading = NO;
        }
    }
}


-(void)stopReading{
    [_captureSession stopRunning];
    _captureSession = nil;
    [_videoPreviewLayer removeFromSuperlayer];
    _isReading = NO;
}

// 手电筒开关
- (void) setFlashlight
{
    [captureDevice lockForConfiguration:nil];
    if (isOpenFlash == NO) {
        [captureDevice setTorchMode:AVCaptureTorchModeOn];
        isOpenFlash = YES;
    } else {
        [captureDevice setTorchMode:AVCaptureTorchModeOff];
        isOpenFlash = NO;
    }
    
    [captureDevice unlockForConfiguration];
}

@end

@implementation QrReaderViewFactory{
    NSObject<FlutterPluginRegistrar>* _registrar;
}
- (instancetype)initWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar
{
    self = [super init];
    if (self) {
        _registrar = registrar;
    }
    return self;
}

- (NSObject<FlutterMessageCodec>*)createArgsCodec {
    return [FlutterStandardMessageCodec sharedInstance];
}

- (NSObject<FlutterPlatformView>*)createWithFrame:(CGRect)frame
                                   viewIdentifier:(int64_t)viewId
                                        arguments:(id _Nullable)args
{
    QrReaderViewController* viewController = [[QrReaderViewController alloc] initWithFrame:frame
                                                                            viewIdentifier:viewId
                                                                                 arguments:args
                                                                           binaryRegistrar:_registrar];
    return viewController;
}
@end

