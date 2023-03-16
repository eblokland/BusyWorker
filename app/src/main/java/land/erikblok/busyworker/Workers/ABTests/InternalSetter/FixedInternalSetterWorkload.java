package land.erikblok.busyworker.Workers.ABTests.InternalSetter;

public class FixedInternalSetterWorkload extends AbstractInternalSetterWorkload{

    private int anInt = 0;

    public FixedInternalSetterWorkload(int numIterations) {
        super(numIterations);
    }

    @Override
    public void work() {
        for(int i = 0; i < iterations; i++){
            // intentionally try to mimic syntax of non-fixed load, probably
            // will compile to same thing as anInt++
           anInt = anInt + 1;
        }
    }
}
