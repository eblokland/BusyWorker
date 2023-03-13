package land.erikblok.busyworker

/**
 * Workload to be used by some implementation of AbstractWorker.
 * Since ART might inline stuff, these workloads should all be their own class, even if they
 * aren't actually doing anything different.  Since the workloads should be the same, this abstract class
 * will actually provide a basic workload.
 */
abstract class AbstractWorkload(val runtime: Int) : Thread(){
    abstract fun work()

}

//these are all copies that won't get optimized out.
const val CHECK_INTERVAL = 1000000

class Workload1(runtime: Int) : AbstractWorkload(runtime){
    /** Toy workload that will, hopefully, load the ALU relatively hard.
     * Every so often (defined by CHECK_INTERVAL) it will check if it needs to exit.
     * CHECK_INTERVAL is set to something that should check relatively often, but not so often
     * that it spends significant time stalling to wait for System.nanoTime()
    */
    override fun work() {
        var meaningless: Int = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % CHECK_INTERVAL == 0){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }
}

class Workload2(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        var meaningless: Int = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % CHECK_INTERVAL == 0){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }
}

class Workload3(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        var meaningless: Int = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % CHECK_INTERVAL == 0){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }
}

class Workload4(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        var meaningless: Int = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % CHECK_INTERVAL == 0){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }
}

class Workload5(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        var meaningless: Int = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % CHECK_INTERVAL == 0){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }
}

class Workload6(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        var meaningless: Int = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % CHECK_INTERVAL == 0){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }
}

class SleepWorkload(runtime: Int) : AbstractWorkload(runtime){
    override fun work() {
        Thread.sleep(runtime.toLong())
    }
}