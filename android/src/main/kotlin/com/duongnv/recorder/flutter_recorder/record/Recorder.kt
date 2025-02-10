package com.duongnv.recorder.flutter_recorder.record

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveConfig
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File


const val WAVE_HEADER_SIZE = 44

val Context.recordFile: File
    get() = File(filesDir, "FakeCall_Record_${System.currentTimeMillis()}.3gp")

class Recorder private constructor(context: Context) {

    var onRecording: (() -> Unit)? = null
    var onStop: ((filePath: String) -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onAmpListener: ((Int) -> Unit)? = null
        set(value) {
            recorder.onAmplitudeListener = value
            field = value
        }

    private var startTime: Long = 0
    private var pauseTime: Long = 0
    private val recordingConfig = WaveConfig()
    private val appContext = context.applicationContext
    private lateinit var recorder: WaveRecorder

    var isRecording = false
        private set

    var isPause = false
        private set


    fun init(): Recorder {
        val filePath = appContext.recordFile.toString()
        recorder = WaveRecorder(filePath)
            .apply { waveConfig = recordingConfig }
        recorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> onRecording?.invoke()
                RecorderState.STOP -> onStop?.invoke(filePath)
                RecorderState.PAUSE -> onPause?.invoke()
                else -> {}
            }
        }
        return this
    }


    fun startRecord() {
        Logger.d("============>>>>>>>>>>>>  start recording isPause $isPause")
        Logger.d("============>>>>>>>>>>>>  start recording isRecording $isRecording")
        if (isPause) {
            resumeRecording()
            return
        }
        if (!isRecording) {
            startTime = System.currentTimeMillis()
            recorder.startRecording()
            isRecording = true
        }
    }

    fun resumeRecording() {
        if (!isRecording) {
            isPause = false
            val pauseDuration = System.currentTimeMillis() - pauseTime
            startTime += pauseDuration
            recorder.resumeRecording()
            isRecording = true
        }
    }

    fun stopRecording() {
        isRecording = false
        isPause = false
        recorder.stopRecording()
    }

    fun pauseRecording() {
        if (isRecording) {
            pauseTime = System.currentTimeMillis()
            recorder.pauseRecording()
            isRecording = false
            isPause = true
        }
    }

//    fun toggleRecording() {
//        isRecording = if (!isRecording) {
//            startTime = System.currentTimeMillis()
//            recorder.startRecording()
//            onRecording?.invoke()
//            true
//        } else {
//            recorder.pauseRecording()
//            onStop?.invoke()
//            false
//        }
//    }

    fun getCurrentTime() = System.currentTimeMillis() - startTime

    val bufferSize: Int
        get() = AudioRecord.getMinBufferSize(
            recordingConfig.sampleRate,
            recordingConfig.channels,
            recordingConfig.audioEncoding
        )

    val tickDuration: Int
        get() = (bufferSize.toDouble() * 1000 / byteRate).toInt()

    private val channelCount: Int
        get() = if (recordingConfig.channels == AudioFormat.CHANNEL_IN_MONO) 1 else 2

    private val byteRate: Long
        get() = (bitPerSample * recordingConfig.sampleRate * channelCount / 8).toLong()

    private val bitPerSample: Int
        get() = when (recordingConfig.audioEncoding) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 16
        }

    fun release() {
        isPause = false
        isRecording = false
        onRecording = null
        onStop = null
        onPause = null
        recorder.onAmplitudeListener = null
        recorder.onStateChangeListener = null
        recorder.stopRecording()
    }

    companion object : SingletonHolder<Recorder, Context>(::Recorder)
}