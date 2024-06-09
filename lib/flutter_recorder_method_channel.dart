import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_recorder_platform_interface.dart';

abstract mixin class IRecord {
  void registerCallback(Function() onCancel, Function(String filePath) onSave, Function() onRequestPermission);

  void unregisterCallback();
}

/// An implementation of [FlutterRecorderPlatform] that uses method channels.
class MethodChannelFlutterRecorder extends FlutterRecorderPlatform  {
  MethodChannelFlutterRecorder() {
    _registerCallback();
  }

  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_recorder');

  Function()? onCancel;
  Function(String filePath)? onSave;
  Function()? onRequestPermission;

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  void _registerCallback() {
    methodChannel.setMethodCallHandler((call) async {
      switch (call.method) {
        case "onCancel":
          print('==========>>>>>>>>> on cancel in flutter  ${onCancel == null }');
          if (onCancel != null) {
            onCancel!();
          }
          break;
        case "onSave":
          final path = call.arguments;
          if (onSave != null && path is String && path.isNotEmpty) {
            onSave!(path);
          }
          break;
        case "onRequestPermission":
          if (onRequestPermission != null) {
            onRequestPermission!();
          }
          break;
        default:
          break;
      }
    });
  }


  @override
  void registerCallback(Function() onCancel, Function(String filePath) onSave, Function() onRequestPermission) {
    this.onSave = onSave;
    this.onCancel = onCancel;
    this.onRequestPermission = onRequestPermission;
  }

  @override
  void unregisterCallback() {
    onRequestPermission = null;
    onSave = null;
    onCancel = null;
  }
}
