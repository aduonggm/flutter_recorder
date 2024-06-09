package com.duongnv.recorder.flutter_recorder.record

import android.util.Log
import com.duongnv.recorder.flutter_recorder.BuildConfig
import com.google.gson.Gson

class Logger {
    companion object {
        fun d(log: Any) {
            if (BuildConfig.DEBUG) {
                when (log) {
                    is String -> Log.i("duongnv", "d: ===========>>>>>>>>  $log")
                    else -> Log.i("duongnv", "===========>>>>>>>> " + Gson().toJson(log))
                }
            }
        }

        fun d(tag: String, log: Any?) {
//            if (BuildConfig.DEBUG) {
                when (log) {
                    is String -> Log.i("duongnv", "===========>>>>>>>> $tag $log")
                    else -> Log.i("duongnv", "===========>>>>>>>> $tag ${Gson().toJson(log)}")
//                }
            }
        }
    }
}