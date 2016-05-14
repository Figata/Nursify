package com.example.nursify.utils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class ProgressFilter implements ServiceFilter {

    @Override
    public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

        final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();

        ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

        Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
            @Override
            public void onFailure(Throwable e) {
                resultFuture.setException(e);
            }

            @Override
            public void onSuccess(ServiceFilterResponse response) {
                resultFuture.set(response);
            }
        });

        return resultFuture;
    }
}