package com.vinpin.network.rxjava;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 管理订阅
 *
 * @author vinpin
 *         create at 2018/03/20 9:20
 */
public class RxApiManager implements RxManager<String> {

    private Map<String, CompositeDisposable> map;

    private RxApiManager() {
        map = new HashMap<>();
    }

    public static RxApiManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final RxApiManager sInstance = new RxApiManager();
    }

    @Override
    public void add(String tag, Disposable disposable) {
        if (tag == null && disposable == null) {
            return;
        }
        Set<String> keySet = map.keySet();
        if (keySet.contains(tag)) {
            CompositeDisposable compositeDisposable = map.get(tag);
            compositeDisposable.add(disposable);
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            map.put(tag, compositeDisposable);
        }
    }

    @Override
    public void remove(String tag) {
        if (tag == null) {
            return;
        }
        if (!map.isEmpty()) {
            map.remove(tag);
        }
    }

    @Override
    public void clear(String tag) {
        if (tag == null) {
            return;
        }
        Set<String> keySet = map.keySet();
        if (keySet.contains(tag)) {
            CompositeDisposable compositeDisposable = map.get(tag);
            compositeDisposable.clear();
            remove(tag);
        }
    }

    @Override
    public void clearAll() {
        Set<String> keySet = map.keySet();
        for (String tag : keySet) {
            clear(tag);
        }
    }
}
