package com.bitnews.bitnews.data.network;


import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.bitnews.bitnews.utils.AppExecutors;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class NetworkBoundResource<ResponseType> {
    private MediatorLiveData<APIResponse<ResponseType>> Response = new MediatorLiveData<>();
    private AppExecutors appExecutors = AppExecutors.getInstance();


    public NetworkBoundResource() {
        MediatorLiveData<ResponseType> dbResponse = new MediatorLiveData<>();

        appExecutors.getDiskIO().execute(() -> {
            ResponseType response = loadFromDB();
            appExecutors.getMainThread().execute(() -> dbResponse.setValue(response));
        });

        Response.addSource(dbResponse, data -> {
            Response.removeSource(dbResponse);
            if (shouldFetch(data))
                fetchFromNetwork(dbResponse);
            else
                Response.addSource(dbResponse, APIResponse::new);
        });
    }

    private void fetchFromNetwork(LiveData<ResponseType> dbResponse) {
        MediatorLiveData<APIResponse<ResponseType>> apiResponse = new MediatorLiveData<>();
        appExecutors.getNetworkIO().execute(() -> {
            APIResponse<ResponseType> response = executeCall(getCall());
            appExecutors.getMainThread().execute(() -> apiResponse.setValue(response));
        });

        Response.addSource(apiResponse, response -> {
            Response.removeSource(apiResponse);
            Response.removeSource(dbResponse);
            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                if (shouldSaveToDB(response.getitem(), dbResponse.getValue()))
                    appExecutors.getDiskIO().execute(() -> saveAPIResponse(response.getitem()));
                Response.setValue(response);
            } else if (response.getStatus() == APIResponse.Status.NETWORK_FAILED) {
                if (dbResponse.getValue() != null)
                    Response.setValue(new APIResponse<>(dbResponse.getValue()));
                else
                    Response.setValue(response);
            } else
                Response.setValue(response);
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
        return Response;
    }

    @WorkerThread
    protected abstract void saveAPIResponse(ResponseType item);

    @MainThread
    protected abstract boolean shouldFetch(ResponseType data);

    @MainThread
    protected boolean shouldSaveToDB(ResponseType apiResponse, ResponseType dbResponse) {
        return !apiResponse.equals(dbResponse);
    }

    @WorkerThread
    protected abstract ResponseType loadFromDB();

    @MainThread
    protected abstract Call<ResponseType> getCall();
}
