package simpledb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Knows how to compute some aggregate over a set of IntFields and group by field.
 */
public class IntGroupAggregator implements Aggregator {
    private final int gbfield;
    private final TupleDesc tupleDesc;
    private final int afield;
    private final Op what;
    private final Map<Field, Integer> aggvalues;
    private final Map<Field, Integer> avgcounts;

    public IntGroupAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.gbfield = gbfield;
        this.tupleDesc = new TupleDesc(
                new Type[]{ gbfieldtype, Type.INT_TYPE },
                new String[] { "" + gbfield, Aggregate.aggName(what) + "_" + gbfield });
        this.afield = afield;
        this.what = what;
        this.aggvalues = new HashMap<Field, Integer>();
        this.avgcounts = new HashMap<Field, Integer>();
    }

    @Override
    public void merge(Tuple tup) {
        Field field = tup.getField(gbfield);
        Integer old = aggvalues.get(field);
        int v = ((IntField) tup.getField(afield)).getValue();
        if (null == old) {
            switch (this.what) {
                case AVG:
                    this.avgcounts.put(field, 1);
                    this.aggvalues.put(field, v);
                    break;
                case COUNT:
                    this.aggvalues.put(field, 1);
                    break;
                default:
                    this.aggvalues.put(field, v);
                    break;
            }
        } else {
            switch (this.what) {
                case MIN:
                    this.aggvalues.put(field, Math.min(old, v));
                    break;
                case MAX:
                    this.aggvalues.put(field, Math.max(old, v));
                    break;
                case SUM:
                    this.aggvalues.put(field, old + v);
                    break;
                case COUNT:
                    this.aggvalues.put(field, old + 1);
                    break;
                case AVG:
                    this.aggvalues.put(field, old + v);
                    this.avgcounts.put(field, this.avgcounts.get(field) + 1);
                    break;
            }

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
                this.inner = aggvalues.keySet().iterator();
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
        if (Op.AVG == this.what) {
            int count = this.avgcounts.get(field);
            return ((0 == count) ? (int) Double.POSITIVE_INFINITY : this.aggvalues.get(field) / count);
        } else {
            return this.aggvalues.get(field);
        }
    }
}
