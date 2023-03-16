package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import android.util.Log
import land.erikblok.busyworker.TAG
import land.erikblok.busyworker.constants.USE_AS_RUNTIME
import land.erikblok.busyworker.constants.USE_FIXED
import land.erikblok.busyworker.constants.WORK_AMOUNT

abstract class AbstractABThreadController(
    ctx: Context,
    WLTag: String,
    val workAmount: Int,
    val useAsRuntime: Boolean,
    val useFixed: Boolean
) : AbstractThreadController(ctx, WLTag) {

    companion object {
        @JvmStatic
        protected fun <T : AbstractABThreadController> parseIntent(
            intent: Intent,
            ctor: (Int, Boolean, Boolean) -> T
        ): T? {
            if (!intent.hasExtra(WORK_AMOUNT) || !intent.hasExtra(USE_AS_RUNTIME)
                || !intent.hasExtra(USE_FIXED)
            ) {
                Log.e(TAG, "missing extras for MIM, got " +
                        "work amount: ${intent.hasExtra(WORK_AMOUNT)}" +
                        "UAR: ${intent.hasExtra(USE_AS_RUNTIME)}" +
                        "UF: ${intent.hasExtra(USE_FIXED)}")
                return null
            }

            val workAmount = intent.getIntExtra(WORK_AMOUNT, -1)
            val useFixed = intent.getBooleanExtra(USE_FIXED, false)
            val useAsRuntime = intent.getBooleanExtra(USE_AS_RUNTIME, false)

            return ctor(workAmount, useAsRuntime, useFixed)

        }
    }

    protected fun startThreads(stopCallback: (() -> Unit)?, addThreads: () -> Unit) {
        super.startThreads(stopCallback)
        addThreads()
        threadList.forEach { it.start() }
        if (useAsRuntime) setTimer(workAmount)
    }
}