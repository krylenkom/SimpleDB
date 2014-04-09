package simpledb;

/**
 * Knows how to compute some aggregate over a set of IntFields without group.
 */
public class IntNoGroupAggregator implements Aggregator {
    private static final TupleDesc TUPLE_DESC = new TupleDesc(new Type[]{Type.INT_TYPE});
    private final int afield;
    private final Op what;
    private int aggvalue;
    private int avgcount;

    public IntNoGroupAggregator(int afield, Op what) {
        this.afield = afield;
        this.what = what;
        switch (this.what) {
            case MIN:
                this.aggvalue = Integer.MAX_VALUE;
                break;
            case MAX:
                this.aggvalue = Integer.MIN_VALUE;
                break;
            case AVG:
                this.avgcount = 0;
                this.aggvalue = 0;
                break;
            default:
                this.aggvalue = 0;
        }
    }

    @Override
    public void merge(Tuple tup) {
        int v = ((IntField) tup.getField(afield)).getValue();
        switch (this.what) {
            case MIN:
                this.aggvalue = Math.min(this.aggvalue, v);
                break;
            case MAX:
                this.aggvalue = Math.max(this.aggvalue, v);
                break;
            case SUM:
                this.aggvalue += v;
                break;
            case COUNT:
                this.aggvalue++;
                break;
            case AVG:
                this.aggvalue += v;
                this.avgcount++;
                break;
        }
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
                    t.setField(0, new IntField(getAggvalue()));
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

    private int getAggvalue() {
        if (Op.AVG == this.what) {
            return ((0 == this.avgcount) ? (int) Double.POSITIVE_INFINITY : this.aggvalue / this.avgcount);
        } else {
            return this.aggvalue;
        }
    }
}
