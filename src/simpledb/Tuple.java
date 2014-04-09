package simpledb;

/**
 * Tuple maintains information about the contents of a tuple.
 * Tuples have a specified schema specified by a TupleDesc object and contain
 * Field objects with the data for each field.
 */
public class Tuple {
    private RecordID rid; // RecordID information for this tuple
    private final TupleDesc td; // specified schema
    private final Field[] fieldAr; // tuple fields

    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td the schema of this tuple. It must be a valid TupleDesc
     *           instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        assert (td != null);
        this.td = td;
        this.fieldAr = new Field[td.numFields()];
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        return this.td;
    }

    /**
     * @return The RecordID representing the location of this tuple on
     *         disk. May be null.
     */
    public RecordID getRecordID() {
        return this.rid;
    }

    /**
     * Set the RecordID information for this tuple.
     *
     * @param rid the new RecordID for this tuple.
     */
    public void setRecordID(RecordID rid) {
        this.rid = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i index of the field to change. It must be a valid index.
     * @param f new value for the field.
     */
    public void setField(int i, Field f) {
        this.fieldAr[i] = f;
    }

    /**
     * @param i field index to return. Must be a valid index.
     * @return the value of the ith field, or null if it has not been set.
     */
    public Field getField(int i) {
        return this.fieldAr[i];
    }

    /**
     * Returns the contents of this Tuple as a string.
     * Note that to pass the system tests, the format needs to be as
     * follows:
     * <p/>
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * <p/>
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        StringBuilder toString = new StringBuilder();
        boolean isFirst = true;
        for (Field field : this.fieldAr) {
            if (!isFirst) {
                toString.append("\t");
            }
            toString.append(field);
            isFirst = false;
        }
        return toString + "\n";
    }
}
