package guepardoapps.mycoins.basic.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableList<T> implements Serializable {
    private static final long serialVersionUID = -7933419990581963085L;

    private ArrayList<T> _dataList;

    public SerializableList() {
        _dataList = new ArrayList<>();
    }

    public void addValue(T newValue) {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
        }
        _dataList.add(newValue);
    }

    public void setValue(int index, T value) {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
        }
        if (index >= getSize()) {
            _dataList.add(value);
            return;
        }
        _dataList.set(index, value);
    }

    public void setFirstValue(T value) {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
            return;
        }
        for (int index = getSize(); index > -1; index--) {
            if (index + 1 >= getSize()) {
                _dataList.add(getValue(index));
            } else {
                _dataList.set(index + 1, getValue(index));
            }
        }
        _dataList.set(0, value);
    }

    public void removeValue(T removeValue) {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
            return;
        }
        _dataList.remove(removeValue);
    }

    public void removeValue(int index) {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
            return;
        }
        _dataList.remove(index);
    }

    public boolean HasValue(T checkValue) {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
            return false;
        }

        for (int index = 0; index < getSize(); index++) {
            if (checkValue == getValue(index)) {
                return true;
            }
        }

        return false;
    }

    public T getValue(int index) {
        if (index >= getSize()) {
            return null;
        }
        return _dataList.get(index);
    }

    public int getSize() {
        if (_dataList == null) {
            return -1;
        }
        return _dataList.size();
    }

    public int getIndex(T entry) {
        for (int index = 0; index < getSize(); index++) {
            if (entry == getValue(index)) {
                return index;
            }
        }

        return -1;
    }

    public void clear() {
        if (_dataList == null) {
            _dataList = new ArrayList<>();
            return;
        }
        _dataList.clear();
    }

    public String toString() {
        if (_dataList == null) {
            return "-1";
        }

        String response = "";
        for (int index = 0; index < getSize(); index++) {
            response += getValue(index).toString();
        }

        return response;
    }
}
