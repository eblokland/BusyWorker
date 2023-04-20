package land.erikblok.busyworker.Workers.ABTests.InternalSetter;

public class PublicInternalSetterWorkload extends AbstractInternalSetterWorkload {
    private int anInt = 0;

    public PublicInternalSetterWorkload(int numIterations) {
        super(numIterations);
    }
    public void setAnInt(int newInt){
        anInt = newInt;
    }
    public int getAnInt(){
        return anInt;
    }

    @Override
    public void work(){
        for(int i = 0; i < iterations; i++){
            setAnInt(getAnInt() + 1);
        }
    }
}
