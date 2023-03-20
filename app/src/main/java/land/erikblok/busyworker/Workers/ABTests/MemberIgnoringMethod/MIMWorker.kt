package land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker


class MIMWorker(useFixed: Boolean, iterations: Int, useAsRuntime: Boolean, outerLoopIterations: Int = 1, onEnd: (() -> Unit)?) :
    AbstractABWorker(iterations, useAsRuntime, onEnd, outerLoopIterations) {

    override val workload = if (useFixed) FixedMemberIgnoringMethodWorkload(numIterations)
    else MemberIgnoringMethodWorkload(numIterations)

}