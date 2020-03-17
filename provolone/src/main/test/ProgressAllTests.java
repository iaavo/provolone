import org.jenkinsci.testinprogress.runner.ProgressSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(ProgressSuite.class)
@SuiteClasses({ SimpleFESTTest.class })
public class ProgressAllTests {

}
