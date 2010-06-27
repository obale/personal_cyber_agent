import to.networld.cyberagent.communication.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TestTestClass {

        @Test public void returnStringTest() {
                final String testString = "Testing String...";
                TestClass obj = new TestClass(testString);
                assertEquals(obj.getTestString(), testString);
        }

}
