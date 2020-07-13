package com.bitnews.bitnews.data.network;


import android.annotation.SuppressLint;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import retrofit2.HttpException;

public abstract class NetworkBoundResource<T> {
    private PublishSubject<APIResponse<T>> response = PublishSubject.create();

    @SuppressLint("CheckResult")
    protected NetworkBoundResource() {
        if (shouldFetchFromDB()) {
            fetchFromDB()
                    .observeOn(Schedulers.io())
                    .subscribe((dbResponse) -> {
                        if (shouldFetchFromAPI(dbResponse))
                            fetchFromAPI(dbResponse).subscribe(response::onNext);
                        else
                            response.onNext(APIResponse.success(dbResponse));
                    });
        } else {
            fetchFromAPI(null).subscribe(response::onNext);
        }

        response.observeOn(AndroidSchedulers.mainThread());
    }

    private Single<APIResponse<T>> fetchFromAPI(T dbResponse) {
        return getAPICall()
                .map(apiResponse -> {
                    if (shouldSaveToDB(apiResponse, dbResponse))
                        saveToDB(apiResponse, dbResponse != null);

                    return APIResponse.success(apiResponse);
                }).onErrorReturn((t) -> {
                    if (t instanceof HttpException && ((HttpException) t).code() >= 400 && ((HttpException) t).code() < 500)
                        return APIResponse.invalid(t);

                    if (dbResponse != null)
                        return APIResponse.success(dbResponse);

                    return APIResponse.failed(t);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Subject<APIResponse<T>> asSubject() {
        return response;
    }

    protected abstract boolean shouldFetchFromDB();

    protected abstract Single<T> fetchFromDB();

    protected abstract boolean shouldFetchFromAPI(T data);

    protected abstract Single<T> getAPICall();

    protected boolean shouldSaveToDB(T apiResponse, T dbResponse) {
        return !apiResponse.equals(dbResponse);
    }

    protected abstract void saveToDB(T item, boolean isUpdate);
}
