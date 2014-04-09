package simpledb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Knows how to compute some aggregate over a set of StringFields and group by field.
 */
public class StringGroupAggregator implements Aggregator {
    private final int gbfield;
    private final TupleDesc tupleDesc;
    private final Map<Field, Integer> aggcounts;

    public StringGroupAggregator(int gbfield, Type gbfieldtype) {
        this.gbfield = gbfield;
        this.tupleDesc = new TupleDesc(
                new Type[]{ gbfieldtype, Type.INT_TYPE },
                new String[] { "" + gbfield, "count_" + gbfield });
        this.aggcounts = new HashMap<Field, Integer>();
    }

    @Override
    public void merge(Tuple tup) {
        Field field = tup.getField(gbfield);
        Integer old = aggcounts.get(field);
        if (null == old) {
            this.aggcounts.put(field, 1);
        } else {
            this.aggcounts.put(field, old + 1);
        }
    }

    @Override
    public DbIterator iterator() {
        return new AbstractDbIterator() {
            private Iterator<Field> inner;

            @Override
            protected Tuple readNext() throws DbException, TransactionAbortedException {
                Tuple t = new Tuple(tupleDesc);
                if (inner.hasNext()) {
                    Field field = inner.next();
                    t.setField(0, field);
                    t.setField(1, new IntField(getAggvalue(field)));
                    return t;
                } else {
                    return null;
                }
            }

            @Override
            public void open() throws DbException, TransactionAbortedException {
                this.inner = aggcounts.keySet().iterator();
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                open();
            }

            @Override
            public TupleDesc getTupleDesc() {
                return tupleDesc;
            }
        };
    }

    @Override
    public TupleDesc getTupleDesc() {
        return this.tupleDesc;
    }

    private int getAggvalue(Field field) {
        return this.aggcounts.get(field);
    }
}
