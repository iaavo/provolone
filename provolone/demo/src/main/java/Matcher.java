import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;

public class Matcher<T extends JComponent> extends GenericTypeMatcher<T> {

	public static Matcher<JToggleButton> toggleButton(String text) {
		return new Matcher<JToggleButton>(JToggleButton.class, text);
	}

	public static Matcher<JCheckBox> checkBox(String text) {
		return new Matcher<JCheckBox>(JCheckBox.class, text);
	}

	public static Matcher<JRadioButton> radioButton(String text) {
		return new Matcher<JRadioButton>(JRadioButton.class, text);
	}

	public static Matcher<JTextField> textField(String text) {
		return new Matcher<JTextField>(JTextField.class, text);
	}

	public static Matcher<JButton> button(String text) {
		return new Matcher<JButton>(JButton.class, text);
	}

	public static Matcher<JInternalFrame> internalFrame(String text) {
		return new Matcher<JInternalFrame>(JInternalFrame.class, text);
	}

	public static Matcher<JTabbedPane> tabbedPane(String text) {
		return new Matcher<JTabbedPane>(JTabbedPane.class, text);
	}

	private String data;
	private boolean contains = false;
	/**
	 * Makes sure only the first matched component is returned even if there are
	 * more components matching. This is only for demonstrating purposes and
	 * should not be used in real fest tests.
	 */
	private boolean matched = false;

	private Matcher(Class<T> supportedType, String text) {
		super(supportedType);
		this.data = text.trim();
	}

	public Matcher<T> contains() {
		contains = true;
		return this;
	}

	@Override
	protected boolean isMatching(T component) {
		boolean matched = false;
		if (check(component.getToolTipText()))
			matched = true;
		if (check(component.getName()))
			matched = true;
		if (component instanceof JTextComponent) {
			if (check(((JTextComponent) component).getText()))
				matched = true;
		}
		if (component instanceof AbstractButton) {
			if (check(((AbstractButton) component).getText()))
				matched = true;
			if (check(((AbstractButton) component).getIcon()))
				matched = true;
		}
		if (component instanceof JLabel) {
			if (check(((JLabel) component).getText()))
				matched = true;
			if (check(((JLabel) component).getIcon()))
				matched = true;
		}
		if (component instanceof JInternalFrame) {
			if (check(((JInternalFrame) component).getTitle()))
				matched = true;
		}
		if (component instanceof JTabbedPane) {
			JTabbedPane c = (JTabbedPane) component;
			for (int i = 0; i < c.getTabCount(); i++) {
				if (check(c.getTitleAt(i)))
					matched = true;
			}
		}
		return matched;
	}

	private boolean check(Object text) {
		if (text == null || matched)
			return false;
		if (contains) {
			if (text.toString().contains(data)) {
				matched = true;
				return true;
			}
		} else {
			if (text.toString().equals(data)) {
				matched = true;
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return supportedType().getSimpleName() + "<" + data + ">";
	}
}