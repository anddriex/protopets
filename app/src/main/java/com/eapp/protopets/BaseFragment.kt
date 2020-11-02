package com.eapp.protopets

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {
    protected var backgroundThread: HandlerThread? = null
    protected var backgroundHandler: Handler? = null
    protected var uIHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uIHandler = Handler(requireContext().mainLooper)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startBackgroundThread()
    }

    protected fun startBackgroundThread() {
        backgroundThread = HandlerThread("BaseFragment")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundHandler!!.looper)
    }

    override fun onDestroy() {
        stopBackgroundThread()
        super.onDestroy()
    }

    protected fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException ) {
            Log.e(TAG, "Error on stopping background thread", e)
        }
    }

    companion object {
        private const val TAG = "BaseFragment"
    }
}