package redistrict.colorado.data;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import redistrict.colorado.core.DatasetModel;

/**
 * Render a row in the LayerList.
 */
public class DatasetRowFactory implements Callback<ListView<DatasetModel>, ListCell<DatasetModel>> { 
	 
	    @Override public ListCell<DatasetModel> call(ListView<DatasetModel> listView) {
	        ListCell<DatasetModel> cell = new DatasetRow();
	        return cell;
	    }

}
