/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.databinding;

import android.support.v4.util.ArrayMap;

import java.util.Collection;

public class ObservableArrayMap<K, V> extends ArrayMap<K, V> implements ObservableMap<K, V> {

    private MapChangeRegistry mListeners;

    @Override
    public void addOnMapChangedListener(
            OnMapChangedListener<? extends ObservableMap<K, V>, K> listener) {
        if (mListeners == null) {
            mListeners = new MapChangeRegistry();
        }
        mListeners.add(listener);
    }

    @Override
    public void removeOnMapChangedListener(
            OnMapChangedListener<? extends ObservableMap<K, V>, K> listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void clear() {
        boolean wasEmpty = isEmpty();
        if (!wasEmpty) {
            super.clear();
            notifyChange(null);
        }
    }

    public V put(K k, V v) {
        V val = super.put(k, v);
        notifyChange(k);
        return v;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean removed = false;
        for (Object key : collection) {
            int index = indexOfKey(key);
            if (index >= 0) {
                removed = true;
                removeAt(index);
            }
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean removed = false;
        for (int i = size() - 1; i >= 0; i--) {
            Object key = keyAt(i);
            if (!collection.contains(key)) {
                removeAt(i);
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public V removeAt(int index) {
        K key = keyAt(index);
        V value = super.removeAt(index);
        if (value != null) {
            notifyChange(key);
        }
        return value;
    }

    @Override
    public V setValueAt(int index, V value) {
        K key = keyAt(index);
        V oldValue = super.setValueAt(index, value);
        notifyChange(key);
        return oldValue;
    }

    private void notifyChange(Object key) {
        if (mListeners != null) {
            mListeners.notifyCallbacks(this, 0, key);
        }
    }
}