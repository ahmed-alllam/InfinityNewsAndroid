package com.bitnews.bitnews.data.network;


import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.bitnews.bitnews.utils.AppExecutors;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class NetworkBoundResource<ResponseType> {
    private MediatorLiveData<APIResponse<ResponseType>> response = new MediatorLiveData<>();
    private AppExecutors appExecutors = AppExecutors.getInstance();


    public NetworkBoundResource() {
        if (shouldFetchFromDB()) {
            MediatorLiveData<ResponseType> dbResponse = new MediatorLiveData<>();

            appExecutors.getDiskIO().execute(() -> {
                ResponseType response = fetchFromDB();
                appExecutors.getMainThread().execute(() -> dbResponse.setValue(response));
            });

            response.addSource(dbResponse, data -> {
                response.removeSource(dbResponse);
                if (shouldFetchFromAPI(data))
                    fetchFromAPI(dbResponse);
                else
                    response.setValue(new APIResponse<>(data));
            });
        } else {
            fetchFromAPI(new MutableLiveData<>(null));
        }
    }

    private void fetchFromAPI(LiveData<ResponseType> dbResponse) {
        MediatorLiveData<APIResponse<ResponseType>> apiResponse = new MediatorLiveData<>();
        appExecutors.getNetworkIO().execute(() -> {
            APIResponse<ResponseType> callResponse = executeCall(getCall());
            appExecutors.getMainThread().execute(() -> apiResponse.setValue(callResponse));
        });

        response.addSource(apiResponse, callResponse -> {
            response.removeSource(apiResponse);
            response.removeSource(dbResponse);

            switch (callResponse.getStatus()) {
                case SUCCESFUL:
                    if (shouldSaveToDB(callResponse.getitem(), dbResponse.getValue()))
                        appExecutors.getDiskIO().execute(() -> saveToDB(callResponse.getitem(), dbResponse.getValue() != null));
                    response.setValue(callResponse);
                    break;
                case NETWORK_FAILED:
                    if (dbResponse.getValue() != null)
                        response.setValue(new APIResponse<>(dbResponse.getValue()));
                    else
                        response.setValue(callResponse);
                    break;
                case BAD_REQUEST:
                    response.setValue(callResponse);
            }
        });
    }

    @WorkerThread
    private APIResponse<ResponseType> executeCall(Call<ResponseType> call) {
        APIResponse<ResponseType> response = new APIResponse<>();
        APIResponse.Status responseStatus = null;

        try {
            Response<ResponseType> retrofitResponse = call.execute();

            int statusCode = retrofitResponse.code();
            if (statusCode >= 300) {
                String errorBody = "";
                if (retrofitResponse.errorBody() != null) {
                    errorBody = retrofitResponse.errorBody().string();
                }
                response.setError(new Throwable(errorBody));
                responseStatus = statusCode < 500 ? APIResponse.Status.BAD_REQUEST : APIResponse.Status.NETWORK_FAILED;
            } else {
                response.setitem(retrofitResponse.body());
                responseStatus = APIResponse.Status.SUCCESFUL;
            }

        } catch (IOException e) {
            response.setError(e);
            responseStatus = APIResponse.Status.NETWORK_FAILED;
        } finally {
            response.setStatus(responseStatus);
        }

        return response;
    }

    public LiveData<APIResponse<ResponseType>> asLiveData() {
        return response;
    }

    @WorkerThread
    protected abstract void saveToDB(ResponseType item, boolean isUpdate);

    @MainThread
    protected abstract boolean shouldFetchFromDB();

    @MainThread
    protected abstract boolean shouldFetchFromAPI(ResponseType data);

    @MainThread
    protected boolean shouldSaveToDB(ResponseType apiResponse, ResponseType dbResponse) {
        return !apiResponse.equals(dbResponse);
    }

    @WorkerThread
    protected abstract ResponseType fetchFromDB();

    @MainThread
    protected abstract Call<ResponseType> getCall();
}
