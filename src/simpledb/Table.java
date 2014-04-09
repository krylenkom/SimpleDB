package simpledb;

/**
 * The table keeps information about table from Catalog
 */
public class Table {
  private final DbFile file; // the contents of the table
  private final TupleDesc t; // the format of tuples
  private final String name; // the name of the table -- may be an empty string

  public Table(DbFile file, TupleDesc t, String name) {
    assert (file != null);
    assert (t != null);
    assert (name != null);
    this.file = file;
    this.t = t;
    this.name = name;
  }

  public DbFile getFile() {
    return file;
  }

  public TupleDesc getT() {
    return t;
  }

  public String getName() {
    return name;
  }
}
