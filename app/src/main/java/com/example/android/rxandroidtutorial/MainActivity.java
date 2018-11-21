package com.example.android.rxandroidtutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.main_tv);

        //This Observable "myObservable" just emits the String "Hello" but it will not emit data until you call
        //subscribe() on the "myObservable" object.
        rx.Observable<String> myObservable = rx.Observable.just("Hello");

        //When you have an Observable you need an Observer to receive the data that the Observable emits
        //and then it will handle the data accordingly. Here we are just logging the result:
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
            @Override
            public void call(String s) {
                Log.d("My Action", s);
            }
        };

        //You could also do
        //Subscription sub1 = myObservable.subscribe(myObserver)
        //When you use the subscribe() method it returns a Subscription object and the Observable emits data.
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

        Observable<Integer> myArrayIntegerObservable = Observable.from(new Integer[]{1, 2, 3, 4, 5, 6});

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


        //This is a custom Observable using the create() operator. You have to control what it emits
        //by calling the onNext(), onError() and onCompleted() methods yourself.
        rx.Observable<String> fetchFromGoogle = rx.Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(Subscriber<? super String> subscriber) {
                try {
                    String data = fetchData("http://www.google.com"); //Fetching data in the form of a string
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                    }catch(Exception e){
                        subscriber.onError(e); // In case there are network errors
                     }
            }
        });

//        fetchFromGoogle
//                .subscribeOn(Schedulers.newThread()) // Create a new thread so it will get data in background
//                .observeOn(AndroidSchedulers.mainThread()) // Use the UI thread as when we get the string we can update UI
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        Log.d("RXResult", "The resulting length of the parsing action of google.com is: " + s);
//                    }
//                });


    rx.Observable<String> fetchFromYahoo = rx.Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? super String> subscriber) {
            try {
                String data = fetchData("http://www.yahoo.com"); //Fetching data in the form of a string
                subscriber.onNext(data); // Emit the contents of the URL
                subscriber.onCompleted(); // Nothing more to emit
            } catch (Exception e) {
                subscriber.onError(e); // In case there are network errors
            }
        }
    });

//    fetchFromYahoo
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(new Action1<String>() {
//                @Override
//                public void call(String s) {
//                    Log.d("RXRESULT", "Yahoo result: " + s);
//                }
//            });


        //We can combine the fetchFromGoogle and fetchFromYahoo Observables together into a zipped Observable:

        Observable<String> zipped
                = Observable.zip(fetchFromGoogle, fetchFromYahoo, new Func2<String, String, String>() {
            @Override
            public String call(String google, String yahoo) {
                //Do something with the results of both threads
                return google + "\n" + yahoo;
            }
        });


        zipped.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                String result = "The zipped result from Google and Yahoo: " + s;

                mTextView.setText(result); //Update the UI on the main thread
                Log.d("RXRESULT", "The zipped result from Google and Yahoo: " + s);
            }
        });



    }

    private String fetchData(String url) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new URL(url).openStream()));

        String inputLine;
        String result = "";

        while ((inputLine = in.readLine()) != null)
            result += inputLine;

        return result.length() + "";

    }
}