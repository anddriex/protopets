package com.eapp.protopets

import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_first.*
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.lang.Exception
import java.nio.FloatBuffer

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AnalyzeImageFragment : AbstractCameraxFragment<AnalysisResult>() {
    private var moduleAssetName: String? = null
    private var module: Module? = null

    private var analyzeImageErrorState = false
    private var inputTensorBuffer: FloatBuffer? = null
    private var inputTensor: Tensor? = null

    companion object {
        private const val TAG = "ProtoPets"
        private const val INPUT_TENSOR_WIDTH: Long = 224
        private const val INPUT_TENSOR_HEIGHT: Long = 224
        private const val TOP_K = 3
        private const val MOVING_AVG_PERIOD = 10
        private const val FORMAT_MS = "%dms"
        private const val FORMAT_AVG_MS = "avg:%.0fms"

        private const val FORMAT_FPS = "%.1fFPS"
        const val SCORES_FORMAT = "%.2f"
    }

    override fun getContentViewLayoutId(): Int {
        return R.layout.fragment_first
    }

    override fun getCameraPreviewView(): PreviewView {
        return viewFinder
    }

    override fun applyToUiAnalyzeImageResult(result: AnalysisResult) {
        Log.d(TAG, "applying analysis image results ${result.analysisDuration.toString()}")
        resultText.text = result.analysisDuration.toString()
    }

    protected fun getModuleAssetName(): String {
        if (!TextUtils.isEmpty(moduleAssetName)) {
            return moduleAssetName!!
        }

        return "resnet18.pt"
    }

    override fun onDestroy() {
        super.onDestroy()
        module?.destroy()
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyzeImage(image: ImageProxy, rotationDegrees: Int): AnalysisResult? {
        if (analyzeImageErrorState) {
            return null
        }

        try {
            if (module == null) {
                val moduleFileAbsolutePath = File(
                    Utils.assetFilePath(requireContext(), getModuleAssetName())!!).absolutePath
                module = Module.load(moduleFileAbsolutePath)
                inputTensorBuffer =
                    Tensor.allocateFloatBuffer( (3 * INPUT_TENSOR_WIDTH * INPUT_TENSOR_HEIGHT).toInt())
                inputTensor = Tensor.fromBlob(inputTensorBuffer, longArrayOf(1, 3, INPUT_TENSOR_HEIGHT, INPUT_TENSOR_WIDTH))

            }

            val startTime = SystemClock.elapsedRealtime()
            TensorImageUtils.imageYUV420CenterCropToFloatBuffer(
                image.image, rotationDegrees,
                INPUT_TENSOR_WIDTH.toInt(), INPUT_TENSOR_HEIGHT.toInt(),
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                inputTensorBuffer, 0)
            val moduleForwardStartTime = SystemClock.elapsedRealtime()
            val outputTensor = module?.forward(IValue.from(inputTensor))?.toTensor()
            val moduleForwardDuration = SystemClock.elapsedRealtime() - moduleForwardStartTime

            val scores = outputTensor?.dataAsFloatArray
            val ixs = Utils.topK(scores, TOP_K)

            val topKClassNames = arrayOfNulls<String>(TOP_K)
            val topKScores = FloatArray(TOP_K)

            for(i in 0 until TOP_K) {
                val ix = ixs[i]
                topKClassNames[i] = Constants.IMAGENET_CLASSES[ix]
                topKScores[i] = scores!![ix]
            }
            val analysisDuration = SystemClock.elapsedRealtime() - startTime
            return AnalysisResult(topKClassNames, topKScores, moduleForwardDuration, analysisDuration)
        } catch (e: Exception) {
            Log.e(TAG, "Error during image analysis", e)
            analyzeImageErrorState = true
            return null
        }
    }
}