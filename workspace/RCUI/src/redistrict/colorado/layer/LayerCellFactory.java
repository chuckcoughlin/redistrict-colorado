package redistrict.colorado.layer;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import redistrict.colorado.core.LayerModel;

/**
 * Render a cell in the LayerList.
 */
public class LayerCellFactory implements Callback<ListView<LayerModel>, ListCell<LayerModel>> { 
	 
	    @Override public ListCell<LayerModel> call(ListView<LayerModel> listView) {
	        ListCell<LayerModel> cell = new LayerListCell();
	        return cell;
	    }

}
