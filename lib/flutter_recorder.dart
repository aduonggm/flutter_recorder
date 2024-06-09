
import 'flutter_recorder_platform_interface.dart';

class FlutterRecorder {
  Future<String?> getPlatformVersion() {
    return FlutterRecorderPlatform.instance.getPlatformVersion();
  }
}
