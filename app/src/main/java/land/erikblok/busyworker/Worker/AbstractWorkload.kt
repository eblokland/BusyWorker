package land.erikblok.busyworker

/**
 * Workload to be used by some implementation of AbstractWorker.
 * Since ART might inline stuff, these workloads should all be their own class, even if they
 * aren't actually doing anything different.  Since the workloads should be the same, this abstract class
 * will actually provide a basic workload.
 */
abstract class AbstractWorkload(val runtime: Long) : Thread(){
    open fun work() {
        var meaningless: Long = 0
        val endTime = System.nanoTime() + (runtime * 1e6)
        while (true) {
            if(meaningless % 1000000 == 0L){
                if (System.nanoTime() > endTime) return
            }
            meaningless += 1
        }
    }

}

//these are all copies that HOPEFULLY won't get optimized

class Workload1(runtime: Long) : AbstractWorkload(runtime)

class Workload2(runtime: Long) : AbstractWorkload(runtime)

class Workload3(runtime: Long) : AbstractWorkload(runtime)

class SleepWorkload(runtime: Long) : AbstractWorkload(runtime){
    override fun work() {
        Thread.sleep(runtime)
    }
}