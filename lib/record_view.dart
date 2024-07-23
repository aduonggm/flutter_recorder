import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

import 'flutter_recorder_platform_interface.dart';

class RecordView extends StatefulWidget {
  const RecordView({
    super.key,
    required this.onCancel,
    required this.onSave,
    required this.onRequestPermission,
    this.touchToStart = "Touch to start record",
    this.cancel = "Cancel",
    this.save = "Save",
    this.delete = "Delete",
    this.touchToResume = "Tap to resume",
    this.touchToPause = "Tap to pause recording",
  });

  // <string name="txt_tap_to_start_record">Chạm để bắt đầu ghi âm</string>
  // <string name="txt_cancel">Cancel</string>
  // <string name="txt_save">Save</string>
  // <string name="txt_tap_to_pause_recording">Tap to pause recording</string>
  // <string name="txt_delete">Delete</string>
  // <string name="txt_tap_to_resume">Tap to resume</string>

  final Function() onCancel;
  final Function(String filePath) onSave;
  final Function() onRequestPermission;
  final String touchToStart;
  final String cancel;
  final String save;
  final String delete;
  final String touchToResume;
  final String touchToPause;

  @override
  State<RecordView> createState() => _RecordViewState();
}

class _RecordViewState extends State<RecordView> {
  late Map<String, dynamic> creationParams = <String, dynamic>{
    "touchToStart": widget.touchToStart,
    "tapToPause": widget.touchToPause,
    "tapToResume": widget.touchToResume,
    "cancel": widget.cancel,
    "save": widget.save,
    "delete": widget.delete,
  };

  @override
  void initState() {
    super.initState();
    FlutterRecorderPlatform.instance.registerCallback(widget.onCancel, widget.onSave, widget.onRequestPermission);
  }

  @override
  void dispose() {
    super.dispose();
    FlutterRecorderPlatform.instance.unregisterCallback();
  }

  @override
  Widget build(BuildContext context) {
    // This is used in the platform side to register the view.
    const String viewType = 'record_view';
    // Pass parameters to the platform side.

    // save = json[""]!!,
    // tapToPause = json[""]!!,
    // delete = json[""]!!,
    // tapToResume = json[""]!!

    return PlatformViewLink(
      viewType: viewType,
      surfaceFactory: (context, controller) {
        return AndroidViewSurface(
          controller: controller as AndroidViewController,
          gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
          hitTestBehavior: PlatformViewHitTestBehavior.opaque,
        );
      },
      onCreatePlatformView: (params) {
        return PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: viewType,
          layoutDirection: TextDirection.ltr,
          creationParams: creationParams,
          creationParamsCodec: const StandardMessageCodec(),
          onFocus: () {
            params.onFocusChanged(true);
          },
        )
          ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
          ..create();
      },
    );
  }
}
