package land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker


class MIMWorker(variant: MemberIgnoringMethodVariant, iterations: Int, useAsRuntime: Boolean, outerLoopIterations: Int = 1, onEnd: (() -> Unit)?) :
    AbstractABWorker(iterations, useAsRuntime, onEnd, outerLoopIterations) {

    override val workload = when(variant){
        MemberIgnoringMethodVariant.UNFIXED -> MemberIgnoringMethodWorkload(numIterations)
        MemberIgnoringMethodVariant.FIXED -> FixedMemberIgnoringMethodWorkload(numIterations)
    }

}