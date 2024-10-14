package coding.toast.batch;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

class SomethingTest {

    @Test
    void test() {
        String testValue = "FirstName ";
        Assert.isTrue(testValue.contains(" "), "the testValue Must contain spaces");
    }

}
