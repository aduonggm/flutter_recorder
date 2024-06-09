import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_recorder/flutter_recorder.dart';
import 'package:flutter_recorder/flutter_recorder_platform_interface.dart';
import 'package:flutter_recorder/flutter_recorder_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterRecorderPlatform
    with MockPlatformInterfaceMixin
    implements FlutterRecorderPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterRecorderPlatform initialPlatform = FlutterRecorderPlatform.instance;

  test('$MethodChannelFlutterRecorder is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterRecorder>());
  });

  test('getPlatformVersion', () async {
    FlutterRecorder flutterRecorderPlugin = FlutterRecorder();
    MockFlutterRecorderPlatform fakePlatform = MockFlutterRecorderPlatform();
    FlutterRecorderPlatform.instance = fakePlatform;

    expect(await flutterRecorderPlugin.getPlatformVersion(), '42');
  });
}
