package redistrict.colorado.ui;

public interface TableCellCallback<T> {
	public void update(String columnName, int row, T newValue);
}
