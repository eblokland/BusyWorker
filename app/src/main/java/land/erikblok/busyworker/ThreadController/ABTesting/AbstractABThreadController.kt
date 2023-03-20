package land.erikblok.busyworker.ThreadController.ABTesting

import android.content.Context
import android.content.Intent
import android.util.Log
import land.erikblok.busyworker.ThreadController.AbstractThreadController
import land.erikblok.busyworker.constants.OUTER_LOOP_ITERATIONS
//import land.erikblok.busyworker.TAG
import land.erikblok.busyworker.constants.USE_AS_RUNTIME
import land.erikblok.busyworker.constants.USE_FIXED
import land.erikblok.busyworker.constants.WORK_AMOUNT

abstract class AbstractABThreadController(
    ctx: Context,
    WLTag: String,
    val workAmount: Int,
    val useAsRuntime: Boolean,
    val useFixed: Boolean,
    val outerLoopIterations: Int
) : AbstractThreadController(ctx, WLTag) {

    companion object {
        protected val ABTAG = "AB_TEST_TC"
        @JvmStatic
        protected fun <T : AbstractABThreadController> parseIntent(
            intent: Intent,
            ctor: (Int, Boolean, Boolean, Int) -> T
        ): T? {
            if (!intent.hasExtra(WORK_AMOUNT) || !intent.hasExtra(USE_AS_RUNTIME)
                || !intent.hasExtra(USE_FIXED)
            ) {
                Log.e(
                    ABTAG, "missing extras for MIM, got " +
                        "work amount: ${intent.hasExtra(WORK_AMOUNT)}" +
                        "UAR: ${intent.hasExtra(USE_AS_RUNTIME)}" +
                        "UF: ${intent.hasExtra(USE_FIXED)}")
                return null
            }

            val workAmount = intent.getIntExtra(WORK_AMOUNT, -1)
            if (workAmount == -1){
                Log.e(ABTAG , "Probably received a long here, bailing as some devices will be really inefficient when given longs.")
                return null
            }
            val useFixed = intent.getBooleanExtra(USE_FIXED, false)
            val useAsRuntime = intent.getBooleanExtra(USE_AS_RUNTIME, false)
            val outerLoopIterations = intent.getIntExtra(OUTER_LOOP_ITERATIONS, 1)

            return ctor(workAmount, useAsRuntime, useFixed, outerLoopIterations)
        }
    }

    protected fun startThreads(stopCallback: (() -> Unit)?, addThreads: () -> Unit) {
        super.startThreads {
            stopCallback?.invoke()
            Log.i(ABTAG, "Thread ended at: ${System.currentTimeMillis()}")
        }
        addThreads()
        threadList.forEach {
            it.start()
        }
        if (useAsRuntime) setTimer(workAmount)
    }

}