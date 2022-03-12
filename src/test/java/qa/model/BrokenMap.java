package qa.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BrokenMap<A, B> implements Map<A, B> {

    @Override
    public int size() {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public B get(Object key) {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public B put(A key, B value) {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public B remove(Object key) {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public void putAll(Map<? extends A, ? extends B> m) {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public Set<A> keySet() {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public Collection<B> values() {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

    @Override
    public Set<Entry<A, B>> entrySet() {
        throw new UnsupportedOperationException("BrokenMap for test");
    }

}
