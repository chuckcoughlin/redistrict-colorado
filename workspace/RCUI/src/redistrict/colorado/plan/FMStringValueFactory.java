package redistrict.colorado.plan;

import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import redistrict.colorado.core.PlanFeature;

public class FMStringValueFactory implements Callback<TableColumn.CellDataFeatures<PlanFeature,String>,ObservableValue<String>> {
	private final static String CLSS = "FMStringValueFactory";
	private static Logger LOGGER = Logger.getLogger(CLSS);

	/** 
	 * Key values off of column names. The only one that is a String is name 
-	 */
	@Override
	public ObservableValue<String> call(CellDataFeatures<PlanFeature, String> cdf) {
		PlanFeature fm = cdf.getValue();
		String name = cdf.getTableColumn().getText();
		//LOGGER.info(String.format("%s.getValue: %s",CLSS,name));
		StringProperty property = new SimpleStringProperty();
		if( name.equalsIgnoreCase("Name")) {
			property.setValue(fm.getName());
		}
		return property;
	}
}
