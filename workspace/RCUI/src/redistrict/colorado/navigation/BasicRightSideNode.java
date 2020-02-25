package redistrict.colorado.navigation;

import javafx.scene.layout.AnchorPane;
import redistrict.colorado.bind.EventBindingHub;
import redistrict.colorado.ui.DisplayOption;
import redistrict.colorado.ui.ViewMode;

public abstract class BasicRightSideNode extends AnchorPane {
	public static final long serialVersionUID = 5234544498000865338L;
	protected final EventBindingHub hub;
	private final DisplayOption option;
	private final ViewMode mode;
	
	public BasicRightSideNode(ViewMode vm, DisplayOption dopt) {
		this.hub = EventBindingHub.getInstance();
		this.option = dopt;
		this.mode = vm;
	}
	
	public ViewMode getMode() { return this.mode; }
	public DisplayOption getOption() { return this.option; }
	/**
	 * Query the hub for the current model and/or feature of the appropriate type.
	 */
	public abstract void updateModel();
}
