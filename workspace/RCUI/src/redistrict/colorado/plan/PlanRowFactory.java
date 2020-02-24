package redistrict.colorado.plan;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import redistrict.colorado.core.PlanModel;

/**
 * Render a row in the LayerList.
 */
public class PlanRowFactory implements Callback<ListView<PlanModel>, ListCell<PlanModel>> {  
	    @Override public ListCell<PlanModel> call(ListView<PlanModel> listView) {
	        ListCell<PlanModel> cell = new PlanRow();
	        return cell;
	    }

}
