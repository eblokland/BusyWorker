package land.erikblok.busyworker.Worker

abstract class AbstractWorker : Thread() {
    abstract fun stopThread()
}