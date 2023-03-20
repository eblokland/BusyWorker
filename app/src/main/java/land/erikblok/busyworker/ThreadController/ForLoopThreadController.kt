package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.Workers.ABTests.SlowForLoop.ForLoopWorker

class ForLoopThreadController(
    ctx: Context,
    workAmount: Int,
    useAsRuntime: Boolean,
    useFixed: Boolean,
    outerLoopIterations: Int
) : AbstractABThreadController(
    ctx,
    "busyworker:ForLoop",
    workAmount,
    useAsRuntime,
    useFixed,
    outerLoopIterations
) {
    companion object : ThreadControllerBuilderInterface<ForLoopThreadController> {
        const val ACTION_START_FORLOOP = "land.erikblok.action.START_FORLOOP"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): ForLoopThreadController? {
            return parseIntent(intent) {workAmount, useAsRuntime, useFixed, outerLoopIterations ->
                ForLoopThreadController(
                    ctx, workAmount, useAsRuntime, useFixed, outerLoopIterations
                )
            }
        }
    }

    override fun startThreads(stopCallback: (() -> Unit)?){
        super.startThreads(stopCallback){
            threadList.add(ForLoopWorker(useFixed, workAmount, useAsRuntime, outerLoopIterations){
                stopThreads()
            })
        }
    }
}