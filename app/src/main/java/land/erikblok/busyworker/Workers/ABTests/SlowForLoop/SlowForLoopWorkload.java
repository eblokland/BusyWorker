package land.erikblok.busyworker.Workers.ABTests.SlowForLoop;

public class SlowForLoopWorkload extends AbstractForLoopWorkload{
    public SlowForLoopWorkload(int iterations) {
        super(iterations);
    }

    public int pleaseDontOptimizeMe = 0;
    @Override
    public void work() {
        int meaningless = 0;
        for(int i = 0; i < this.iterations; i++){
            meaningless += this.arrayToIterate[i];
        }
        this.pleaseDontOptimizeMe = meaningless;
    }
}
