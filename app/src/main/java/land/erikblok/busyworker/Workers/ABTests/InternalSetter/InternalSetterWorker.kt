package land.erikblok.busyworker.Workers.ABTests.InternalSetter

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker

class InternalSetterWorker(workAmount: Int, useAsRuntime: Boolean, workerType: InternalSetterWorkloadVariant, outerLoopIterations: Int = 1, onEnd: (() -> Unit)?) :
    AbstractABWorker(workAmount, useAsRuntime, onEnd, outerLoopIterations) {
    override val workload =
        when (workerType){
            InternalSetterWorkloadVariant.PRIVATE_INTERNAL_SETTER -> InternalSetterWorkload(numIterations)
            InternalSetterWorkloadVariant.PUBLIC_INTERNAL_SETTER -> PublicInternalSetterWorkload(numIterations)
            InternalSetterWorkloadVariant.FIXED_INTERNAL_SETTER -> FixedInternalSetterWorkload(numIterations)
        }

}