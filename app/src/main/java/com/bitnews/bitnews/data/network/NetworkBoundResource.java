package com.bitnews.bitnews.data.network;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public abstract class NetworkBoundResource<T> {
    private Single<APIResponse<T>> response;

    protected NetworkBoundResource() {
        if (shouldFetchFromDB()) {
            response = fetchFromDB()
                    .map(APIResponse::success)
                    .flatMap(dbResponse -> {
                        if (shouldFetchFromAPI(dbResponse.getitem()))
                            return fetchAPIResponse(dbResponse.getitem());
                        return Single.just(dbResponse).onErrorReturn(APIResponse::failed);
                    });
        } else {
            response = fetchAPIResponse(null);
        }

        response = response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<APIResponse<T>> fetchAPIResponse(T dbResponse) {
        return getAPICall()
                .doOnSuccess((apiResponse) -> {
                    if (shouldSaveToDB(apiResponse, dbResponse))
                        saveToDB(apiResponse, dbResponse != null);
                })
                .map(APIResponse::success)
                .onErrorReturn((t) -> handleErrorResponse(t, dbResponse));
    }

    private APIResponse<T> handleErrorResponse(Throwable t, T dbResponse) {
        if (t instanceof HttpException && ((HttpException) t).code() >= 400
                && ((HttpException) t).code() < 500)
            return APIResponse.invalid(t);

        if (shouldReturnDbResponseOnError(dbResponse))
            return APIResponse.success(dbResponse);

        return APIResponse.failed(t);
    }

    public Single<APIResponse<T>> asSingle() {
        return response;
    }

    protected abstract boolean shouldFetchFromDB();

    protected abstract Single<T> fetchFromDB();

    protected abstract boolean shouldFetchFromAPI(T data);

    protected abstract Single<T> getAPICall();

    protected boolean shouldSaveToDB(T apiResponse, T dbResponse) {
        return true;
    }

    protected boolean shouldReturnDbResponseOnError(T dbResponse) {
        return dbResponse != null;
    }

    protected abstract void saveToDB(T item, boolean isUpdate);
}
