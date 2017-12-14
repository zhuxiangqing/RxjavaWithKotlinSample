package com.zhuxiangqing.rxjavawithkotlinsample;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ContentFragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentFragment = (ContentFragment) getSupportFragmentManager().findFragmentByTag("content");
        if (null == contentFragment) {
            contentFragment = ContentFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_content, contentFragment, "content")
                    .commit();
        }


        final List<Student> students = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Course> courseList = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                courseList.add(new Course("course" + i + j));
            }
            students.add(new Student("student" + i, courseList));
        }
        final Integer[] test = {10, 20, 30, 40, 50, 60};
        final Integer[] test0 = {11, 21, 31, 41, 51, 61, 71};
        final Integer[] testCopy = {10, 20, 30, 40, 50, 60, 99};
//        Create  static 方法
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxjavaTest.create(test);
            }
        });

        //Map   not static
        findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxjavaTest.map(test0);
            }
        });

        //Zip static
        findViewById(R.id.zip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable
                        .zip(
                                Observable.fromArray(test),
                                Observable.fromArray(test0)
                                        .map(new Function<Integer, String>() {
                                            @Override
                                            public String apply(Integer integer) throws Exception {
                                                return integer + "";
                                            }
                                        }),
                                new BiFunction<Integer, String, String>() {
                                    @Override
                                    public String apply(Integer integer, String s) throws Exception {
                                        return s + " " + integer;
                                    }
                                }
                        )
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.e(RxjavaTest.TAG, "accept: " + s + "  " + s.getClass());
                            }
                        });
            }
        });

        //concat static  拼接
        //distinct 和 concat 一起使用
        //就是识别多个Observable对象是否一致
        findViewById(R.id.concat)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        Observable
                                .concat(
                                        Observable.fromArray(test),
                                        Observable
                                                .fromArray(test)
//                                                .map(new Function<Integer, String>() {
//                                                    @Override
//                                                    public String apply(Integer integer) throws Exception {
//                                                        return integer + "";
//                                                    }
//                                                })
                                )
                                .distinct()
                                .subscribe(new Consumer<Serializable>() {
                                    @Override
                                    public void accept(Serializable serializable) throws Exception {
                                        Log.e(RxjavaTest.TAG, "accept: " + serializable.toString());
                                    }
                                });
                    }
                });

        //FLatMap not static
        findViewById(R.id.flatMap)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        Observable
                                .fromIterable(students)
                                .flatMap(new Function<Student, ObservableSource<Course>>() {
                                    @Override
                                    public ObservableSource<Course> apply(Student student) throws Exception {
                                        return Observable.fromIterable(student.getCourseList());
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Course>() {
                                    @Override
                                    public void accept(Course course) throws Exception {
                                        Toast.makeText(MainActivity.this, course.getName(), Toast.LENGTH_SHORT).show();
                                    }
                                });

//                        Observable
//                                .create(new ObservableOnSubscribe<Course>() {
//                                    @Override
//                                    public void subscribe(ObservableEmitter<Course> e) throws Exception {
//                                        for (Student student : students
//                                                ) {
//                                            for (Course course : student.getCourseList()
//                                                    ) {
//                                                e.onNext(course);
//                                            }
//                                        }
//                                        e.onComplete();
//                                    }
//                                })
//                                .subscribeOn(Schedulers.newThread())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Consumer<Course>() {
//                                    @Override
//                                    public void accept(Course course) throws Exception {
//                                        Toast.makeText(MainActivity.this, course.getName(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                    }
                });
        //filter
        findViewById(R.id.filter)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable
                                .concat(
                                        Observable.fromArray(test),
                                        Observable
                                                .fromArray(test0)
                                                .map(new Function<Integer, String>() {
                                                    @Override
                                                    public String apply(Integer integer) throws Exception {
                                                        return integer + "";
                                                    }
                                                })
                                )
                                .filter(new Predicate<Serializable>() {
                                    @Override
                                    public boolean test(Serializable serializable) throws Exception {
                                        return false;
                                    }
                                });
                    }
                });

        //Timer
        findViewById(R.id.timer)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String[] s = {""};
                        Observable
                                .timer(
                                        2,
                                        TimeUnit.SECONDS
                                )
                                .subscribe(new Observer<Long>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        s[0] += aLong;

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {


                                    }
                                });
                    }
                });

        findViewById(R.id.intervel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.zip(
                                Observable.fromArray(test),
                                Observable.interval(1, 1, TimeUnit.SECONDS),
                                new BiFunction<Integer, Long, Integer>() {
                                    @Override
                                    public Integer apply(Integer integer, Long aLong) throws Exception {
                                        return integer;
                                    }
                                })
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Log.e(RxjavaTest.TAG, integer + "  " + System.currentTimeMillis());
                                    }
                                });
                    }
                });
//doOnNext 是在subscribe 之前插入一步操作；
        findViewById(R.id.doOnNext)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.fromArray(test)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Toast.makeText(MainActivity.this, "doOnNext1" + integer, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .doOnNext(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Toast.makeText(MainActivity.this, "doOnNext2" + integer, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Toast.makeText(MainActivity.this, "" + integer, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

        //Skip
        findViewById(R.id.skip)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.fromArray(test)
                                .skip(2, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Log.d(RxjavaTest.TAG, "accept: " + integer);
                                    }
                                });
                    }
                });

        //take 弱水三千 只取一瓢饮
        findViewById(R.id.take)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.fromArray(test)
                                .take(2)
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Log.d(RxjavaTest.TAG, "accept: " + integer);
                                    }
                                });
                    }
                });

        //Just
        findViewById(R.id.just)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.just(test[0])
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {

                                    }
                                });
                    }
                });

        //Single
        findViewById(R.id.single)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //?Single的功能 Observable都能实现吗？
                        //是的；
                        //Single 和 Observable 可以相互转换
                        //  toObservable()
                        Single.just(test[0])
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });
                    }
                });

        //Debounce 去掉发送间隔 过短的项目
        findViewById(R.id.debounce)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //可以通过filter实现debounce的功能吗；
                        Observable
                                .fromArray(test)
                                .debounce(100, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Log.d(RxjavaTest.TAG, "accept: " + integer);
                                    }
                                });
                    }
                });

        //Defer Observable对象延迟创建的过程；
        findViewById(R.id.defer)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.defer(new Callable<ObservableSource<Integer>>() {
                            @Override
                            public ObservableSource<Integer> call() throws Exception {
                                return Observable.fromArray(test);
                            }
                        }).subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {

                            }
                        });
                    }
                });


        //last
        findViewById(R.id.last)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Observable.fromArray(test)
                                .last(2)
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Log.e(RxjavaTest.TAG, "accept: " + integer);
                                    }
                                });
                    }
                });

        findViewById(R.id.merge)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
    }


}
