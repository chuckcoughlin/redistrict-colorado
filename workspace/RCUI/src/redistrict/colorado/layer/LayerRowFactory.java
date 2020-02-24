package redistrict.colorado.layer;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import redistrict.colorado.core.LayerModel;

/**
 * Render a row in the LayerList.
 */
public class LayerRowFactory implements Callback<ListView<LayerModel>, ListCell<LayerModel>> { 
	 
	    @Override public ListCell<LayerModel> call(ListView<LayerModel> listView) {
	        ListCell<LayerModel> cell = new LayerRow();
	        return cell;
	    }

}
