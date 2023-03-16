package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod.MIMWorker

class MemberIgnoringMethodThreadController(
    ctx: Context,
    workAmount: Int,
    useAsRuntime: Boolean,
    useFixed: Boolean
) : AbstractABThreadController(ctx, "busyworker:MIM", workAmount, useAsRuntime, useFixed) {

    companion object : ThreadControllerBuilderInterface<MemberIgnoringMethodThreadController> {
        const val ACTION_START_MIM = "land.erikblok.action.START_MIM"
        const val ACTION_STOP_MIM = "land.erikblok.action.STOP_MIM"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): MemberIgnoringMethodThreadController? {
            return parseIntent(intent) { workAmount, useAsRuntime, useFixed ->
                MemberIgnoringMethodThreadController(
                    ctx,
                    workAmount,
                    useAsRuntime,
                    useFixed,
                )
            }
        }

    }

    override fun startThreads(stopCallback: (() -> Unit)?) {
        super.startThreads(stopCallback) { threadList.add(MIMWorker(useFixed, workAmount, useAsRuntime)) }
    }
}