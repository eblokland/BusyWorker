package land.erikblok.busyworker.ThreadController.ABTesting

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.ThreadController.ThreadControllerBuilderInterface
import land.erikblok.busyworker.Workers.ABTests.InternalSetter.InternalSetterWorker
import land.erikblok.busyworker.Workers.ABTests.InternalSetter.InternalSetterWorkloadVariant

class InternalSetterThreadController(
    ctx: Context,
    workAmount: Int,
    useAsRuntime: Boolean,
    private val variant: InternalSetterWorkloadVariant,
    outerLoopIterations: Int
) :
    AbstractABThreadController(ctx, "busyworker:IS", workAmount, useAsRuntime, outerLoopIterations) {

    companion object : ThreadControllerBuilderInterface<InternalSetterThreadController> {
        const val ACTION_START_IS = "land.erikblok.action.START_IS"
        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): InternalSetterThreadController? {
            return parseIntent(intent) { workAmount, useAsRuntime, variant, outerLoopIterations ->

                InternalSetterThreadController(
                    ctx,
                    workAmount,
                    useAsRuntime,
                    InternalSetterWorkloadVariant.intToWorkloadVariant(variant),
                    outerLoopIterations,
                )
            }
        }

    }

    override fun startThreads(stopCallback: (() -> Unit)?) {
        super.startThreads(stopCallback) {
            threadList.add(InternalSetterWorker(
                workAmount, useAsRuntime, variant, outerLoopIterations,
            ) { stopThreads() })
        }
    }
}