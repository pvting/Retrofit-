package com.example.retrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    String BASE_URL = "http://www.mocky.io/";
    String name = "5d2469082f000059702418e2";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SimpleRetrofit();
        rxRetrofit();

    }


    private void SimpleRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();

        GithubService simpleService = retrofit.create(GithubService.class);

        Call<GithubUserBean> call = simpleService.getUserString(name);

        call.enqueue(new Callback<GithubUserBean>() {
            @Override
            public void onResponse(Call<GithubUserBean> call, Response<GithubUserBean> response) {
                GithubUserBean bean = response.body();
                setUserView(bean);

            }

            @Override
            public void onFailure(Call<GithubUserBean> call, Throwable t) {
            }
        });
    }

    private void setUserView(GithubUserBean user) {
        if (user != null) {
            Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "result is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void rxRetrofit(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();

        GithubService simpleService = retrofit.create(GithubService.class);
        final Call<GithubUserBean> call = simpleService.getUserString(name);
        final Observable myObserable = Observable.create(new Observable.OnSubscribe<GithubUserBean>() {
            @Override
            public void call(Subscriber<? super GithubUserBean> subscriber) {
                Response<GithubUserBean> bean = null;
                try {
                    String name = Thread.currentThread().getName();
                    bean = call.execute();
                    subscriber.onNext(bean.body());

                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

                subscriber.onCompleted();
            }
        }) ;

        myObserable
                .subscribeOn(Schedulers.io())  //使用线程池
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<GithubUserBean, GithubUserBean>() {
                    @Override
                    public GithubUserBean call(GithubUserBean o) {
                        String name = Thread.currentThread().getName();
                        return o;
                    }
                })
                .subscribe(new Subscriber<GithubUserBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(GithubUserBean o) {
                        String name = Thread.currentThread().getName();
                        setUserView(o);
                    }
                });

    }
}

