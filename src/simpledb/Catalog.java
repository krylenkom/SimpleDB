package simpledb;

import java.util.*;

/**
 * The Catalog keeps track of all available tables in the database and their
 * associated schemas.
 * For now, this is a stub catalog that must be populated with tables by a
 * user program before it can be used -- eventually, this should be converted
 * to a catalog that reads a catalog table from disk.
 */

public class Catalog {
    private final Map<Integer, Table> tableidMap = new HashMap<Integer, Table>();
    private final Map<String, Table> tablenameMap = new HashMap<String, Table>();

    /**
     * Constructor.
     * Creates a new, empty catalog.
     */
    public Catalog() {
        // empty body
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     *
     * @param file the contents of the table to add;  file.id() is the identfier of
     *             this file/tupledesc param for the calls getTupleDesc and getFile
     * @param t    the format of tuples that are being added
     * @param name the name of the table -- may be an empty string.  May not be null.  If a name
     *             conflict exists, use the last table to be added as the table for a given name.
     */
    public void addTable(DbFile file, TupleDesc t, String name) {
        Table table = new Table(file, t, name);
        this.tableidMap.put(table.getFile().id(), table);
        this.tablenameMap.put(name, table);
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     *
     * @param file the contents of the table to add;  file.id() is the identfier of
     *             this file/tupledesc param for the calls getTupleDesc and getFile
     * @param t    the format of tuples that are being added
     */
    public void addTable(DbFile file, TupleDesc t) {
        addTable(file, t, "");
    }

    /**
     * Return the id of the table with a specified name,
     *
     * @throws NoSuchElementException if the table doesn't exist
     */
    public int getTableId(String name) {
        if (!this.tablenameMap.containsKey(name)) throw new NoSuchElementException();
        return this.tablenameMap.get(name).getFile().id();
    }

    /**
     * Returns the tuple descriptor (schema) of the specified table
     *
     * @param tableid The id of the table, as specified by the DbFile.id()
     *                function passed to addTable
     */
    public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException {
        if (!this.tableidMap.containsKey(tableid)) throw new NoSuchElementException();
        return this.tableidMap.get(tableid).getT();
    }

    /**
     * Returns the DbFile that can be used to read the contents of the
     * specified table.
     *
     * @param tableid The id of the table, as specified by the DbFile.id()
     *                function passed to addTable
     */
    public DbFile getDbFile(int tableid) throws NoSuchElementException {
        if (!this.tableidMap.containsKey(tableid)) throw new NoSuchElementException();
        return this.tableidMap.get(tableid).getFile();
    }

    /**
     * Delete all tables from the catalog
     */
    public void clear() {
        this.tableidMap.clear();
        this.tablenameMap.clear();
    }
}
