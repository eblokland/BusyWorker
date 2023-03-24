package land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod;

public class FixedMemberIgnoringMethodWorkload extends AbstractMemberIgnoringMethodWorkload {

    public FixedMemberIgnoringMethodWorkload(int workAmount) {
        super(workAmount);
    }

    @Override
    public void work() {
            for (int i = 0; i < iterations; i++) {
                lonelyMember = fixedMemberIgnoringMethod(lonelyMember);
            }
    }

    private static int fixedMemberIgnoringMethod(int input) {
        return input + 1;
    }
}
