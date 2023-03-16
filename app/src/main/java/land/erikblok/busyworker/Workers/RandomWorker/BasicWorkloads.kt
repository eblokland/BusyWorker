package land.erikblok.busyworker.Workers.RandomWorker

import land.erikblok.busyworker.Workers.AbstractWorkload


const val CHECK_INTERVAL = 1000000

/**
 * Basic workload for the workload classes.
 * Using inline here forces kotlin to inline this workload into the classes, which
 * will make it visible when using stack sampling.
 *
 * @param runtime amount of time in milliseconds to run for
 * @param pleaseDontDedupeMe prevents this method from being deduped, allowing it
 * to show up in stack traces
 */
private inline fun work(runtime: Int, pleaseDontDedupeMe: Int): Int{
    var meaningless: Int = 0
    val endTime = System.nanoTime() + (runtime * 1e6)
    while (true) {
        if(meaningless % CHECK_INTERVAL == 0){
            if (System.nanoTime() > endTime) return meaningless
        }
        meaningless += pleaseDontDedupeMe
    }
}

//these are all copies that won't get optimized out.

class Workload1(val runtime: Int) : AbstractWorkload {
    /** Toy workload that will, hopefully, load the ALU relatively hard.
     * Every so often (defined by CHECK_INTERVAL) it will check if it needs to exit.
     * CHECK_INTERVAL is set to something that should check relatively often, but not so often
     * that it spends significant time stalling to wait for System.nanoTime()
    */
    override fun work() {
        work(runtime, 1)
    }
}

class Workload2(val runtime: Int) : AbstractWorkload {
    override fun work() {
        work(runtime, 2)
    }
}

class Workload3(val runtime: Int) : AbstractWorkload {
    override fun work() {
        work(runtime, 3)
    }
}

class Workload4(val runtime: Int) : AbstractWorkload {
    override fun work() {
        work(runtime, 4)
    }
}

class Workload5(val runtime: Int) : AbstractWorkload {
    override fun work() {
        work(runtime, 5)
    }
}

class Workload6(val runtime: Int) : AbstractWorkload {
    override fun work() {
        work(runtime, 6)
    }
}