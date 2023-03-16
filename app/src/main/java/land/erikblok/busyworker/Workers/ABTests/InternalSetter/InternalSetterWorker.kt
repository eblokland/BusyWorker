package land.erikblok.busyworker.Workers.ABTests.InternalSetter

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker

class InternalSetterWorker(workAmount: Int, useAsRuntime: Boolean, useFixed: Boolean) :
    AbstractABWorker(workAmount, useAsRuntime) {
    override val workload =
        if (useFixed) FixedInternalSetterWorkload(numIterations)
        else InternalSetterWorkload(numIterations)
}