#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'flutter_qr_reader'
  s.version          = '0.0.1'
  s.summary          = 'QR code (scan code &#x2F; picture) recognition （AndroidView&#x2F;UiKitView）'
  s.description      = <<-DESC
QR code (scan code &#x2F; picture) recognition （AndroidView&#x2F;UiKitView）
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'

  s.ios.deployment_target = '8.0'
end

