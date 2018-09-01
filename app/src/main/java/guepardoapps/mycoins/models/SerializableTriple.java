package guepardoapps.mycoins.models;

import java.io.Serializable;
import java.util.Locale;

public class SerializableTriple<K, V, W> implements Serializable {
    private static final String TAG = SerializableTriple.class.getSimpleName();

    private K _key;
    private V _value;
    private W _value2;

    public SerializableTriple(K key, V value, W value2) {
        _key = key;
        _value = value;
        _value2 = value2;
    }

    public K GetKey() {
        return _key;
    }

    public V GetValue() {
        return _value;
    }

    public W GetValue2() {
        return _value2;
    }

    @Override
    public String toString() {
        return String.format(
                Locale.getDefault(),
                "%s: {Key: %s}{Value: %s}{Value2: %s}",
                TAG, _key, _value, _value2);
    }
}
