package com.zhuxiangqing.rxjavawithkotlinsample;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by zhuxi on 2017/12/12.
 */

class RxjavaTest {
    public static final String TAG = "RxjavaTest";

    static void create(final Integer... input) {
        //发射器创建发射逻辑
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (Integer i : input
                        ) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: " + integer + "  " + integer.getClass());
            }
        });
    }

    static void map(Integer... input) {
        Observable.fromArray(input)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return String.valueOf(integer);
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "accept: " + s + "  " + s.getClass());
            }
        });
    }


}
