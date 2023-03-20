package land.erikblok.busyworker.Workers.SensorWorker

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class AccelerometerWorkload(numIterations: Int) : SensorWorkload(numIterations) {


    //i think that the sensor event will come from a different thread
    //we should ensure that the data is updated.
    @Volatile
    private var sensorOutput: FloatArray = floatArrayOf(0f,0f,0f)

    override var sensorEventListener = object: SensorEventListener{
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return
            sensorOutput = event.values.clone()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            //i don't really care what the accuracy is
        }

    }

    override fun work() {
        //do some hard math with the sensor values
        var meaningless = 0f
        for(i in 0 until numIterations){
            // the FPU will hate me for doing this.
            sensorOutput.forEach {
                meaningless += it
            }

        }
    }
}