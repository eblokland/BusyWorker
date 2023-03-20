package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.Workers.ABTests.InternalSetter.InternalSetterWorker

class InternalSetterThreadController(
    ctx: Context,
    workAmount: Int,
    useAsRuntime: Boolean,
    useFixed: Boolean,
    outerLoopIterations: Int
) :
    AbstractABThreadController(ctx, "busyworker:IS", workAmount, useAsRuntime, useFixed, outerLoopIterations) {

    companion object : ThreadControllerBuilderInterface<InternalSetterThreadController> {
        const val ACTION_START_IS = "land.erikblok.action.START_IS"
        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): InternalSetterThreadController? {
            return parseIntent(intent) { workAmount, useAsRuntime, useFixed, outerLoopIterations ->
                InternalSetterThreadController(
                    ctx,
                    workAmount,
                    useAsRuntime,
                    useFixed,
                    outerLoopIterations,
                )
            }
        }

    }

    override fun startThreads(stopCallback: (() -> Unit)?) {
        super.startThreads(stopCallback) {
            threadList.add(InternalSetterWorker(
                workAmount, useAsRuntime, useFixed, outerLoopIterations,
            ) { stopThreads() })
        }
    }
}