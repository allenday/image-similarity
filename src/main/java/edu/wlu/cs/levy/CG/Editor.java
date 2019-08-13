package edu.wlu.cs.levy.CG;


public interface Editor<T> {
    T edit(T current) throws KeyDuplicateException;

    abstract class BaseEditor<T> implements Editor<T> {
        final T val;

        BaseEditor(T val) {
            this.val = val;
        }

        public abstract T edit(T current) throws KeyDuplicateException;
    }

    class Inserter<T> extends BaseEditor<T> {
        Inserter(T val) {
            super(val);
        }

        public T edit(T current) throws KeyDuplicateException {
            if (current == null) {
                return this.val;
            }
            throw new KeyDuplicateException();
        }
    }

    class OptionalInserter<T> extends BaseEditor<T> {
        OptionalInserter(T val) {
            super(val);
        }

        public T edit(T current) {
            return (current == null) ? this.val : current;
        }
    }

    class Replacer<T> extends BaseEditor<T> {
        Replacer(T val) {
            super(val);
        }

        public T edit(T current) {
            return this.val;
        }
    }
}