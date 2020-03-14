package redistrict.colorado.district;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import redistrict.colorado.ui.GuiUtil;

/**
 * Render cell for the tree.
 */
public class DistrictCellFactory implements Callback<TreeView<String>,TreeCell<String>>{
	private final static String CLSS = "DistrictCellFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);
	private static final GuiUtil guiu = new GuiUtil();
	private final Node openFolderIcon;
    private final Node closedFolderIcon;
    
	public DistrictCellFactory() {
		openFolderIcon = guiu.loadImage("images/folder.png");
		closedFolderIcon = guiu.loadImage("images/folder_closed.png");
	}
	@Override
	public TreeCell<String> call(TreeView<String> p) {
		LOGGER.info(String.format("%s.call", CLSS));
		return new TextFieldTreeCell();
	}


	public final class TextFieldTreeCell extends TreeCell<String> {

		public TextFieldTreeCell() {
		}


		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if(item!=null) {
				if (empty) {
					setText(null);
					setGraphic(null);
				}
				else {
					LOGGER.info(String.format("%s.updateItem %s %s", CLSS, item, (empty?"EMPTY":"NOT EMPTY")));
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
}
