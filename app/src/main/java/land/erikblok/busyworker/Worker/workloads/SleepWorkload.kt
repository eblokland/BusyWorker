package land.erikblok.busyworker.Worker.workloads

import land.erikblok.busyworker.AbstractWorkload

class SleepWorkload(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        Thread.sleep(runtime.toLong())
    }
}