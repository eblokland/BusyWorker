package land.erikblok.busyworker.Workers.ABTests.SlowForLoop

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker

class ForLoopWorker(
    useFixed: Boolean,
    workAmount: Int,
    useAsRuntime: Boolean,
    outerLoop: Int = 1,
    onEnd: (() -> Unit)?
) : AbstractABWorker(workAmount, useAsRuntime, onEnd, outerLoop) {

    override val workload = if (useFixed) FixedSlowForLoopWorkload(numIterations)
    else SlowForLoopWorkload(numIterations)
}