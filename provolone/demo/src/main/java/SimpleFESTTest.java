import java.awt.GraphicsEnvironment;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JInternalFrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleFESTTest {

    private FrameFixture ff;
    private SwingSet2 swingset;

    @Before
    public void setUp() throws InterruptedException {
        GuiActionRunner.execute(new GuiTask() {
			@Override
            protected void executeInEDT() throws Throwable {
            	swingset = new SwingSet2(null, GraphicsEnvironment.
                        getLocalGraphicsEnvironment().
                        getDefaultScreenDevice().
                        getDefaultConfiguration());
            }
        });
        ff = new FrameFixture(swingset.getFrame());
    }

    @After
    public void tearDown() throws Exception {
        ff.cleanUp();
    }

    @Test
    @GUITest
    public void testInternalFrames() {
        ff.toggleButton(Matcher.toggleButton("JInternalFrame demo")).click();
        
        ff.checkBox(Matcher.checkBox("Resizable")).click();
        ff.checkBox(Matcher.checkBox("Maximizable")).click();
        ff.textBox(Matcher.textField("Frame")).deleteText().enterText("Provolone");
        ff.button(Matcher.button("cab_small.gif").contains()).click();
        new JInternalFrameFixture(ff.robot, ff.robot.finder().find(Matcher.internalFrame("Provolone").contains())).close();
	}
	
    @Test
    @GUITest
	public void testButtons() {
		ff.toggleButton(Matcher.toggleButton("JButton, JRadioButton, JToggleButton, JCheckbox demos")).click();
		
		ff.radioButton(Matcher.radioButton("0")).click();
		ff.button(Matcher.button("b1.gif").contains()).click();
		ff.button(Matcher.button("Three!").contains()).click();
		ff.tabbedPane(Matcher.tabbedPane("Check Boxes")).selectTab("Check Boxes");
		ff.checkBox(Matcher.checkBox("cb.gif").contains()).click();
	}
	
    @Test
    @GUITest
    public void testColorChooser() {
		for (int i = 0; i < 10; i++) {
			ff.toggleButton(Matcher.toggleButton("JColorChooser demo")).click();
		}
	}
}
