package com.duongnv.recorder.flutter_recorder

import com.duongnv.recorder.flutter_recorder.record.NativeViewFactory
import com.duongnv.recorder.flutter_recorder.record.RecordView
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterRecorderPlugin */
class FlutterRecorderPlugin : FlutterPlugin, MethodCallHandler, RecordView.Callback {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_recorder")
        channel.setMethodCallHandler(this)
        flutterPluginBinding.platformViewRegistry.registerViewFactory(
            "record_view",
            NativeViewFactory(this)
        )
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onCancel() {
        channel.invokeMethod("onCancel", null)
    }

    override fun onSave(path: String) {
        channel.invokeMethod("onSave", path)
    }

    override fun onRequestPermission() {
        channel.invokeMethod("onRequestPermission", null)
    }
}
