package redistrict.colorado.bind;

import javafx.event.ActionEvent;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.ViewMode;

public class LeftSelectionEvent extends ActionEvent {
	private static final long serialVersionUID = 5234543398000865338L;
	private final DisplayOption option;
	private final ViewMode mode;
	
	public LeftSelectionEvent(ViewMode vm, DisplayOption dopt) {
		this.option = dopt;
		this.mode = vm;
	}
	
	public ViewMode getMode() { return this.mode; }
	public DisplayOption getOption() { return this.option; }

}
