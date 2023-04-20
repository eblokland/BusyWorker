package land.erikblok.busyworker.ThreadController.ABTesting

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.ThreadController.ThreadControllerBuilderInterface
import land.erikblok.busyworker.Workers.ABTests.SlowForLoop.ForLoopVariant
import land.erikblok.busyworker.Workers.ABTests.SlowForLoop.ForLoopWorker

class ForLoopThreadController(
    ctx: Context,
    workAmount: Int,
    useAsRuntime: Boolean,
    private val variant: ForLoopVariant,
    outerLoopIterations: Int
) : AbstractABThreadController(
    ctx,
    "busyworker:ForLoop",
    workAmount,
    useAsRuntime,
    outerLoopIterations
) {
    companion object : ThreadControllerBuilderInterface<ForLoopThreadController> {
        const val ACTION_START_FORLOOP = "land.erikblok.action.START_FORLOOP"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): ForLoopThreadController? {
            return parseIntent(intent) {workAmount, useAsRuntime, variant, outerLoopIterations ->
                ForLoopThreadController(
                    ctx, workAmount, useAsRuntime, ForLoopVariant.intToWorkloadVariant(variant), outerLoopIterations
                )
            }
        }
    }

    override fun startThreads(stopCallback: (() -> Unit)?){
        super.startThreads(stopCallback){
            threadList.add(ForLoopWorker(variant, workAmount, useAsRuntime, outerLoopIterations){
                stopThreads()
            })
        }
    }
}