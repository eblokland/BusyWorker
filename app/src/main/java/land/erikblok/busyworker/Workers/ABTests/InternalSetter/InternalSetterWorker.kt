package land.erikblok.busyworker.Workers.ABTests.InternalSetter

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker

class InternalSetterWorker(workAmount: Int, useAsRuntime: Boolean, useFixed: Boolean, onEnd: (() -> Unit)?) :
    AbstractABWorker(workAmount, useAsRuntime, onEnd) {
    override val workload =
        if (useFixed) FixedInternalSetterWorkload(numIterations)
        else InternalSetterWorkload(numIterations)
}