package land.erikblok.busyworker.Workers.ABTests.SlowForLoop;

public class FixedSlowForLoopWorkload extends AbstractForLoopWorkload{
    public FixedSlowForLoopWorkload(int iterations) {
        super(iterations);
    }

    @Override
    public void work() {
        int meaningless = 0;
        for(int i : this.arrayToIterate){
            meaningless += i;
        }
    }
}
