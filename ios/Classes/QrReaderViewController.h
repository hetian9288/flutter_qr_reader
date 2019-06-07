//
//  ReReaderViewController.h
//  flutter_qr_reader
//
//  Created by 王贺天 on 2019/6/7.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>
#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
NS_ASSUME_NONNULL_BEGIN

@interface QrReaderViewController : NSObject<FlutterPlatformView>
@end

@interface QrReaderViewFactory : NSObject <FlutterPlatformViewFactory>
- (instancetype)initWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar;
@end

NS_ASSUME_NONNULL_END
