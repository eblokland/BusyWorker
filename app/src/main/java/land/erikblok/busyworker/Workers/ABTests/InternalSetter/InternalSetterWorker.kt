package land.erikblok.busyworker.Workers.ABTests.InternalSetter

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker

class InternalSetterWorker(workAmount: Int, useAsRuntime: Boolean, useFixed: Boolean, outerLoopIterations: Int = 1, onEnd: (() -> Unit)?) :
    AbstractABWorker(workAmount, useAsRuntime, onEnd, outerLoopIterations) {
    override val workload =
        if (useFixed) FixedInternalSetterWorkload(numIterations)
        else InternalSetterWorkload(numIterations)

}