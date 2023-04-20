package land.erikblok.busyworker.Workers.ABTests.SlowForLoop

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker

class ForLoopWorker(
    variant: ForLoopVariant,
    workAmount: Int,
    useAsRuntime: Boolean,
    outerLoop: Int = 1,
    onEnd: (() -> Unit)?
) : AbstractABWorker(workAmount, useAsRuntime, onEnd, outerLoop) {

    override val workload = when(variant){
        ForLoopVariant.UNFIXED -> SlowForLoopWorkload(numIterations)
        ForLoopVariant.FIXED -> FixedSlowForLoopWorkload(numIterations)
    }
}