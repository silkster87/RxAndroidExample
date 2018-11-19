package com.example.android.rxandroidtutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rx.Observable<String> myObservable = rx.Observable.just("Hello");

        Observer<String> myObserver = new Observer<String>() {
            @Override
            public void onCompleted() {
                //Called when the observable has no more data to emit
            }

            @Override
            public void onError(Throwable e) {
                //Called when the observable encounters an error
            }

            @Override
            public void onNext(String s) {
                //Called each time the observable emits data
                Log.d("MY OBSERVER", s);
            }
        };

        //The onCompleted() and onError() methods in myObserver
        //are often left unused. You have the option of using the Action1 interface
        //which has a single method call()
        Action1<String> myAction = new Action1<String>() {
            @Override public void call(String s) {
                Log.d("My Action", s); }
        };

        //You could also do
        //Subscription sub1 = myObservable.subscribe(myObserver)
        Subscription sub2 = myObservable.subscribe(myAction);

        //To detach an observer from an observable while it is still emitting data
        //you call the  unsubscribe() method on the subscription object
        //sub2.unsubscribe()

        //Emits each item of the array, one at a time
        Observable<String> myArrayStringObservable = Observable.from(new String[]{"One", "Two", "Three"});

        myArrayStringObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d("RXResult", s);
            }
        });

        Observable<Integer> myArrayIntegerObservable = Observable.from(new Integer[]{1,2,3,4,5,6});

        //We can use the map() operator to square the integers
        //The Func1 inteface simulates a lambda operation
        myArrayIntegerObservable.map(new Func1<Integer, Integer>() { //Input and Output are both Integer
            @Override
            public Integer call(Integer integer) {
                return integer * integer; // square the number
            }
        }).skip(2).filter(new Func1<Integer, Boolean>() { // Skip the first 2 numbers
            @Override
            public Boolean call(Integer integer) {
                return integer % 2 == 0; //Only return even numbers - ignore items that return false
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d("Array Integers", String.valueOf(integer));
            }
        });


    }
}
