package simpledb;

/**
 * Knows how to compute some aggregate over a set of StringFields without group.
 */
public class StringNoGroupAggregator implements Aggregator {
    private static final TupleDesc TUPLE_DESC = new TupleDesc(new Type[]{Type.INT_TYPE});
    private int aggvalue;

    public StringNoGroupAggregator() {
        this.aggvalue = 0;
    }

    @Override
    public void merge(Tuple tup) {
        this.aggvalue++;
    }

    @Override
    public DbIterator iterator() {
        return new AbstractDbIterator() {
            private boolean read;

            @Override
            protected Tuple readNext() throws DbException, TransactionAbortedException {
                if (this.read) {
                    return null;
                } else {
                    this.read = true;
                    Tuple t = new Tuple(TUPLE_DESC);
                    t.setField(0, new IntField(aggvalue));
                    return t;
                }
            }

            @Override
            public void open() throws DbException, TransactionAbortedException {
                this.read = false;
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                open();
            }

            @Override
            public TupleDesc getTupleDesc() {
                return TUPLE_DESC;
            }
        };
    }

    @Override
    public TupleDesc getTupleDesc() {
        return TUPLE_DESC;
    }
}
