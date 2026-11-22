package com.lab3.sudoku.util

import android.util.Log

object AppLogger {
    private const val TAG = "Sudoku"

    fun d(msg: String) { Log.d(TAG, msg) }
    fun i(msg: String) { Log.i(TAG, msg) }
    fun w(msg: String) { Log.w(TAG, msg) }
    fun e(msg: String, t: Throwable? = null) { Log.e(TAG, msg, t) }
}