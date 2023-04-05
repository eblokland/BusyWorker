package land.erikblok.busyworker.Workers.ABTests.SlowForLoop;

import android.util.Log;

import java.util.Arrays;

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorkload;

abstract public class AbstractForLoopWorkload extends AbstractABWorkload {
    protected int[] arrayToIterate;
    private static final String TAG = "ABS_FOR_LOOP";
    private static final int MAX_SIZE = 10000000;
    public AbstractForLoopWorkload(int iterations) {
        super(iterations);
        if(this.iterations > MAX_SIZE) {
            Log.w(TAG, "refusing to create a huge array, limiting size to " + MAX_SIZE);
            this.iterations = MAX_SIZE;
        }
        arrayToIterate = new int[this.iterations];
        //i'm sure this won't take forever...
        Arrays.fill(arrayToIterate, 1);
    }


}
