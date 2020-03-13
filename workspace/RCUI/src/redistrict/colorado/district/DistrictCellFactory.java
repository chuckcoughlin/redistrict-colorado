package redistrict.colorado.district;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import redistrict.colorado.ui.GuiUtil;

/**
 * Render cell for the tree.
 */
public class DistrictCellFactory implements Callback<TreeView<String>,TreeCell<String>>{
	private static final GuiUtil guiu = new GuiUtil();
	private final Node openFolderIcon;
    private final Node closedFolderIcon;
    
	public DistrictCellFactory() {
		openFolderIcon = guiu.loadImage("images/folder.png");
		closedFolderIcon = guiu.loadImage("images/folder_closed.png");
	}
	@Override
	public TreeCell<String> call(TreeView<String> p) {
		return new TextFieldTreeCell();
	}


	public final class TextFieldTreeCell extends TreeCell<String> {

		private TextField textField;

		public TextFieldTreeCell() {
		}

		@Override
		public void startEdit() {
			super.startEdit();

			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText((String) getItem());
			setGraphic(getTreeItem().getGraphic());
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(textField.getText());
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
}
