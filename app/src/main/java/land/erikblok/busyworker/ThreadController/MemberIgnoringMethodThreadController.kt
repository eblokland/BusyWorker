package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import android.util.Log
import land.erikblok.busyworker.TAG
import land.erikblok.busyworker.Workers.MemberIgnoringMethod.MIMWorker
import land.erikblok.busyworker.constants.ITERATIONS
import land.erikblok.busyworker.constants.RUNTIME
import land.erikblok.busyworker.constants.USE_FIXED

class MemberIgnoringMethodThreadController(
    ctx: Context,
    val workAmount: Int,
    val useAsRuntime: Boolean,
    val useFixed: Boolean
) : AbstractThreadController(ctx, "busyworker:MIM") {

    companion object : ThreadControllerBuilderInterface<MemberIgnoringMethodThreadController> {
        const val ACTION_START_MIM = "land.erikblok.action.START_MIM"
        const val ACTION_STOP_MIM = "land.erikblok.action.STOP_MIM"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): MemberIgnoringMethodThreadController? {
            val hasIt = intent.hasExtra(ITERATIONS)
            if ((!hasIt && !intent.hasExtra(RUNTIME))
                || !intent.hasExtra(USE_FIXED)
            ) {
                Log.e(TAG, "missing extras for MIM")
                return null
            }

            val iterations = if (hasIt) intent.getIntExtra(ITERATIONS, -1)
            else intent.getIntExtra(RUNTIME, Int.MIN_VALUE)

            val useFixed = intent.getBooleanExtra(USE_FIXED, false)

            return MemberIgnoringMethodThreadController(ctx, iterations, !hasIt, useFixed)
        }

    }

    override fun startThreads(stopCallback: (() -> Unit)?) {
        cleanUpThreads()
        threadList.add(MIMWorker(useFixed, workAmount, useAsRuntime))
        threadList.forEach { it.start() }
        if (useAsRuntime) setTimer(workAmount)
        startThreads(stopCallback)
    }
}