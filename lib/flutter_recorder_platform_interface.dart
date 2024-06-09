import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_recorder_method_channel.dart';

abstract class FlutterRecorderPlatform extends PlatformInterface with IRecord {
  /// Constructs a FlutterRecorderPlatform.
  FlutterRecorderPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterRecorderPlatform _instance = MethodChannelFlutterRecorder();

  /// The default instance of [FlutterRecorderPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterRecorder].
  static FlutterRecorderPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterRecorderPlatform] when
  /// they register themselves.
  static set instance(FlutterRecorderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    return _instance.getPlatformVersion();
  }

}
