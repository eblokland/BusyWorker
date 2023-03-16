package land.erikblok.busyworker.Workers.ABTests.InternalSetter;

public class InternalSetterWorkload extends AbstractInternalSetterWorkload {
    private int anInt = 0;

    public InternalSetterWorkload(int numIterations) {
        super(numIterations);
    }

    private void setAnInt(int newInt){
        anInt = newInt;
    }

    private int getAnInt(){
        return anInt;
    }

    @Override
    public void work(){
        for(int i = 0; i < iterations; i++){
            setAnInt(getAnInt() + 1);
        }
    }
}
