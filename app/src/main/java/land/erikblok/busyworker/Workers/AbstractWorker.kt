package land.erikblok.busyworker.Workers

abstract class AbstractWorker : Thread() {
    abstract fun stopThread()
}