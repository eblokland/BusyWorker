package land.erikblok.busyworker.ThreadController.ABTesting

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.ThreadController.ThreadControllerBuilderInterface
import land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod.MIMWorker
import land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod.MemberIgnoringMethodVariant

class MemberIgnoringMethodThreadController(
    ctx: Context,
    workAmount: Int,
    useAsRuntime: Boolean,
    private val variant: MemberIgnoringMethodVariant,
    outerLoopIterations: Int
) : AbstractABThreadController(ctx, "busyworker:MIM", workAmount, useAsRuntime, outerLoopIterations) {

    companion object : ThreadControllerBuilderInterface<MemberIgnoringMethodThreadController> {
        const val ACTION_START_MIM = "land.erikblok.action.START_MIM"
        const val ACTION_STOP_MIM = "land.erikblok.action.STOP_MIM"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): MemberIgnoringMethodThreadController? {
            return parseIntent(intent) { workAmount, useAsRuntime, variant, outerLoopIterations ->
                MemberIgnoringMethodThreadController(
                    ctx,
                    workAmount,
                    useAsRuntime,
                    MemberIgnoringMethodVariant.intToWorkloadVariant(variant),
                    outerLoopIterations,
                )
            }
        }

    }

    override fun startThreads(stopCallback: (() -> Unit)?) {
        super.startThreads(stopCallback) { threadList.add(MIMWorker(variant, workAmount, useAsRuntime, outerLoopIterations) { stopThreads() }) }
    }
}