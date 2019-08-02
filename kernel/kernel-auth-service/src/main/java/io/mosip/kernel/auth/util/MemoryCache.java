package io.mosip.kernel.auth.util;

import org.apache.commons.collections.map.LRUMap;

import lombok.Getter;

/**
 * Local cache to store admin token
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 * @param <K> type of key
 * @param <T> type of value
 */
public class MemoryCache<K, T> {

	private LRUMap cacheMap;

	protected class CacheObject {
		@Getter
		private long lastAccessed = System.currentTimeMillis();
		private T value;

		protected CacheObject(T value) {
			this.value = value;
		}
	}

	public MemoryCache(int maxItems) {
		cacheMap = new LRUMap(maxItems);
	}

	public void put(K key, T value) {
		synchronized (cacheMap) {
			cacheMap.put(key, new CacheObject(value));
		}
	}

	@SuppressWarnings("unchecked")
	public T get(K key) {
		synchronized (cacheMap) {
			CacheObject c = (CacheObject) cacheMap.get(key);
			if (c == null)
				return null;
			else {
				c.lastAccessed = System.currentTimeMillis();
				return c.value;
			}
		}
	}

	public void remove(K key) {
		synchronized (cacheMap) {
			cacheMap.remove(key);
		}
	}

	public int size() {
		synchronized (cacheMap) {
			return cacheMap.size();
		}
	}
}