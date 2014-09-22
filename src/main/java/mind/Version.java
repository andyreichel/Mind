package mind;

import java.util.Map;

final class Version<K, V> implements Map.Entry<K, V> {
    private final K versionKey;
    private V versionDate;

    public Version(K key, V value) {
        this.versionKey = key;
        this.versionDate = value;
    }


	public K getKey() {
		return versionKey;
	}


	public V setValue(V versionDate) {
		this.versionDate = versionDate;
		return versionDate;
	}


	public V getValue() {
		return versionDate;
	}

}