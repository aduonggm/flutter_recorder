import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_recorder_platform_interface.dart';

/// An implementation of [FlutterRecorderPlatform] that uses method channels.
class MethodChannelFlutterRecorder extends FlutterRecorderPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_recorder');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
