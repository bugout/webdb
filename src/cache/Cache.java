package cache;

public abstract class Cache<Key, Value> {
	public abstract Value get(Key key);
	public abstract void put(Key key, Value value);
}
