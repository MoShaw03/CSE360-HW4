package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for the set of {@link Review} instances associated with a question or answer. 
 * This class provides mutable collection operations and exposes the reviews as a list to callers.
 */

public class Reviews {
    private final List<Review> items = new ArrayList<>();

    public void add(Review r) { items.add(r); }
    public void addAll(java.util.Collection<Review> rs) { items.addAll(rs); }
    public List<Review> all() { return Collections.unmodifiableList(items); }
    public void clear() { items.clear(); }
    public int size() { return items.size(); }
    public boolean isEmpty() { return items.isEmpty(); }
}

