package com.duongnv.recorder.flutter_recorder.record

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.duongnv.recorder.flutter_recorder.R
import com.duongnv.recorder.flutter_recorder.databinding.FragmentRecordingBinding
import io.flutter.plugin.platform.PlatformView
import java.util.concurrent.TimeUnit

@SuppressLint("ViewConstructor")
class RecordView(
    context: Context?,
    private val callback: Callback,
    private val textModel: TextModel
) :
    LinearLayout(context), PlatformView {

    private val binding: FragmentRecordingBinding =
        FragmentRecordingBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initView()
    }

    override fun getView() = this

    override fun dispose() {

    }

    private lateinit var recorder: Recorder

    private val animations = arrayListOf<AnimatorSet>()

    companion object {
        var isCancel = false
    }

    private fun initView() {
        binding.tvCancel.text = textModel.cancel
        binding.tvSave.text = textModel.save
        binding.tvRecordState.text = textModel.touchToStart

        binding.btnRecord.setOnClickListener { record() }
        binding.btnStopRecord.setOnClickListener {
            recorder.pauseRecording()
        }
        binding.btnSave.setOnClickListener {
            Logger.d("========>>>>>>> on save ")

            isCancel = false
            recorder.stopRecording()
        }
        binding.btnCancel.setOnClickListener {
            isCancel = true
            Logger.d("=======>>>>>> run to delete ", recorder.isRecording)
            Logger.d("=======>>>>>> run to delete ", recorder.isPause)
            if (recorder.isRecording || recorder.isPause) {
                recorder.stopRecording()
            } else {
                Logger.d("=========>>>>>>>>>> on cancel")
                callback.onCancel()
            }
        }
        listenOnRecorderStates()
    }


    private fun listenOnRecorderStates() = with(binding) {
        recorder = Recorder.getInstance(context).init().apply {
            onRecording = {
                binding.tvRecordState.text = textModel.tapToPause
//                binding.tvRecordState.text = context.getString(R.string.txt_tap_to_pause_recording)
                binding.btnRecord.visible = false
                binding.btnStopRecord.visible = true
                binding.btnSave.isEnabled = true
                binding.tvSave.isEnabled = true
                binding.tvCancel.text = textModel.delete
                binding.btnRecord.setImageResource(R.drawable.ic_play)
                startWave()
            }
            onPause = {
                stopWave()
                binding.tvRecordState.text = textModel.tapToResume
                binding.btnSave.isEnabled = true
                binding.btnRecord.visible = true
                binding.btnStopRecord.visible = false
                binding.tvCancel.text = textModel.delete

            }
            onStop = { filePath ->
                stopWave()
                binding.btnRecord.setImageResource(R.drawable.ic_mic)
                binding.btnSave.isEnabled = false
                binding.tvRecordState.text = textModel.touchToStart
                visualizer.clear()
                binding.btnRecord.visible = true
                binding.btnStopRecord.visible = false
                binding.tvTimer.text = 0L.formatAsTime()
                binding.tvCancel.text = textModel.cancel
                Logger.d("===========>>>>>>>>. on stop record $filePath   $isCancel")
                if (filePath.isNotEmpty() && !isCancel) {
                    callback.onSave(filePath)
                }
            }
            onAmpListener = {
                post {
                    if (recorder.isRecording) {
                        binding.tvTimer.text = recorder.getCurrentTime().formatAsTime()
                        visualizer.addAmp(it, tickDuration)
                    }
                }
            }
        }
        callback.onCreateRecorder(recorder)
    }

    private fun stopWave() {
        binding.answerAnimation.visible = false
        binding.answerAnimation1.visible = false
        binding.answerAnimation2.visible = false
        binding.answerAnimation3.visible = false
        animations.forEach {
            it.cancel()
        }
        animations.clear()
    }

    private var View.visible: Boolean
        get() = visibility == View.VISIBLE
        set(value) {
            visibility = when (value) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        }

    private fun startWave() {
        animations.add(startAnimationWave(binding.answerAnimation))
        postDelayed({
            animations.add(startAnimationWave(binding.answerAnimation1))
        }, 600)
        postDelayed({
            animations.add(startAnimationWave(binding.answerAnimation2))
        }, 1200)
        postDelayed({
            animations.add(startAnimationWave(binding.answerAnimation3))
        }, 1800)
    }

    private fun startRecording() {
        recorder.startRecord()
    }

    @SuppressLint("DefaultLocale")
    private fun Long.formatAsTime(): String {
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(this) % 60).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(this) % 60).toInt()

        return when (val hours = (TimeUnit.MILLISECONDS.toHours(this)).toInt()) {
            0 -> String.format("%02d:%02d", minutes, seconds)
            else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    private fun record() {
        if (hasPermission()) {
            startRecording()
        } else {
            callback.onRequestPermission()

        }
    }

    private fun hasPermission(): Boolean {

        val result = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        return (result == PackageManager.PERMISSION_GRANTED)
    }

    private fun startAnimationWave(view: View, duration: Long = 2400L): AnimatorSet {
        view.isVisible = true
        val alpha =
            ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f, 1.0f, 0.0f).setDuration(duration)
        alpha.repeatCount = ValueAnimator.INFINITE
        alpha.repeatMode = ValueAnimator.RESTART
        val duration2 = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.0f).setDuration(duration)
        duration2.repeatCount = ValueAnimator.INFINITE
        duration2.repeatMode = ValueAnimator.RESTART
        val duration3 = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f).setDuration(duration)
        duration3.repeatCount = ValueAnimator.INFINITE
        duration3.repeatMode = ValueAnimator.RESTART
        return AnimatorSet().apply {
            playTogether(alpha, duration2, duration3)
            start()
        }
    }

    interface Callback {
        fun onCancel()
        fun onSave(path: String)
        fun onRequestPermission()
        fun onCreateRecorder(recorder: Recorder)
    }
}


data class TextModel(
    val touchToStart: String,
    val cancel: String,
    val save: String,
    val tapToPause: String,
    val delete: String,
    val tapToResume: String
) {

    companion object {

        fun fromJson(json: Map<String, String>): TextModel {
            return TextModel(
                touchToStart = json["touchToStart"]!!,
                cancel = json["cancel"]!!,
                save = json["save"]!!,
                tapToPause = json["tapToPause"]!!,
                delete = json["delete"]!!,
                tapToResume = json["tapToResume"]!!
            )
        }
    }
}