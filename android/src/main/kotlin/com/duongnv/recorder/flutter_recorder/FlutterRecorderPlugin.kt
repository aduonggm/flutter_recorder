package com.duongnv.recorder.flutter_recorder

import android.util.Log
import com.duongnv.recorder.flutter_recorder.record.NativeViewFactory
import com.duongnv.recorder.flutter_recorder.record.RecordView
import com.duongnv.recorder.flutter_recorder.record.Recorder
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
    private var recorder: Recorder? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_recorder")
        channel.setMethodCallHandler(this)
        flutterPluginBinding.platformViewRegistry.registerViewFactory(
            "record_view",
            NativeViewFactory(this)
        )
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "dispose" -> {
                RecordView.isCancel = true
                recorder?.stopRecording()
                Log.d(
                    "duonvnd",
                    "on dispose widget : recorder?.isPause: ${recorder?.isPause}   recorder?.isRecording:${recorder?.isRecording} "
                )
            }

            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Log.d("nvd", "onDetachedFromEngine: ")
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

    override fun onCreateRecorder(recorder: Recorder) {
        this.recorder = recorder
    }
}
