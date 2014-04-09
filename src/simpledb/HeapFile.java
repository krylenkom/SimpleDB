package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection
 * of tuples in no particular order.  Tuples are stored on pages, each of
 * which is a fixed size, and the file is simply a collection of those
 * pages. HeapFile works closely with HeapPage.  The format of HeapPages
 * is described in the HeapPage constructor.
 *
 * @author Sam Madden
 * @see simpledb.HeapPage#HeapPage
 */
public class HeapFile implements DbFile {
    private final File f;
    private int numPages;

    /**
     * Constructor.
     * Creates a new heap file that stores pages in the specified buffer pool.
     *
     * @param f The file that stores the on-disk backing store for this DbFile.
     */
    public HeapFile(File f) {
        this.f = f;
        this.numPages = (int) (f.length() / BufferPool.PAGE_SIZE);
    }

    /**
     * Return a Java File corresponding to the data from this HeapFile on disk.
     */
    public File getFile() {
        return this.f;
    }

    /**
     * @return an ID uniquely identifying this HeapFile
     *         (Implementation note:  you will need to generate this tableid somewhere,
     *         ensure that each HeapFile has a "unique id," and that you always
     *         return the same value for a particular HeapFile.  The implementation we
     *         suggest you use could hash the absolute file name of the file underlying
     *         the heapfile, i.e. f.getAbsoluteFile().hashCode()
     *         )
     */
    public int id() {
        return this.f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns a Page from the file.
     */
    public Page readPage(PageId pid) throws NoSuchElementException {
        int pageSize = bytesPerPage();
        byte[] buf = new byte[pageSize];
        try {
            RandomAccessFile raf = new RandomAccessFile(this.f, "r");
            raf.skipBytes(pid.pageno() * pageSize);
            raf.read(buf);
            HeapPage page = new HeapPage((HeapPageId) pid, buf);
            raf.close();
            return page;
        } catch (IOException e) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Writes the given page to the appropriate location in the file.
     */
    public void writePage(Page page) throws IOException {
        // not necessary for this project
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        return this.numPages;
    }

    /**
     * Adds the specified tuple to the table under the specified TransactionId.
     *
     * @return An ArrayList contain the pages that were modified
     * @throws DbException
     * @throws IOException
     */
    public ArrayList<Page> addTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        ArrayList<Page> result = new ArrayList<Page>();
        for (int i = 0; i < numPages(); ++i) {
            HeapPage page = (HeapPage) Database.getBufferPool()
                    .getPage(tid, new HeapPageId(id(), i), Permissions.READ_WRITE);
            if (page.getNumEmptySlots() > 0) {
                page.addTuple(t);
                result.add(page);
                return result;
            }
        }
        HeapPage page = (HeapPage) Database.getBufferPool()
                .getPage(tid, new HeapPageId(id(), numPages()), Permissions.READ_WRITE);
        this.numPages++;
        page.addTuple(t);
        result.add(page);
        return result;
    }

    /**
     * Deletes the specified tuple from the table, under the specified
     * TransactionId.
     */
    public Page deleteTuple(TransactionId tid, Tuple t)
            throws DbException, TransactionAbortedException {
        if (id() != t.getRecordID().pageid().tableid())
            throw new DbException("not a member of the page");
        HeapPage page = (HeapPage) Database.getBufferPool()
                .getPage(tid, t.getRecordID().pageid(), Permissions.READ_WRITE);
        page.deleteTuple(t);
        return page;
    }

    /**
     * An iterator over all tuples on this file, over all pages.
     * Note that this iterator should use BufferPool.getPage(), rather than HeapFile.getPage()
     * to iterate through pages.
     */
    public DbFileIterator iterator(final TransactionId tid) {
        return new AbstractDbFileIterator() {
            private int index;
            private Iterator<Tuple> inner;

            @Override
            protected Tuple readNext() throws DbException, TransactionAbortedException {
                if (this.inner == null) {
                    return null;
                } else if (this.inner.hasNext()) {
                    return this.inner.next();
                } else {
                    index++;
                    if (index < numPages()) {
                        open();
                        return readNext();
                    } else {
                        return null;
                    }
                }
            }

            @Override
            public void open() throws DbException, TransactionAbortedException {
                HeapPage p = (HeapPage) Database.getBufferPool()
                        .getPage(tid, new HeapPageId(id(), this.index), Permissions.READ_ONLY);
                this.inner = p.iterator();
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                this.index = 0;
                open();
            }

            @Override
            public void close() {
                this.inner = null;
                super.close();
            }
        };
    }

    /**
     * @return the number of bytes on a page, including the number of bytes
     *         in the header.
     */
    public int bytesPerPage() {
        return BufferPool.PAGE_SIZE + (((BufferPool.PAGE_SIZE / Database.getCatalog()
                .getTupleDesc(id()).getSize()) / HeapPage.INTEGER_SIZE) + 1) * 4;
    }
}

