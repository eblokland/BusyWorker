package land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorker


class MIMWorker(useFixed: Boolean, iterations: Int, useAsRuntime: Boolean) :
    AbstractABWorker(iterations, useAsRuntime) {

    override val workload = if (useFixed) FixedMemberIgnoringMethodWorkload(numIterations)
    else MemberIgnoringMethodWorkload(numIterations)

}