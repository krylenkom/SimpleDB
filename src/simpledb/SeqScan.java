package simpledb;
import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {
    private final TransactionId tid; // The transaction this scan is running as a part of
    private final int tableid; // the table to scan
    private final String tableAlias; // the alias of this table
    private final DbFileIterator inner; // inner iterator

    /**
     * Constructor.
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     *
     * @param tid The transaction this scan is running as a part of.
     * @param tableid the table to scan.
     * @param tableAlias the alias of this table (needed by the parser);
     *         the returned tupleDesc should have fields with name tableAlias.fieldName
     *         (note: this class is not responsible for handling a case where tableAlias
     *         or fieldName are null.  It shouldn't crash if they are, but the resulting
     *         name can be null.fieldName, tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        this.tid = tid;
        this.tableid = tableid;
        this.tableAlias = tableAlias;
        this.inner = Database.getCatalog().getDbFile(tableid).iterator(tid);
    }

    /**
     * Opens this sequential scan.
     * Needs to be called before getNext().
     */
    public void open()
        throws DbException, TransactionAbortedException {
        this.inner.open();
    }

    /**
     * Implementation of DbIterator.getTupleDesc method.
     * Should return a tupleDesc with field names from the underlying HeapFile with field
     *   names prefaced by the passed in tableAlias string
     */
    public TupleDesc getTupleDesc() {
        return Database.getCatalog().getTupleDesc(tableid);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        return this.inner.hasNext();
    }

    /**
     * Implementation of DbIterator.getNext method.
     * Return the next tuple in the scan, or null if there are no more tuples.
     *
     */
    public Tuple next()
        throws NoSuchElementException, TransactionAbortedException, DbException {
        return this.inner.next();
    }

    /**
     * Closes the sequential scan.
     */
    public void close() {
        this.inner.close();
    }

    /**
     * Rewinds the sequential back to the first record.
     */
    public void rewind()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        this.inner.rewind();
    }
}
