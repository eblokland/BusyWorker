package land.erikblok.busyworker.Workers.MemberIgnoringMethod;


/**
 * This class contains a member ignoring method code smell.
 * In order to control for the Kotlin compiler optimizing this out, it has been written in Java.
 */
public class MemberIgnoringMethodWorkload extends AbstractMemberIgnoringMethodWorkload {
    public MemberIgnoringMethodWorkload(int workAmount) {
        super(workAmount);
    }

    @Override
    public void work() {
        for (int i = 0; i < workAmount; i++) {
            lonelyMember = memberIgnoringMethod(lonelyMember);
        }
    }

    private int memberIgnoringMethod(int input) {
        return input + 1;
    }
}


