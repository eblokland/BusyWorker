package land.erikblok.busyworker.Workers

/**
 * Workload to be used by some implementation of AbstractWorker.
 * Since ART might inline stuff, these workloads should all be their own class, even if they
 * aren't actually doing anything different.  Since the workloads should be the same, this abstract class
 * will actually provide a basic workload.
 */
interface AbstractWorkload{
    fun work()
}

