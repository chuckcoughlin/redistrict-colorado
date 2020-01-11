package redistrict.colorado.layer;

import org.openjump.feature.AttributeType;
import javafx.util.StringConverter;

public class FeatureAttributeStringConverter extends StringConverter<String> {
	private final AttributeType type;
	 
	public FeatureAttributeStringConverter(AttributeType t) {
		this.type = t;
	}

	@Override
	public String fromString(String string) {
		return string;
	}

	@Override
	public String toString(String string) {
		return string;
	}

}
