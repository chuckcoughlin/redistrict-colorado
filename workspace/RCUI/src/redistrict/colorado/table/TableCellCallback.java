package redistrict.colorado.table;

public interface TableCellCallback<T> {
	public void update(String columnName, int row, T newValue);
}
