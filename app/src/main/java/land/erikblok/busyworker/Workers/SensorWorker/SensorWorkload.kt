package land.erikblok.busyworker.Workers.SensorWorker

import android.hardware.SensorEventListener
import land.erikblok.busyworker.Workers.AbstractWorkload

abstract class SensorWorkload(protected val numIterations: Int) : AbstractWorkload {

    abstract var sensorEventListener : SensorEventListener
}