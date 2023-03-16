package land.erikblok.busyworker.Workers.RandomWorker

import land.erikblok.busyworker.Workers.AbstractWorkload

class SleepWorkload(val runtime: Int) : AbstractWorkload {
    override fun work() {
        Thread.sleep(runtime.toLong())
    }
}