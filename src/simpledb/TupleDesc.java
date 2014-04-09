package simpledb;

import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {
    private final Type[] typeAr; // array specifying the number of and types of fields
    private final String[] fieldAr; // array specifying the names of the fields
    private final int numFields; // the number of fields
    private final int size; // The size (in bytes) of tuples
    private final String description; // a String describing this descriptor

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields
     * fields, with the first td1.numFields coming from td1 and the remaining
     * from td2.
     *
     * @param td1 The TupleDesc with the first fields of the new TupleDesc
     * @param td2 The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc combine(TupleDesc td1, TupleDesc td2) {
        assert (td1 != null);
        assert (td2 != null);
        Type[] typeAr = new Type[td1.numFields() + td2.numFields()];
        System.arraycopy(td1.typeAr, 0, typeAr, 0, td1.numFields());
        System.arraycopy(td2.typeAr, 0, typeAr, td1.numFields(), td2.numFields());
        String[] fieldAr = new String[td1.numFields() + td2.numFields()];
        if (td1.fieldAr != null) {
            System.arraycopy(td1.fieldAr, 0, fieldAr, 0, td1.numFields());
        }
        if (td2.fieldAr != null) {
            System.arraycopy(td2.fieldAr, 0, fieldAr, td1.numFields(), td2.numFields());
        }
        return new TupleDesc(typeAr, fieldAr);
    }

    /**
     * Constructor.
     * Create a new tuple desc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr  array specifying the number of and types of fields in
     *                this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        assert (typeAr != null);
        assert (typeAr.length > 0);
        if (fieldAr != null) {
            assert (typeAr.length == fieldAr.length);
        }
        this.typeAr = typeAr;
        this.fieldAr = fieldAr;
        this.numFields = typeAr.length;
        int size = 0;
        int index = 0;
        StringBuilder description = new StringBuilder();
        for (Type type : typeAr) {
            size += type.getLen();
            description.append(type)
                    .append("[")
                    .append(index)
                    .append("](")
                    .append(fieldAr == null || fieldAr[index] == null ? "noname" : fieldAr[index])
                    .append(")");
            index++;
        }
        this.size = size;
        description.append(")");
        this.description = description.toString();
    }

    /**
     * Constructor.
     * Create a new tuple desc with typeAr.length fields with fields of the
     * specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *               this TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        this(typeAr, null);
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return numFields;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (this.fieldAr == null) {
            return "noname";
        }
        try {
            return this.fieldAr[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
        if (this.fieldAr == null) {
            throw new NoSuchElementException();
        }
        int index = 0;
        for (String field : this.fieldAr) {
            if (field == null && name == null) {
                return index;
            }
            if (field != null && field.equals(name)) {
                return index;
            }
            index++;
        }
        // no field with a matching name is found
        throw new NoSuchElementException();
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        try {
            return this.typeAr[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Compares the specified object with this TupleDesc for equality.
     * Two TupleDescs are considered equal if they are the same size and if the
     * n-th type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        return this == o || o instanceof TupleDesc
                && Arrays.equals(this.typeAr, ((TupleDesc) o).typeAr);

    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        return Arrays.hashCode(this.typeAr);
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     *
     * @return String describing this descriptor.
     */
    public String toString() {
        return this.description;
    }
}
