package land.erikblok.busyworker.Workers.SensorWorker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import land.erikblok.busyworker.ThreadController.SensorThreadController
import land.erikblok.busyworker.Workers.AbstractWorker

class SensorWorker private constructor(
    private val sensor: Sensor,
    private val sm: SensorManager,
    private val workload: SensorWorkload,
    private val samplingPeriodUs: Int,
    private val workPeriodMillis: Long,
) : AbstractWorker() {

    @Volatile
    private var stop: Boolean = false
    override fun stopThread() {
        stop = true
    }

    override fun run() {
        sm.registerListener(workload.sensorEventListener, sensor, samplingPeriodUs)
        while (!stop) {
            workload.work()
            sleep(workPeriodMillis)
        }
        sm.unregisterListener(workload.sensorEventListener)
    }

    companion object {
        fun getSensorWorkerForType(
            ctx: Context,
            sensorType: Int,
            useWakeup: Boolean,
            samplingPeriodUs: Int,
            workPeriodMillis: Long,
            numIterations: Int
        ): SensorWorker? {
            val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sm.getDefaultSensor(sensorType, useWakeup)

            if(sensor == null){
                Log.w(SensorThreadController.STC_TAG, "Failed to find sensor for type $sensorType")
                return null
            }

            val workload = when (sensorType){
                Sensor.TYPE_ACCELEROMETER -> AccelerometerWorkload(numIterations)
                else -> return null
            }

            return SensorWorker(sensor, sm, workload, samplingPeriodUs, workPeriodMillis)
        }
    }
}