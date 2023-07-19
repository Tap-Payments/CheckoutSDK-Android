package company.tap.tapnetworkkit.request;

import com.google.gson.JsonElement;

import company.tap.tapnetworkkit.callbacks.BaseCallback;
import company.tap.tapnetworkkit.interfaces.APIRequestCallback;
import retrofit2.Call;

/**
 * Created by Mario Gamal on 5/12/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 */
public class TapRequest {
    private Call<JsonElement> request;
    private APIRequestCallback requestCallback;
    private int requestCode;

    /**
     * Instantiates a new Delayed request.
     *
     * @param request         the request
     * @param requestCallback the request callback
     */
    public TapRequest(Call<JsonElement> request, APIRequestCallback requestCallback, int requestCode) {
        this.request = request;
        this.requestCallback = requestCallback;
        this.requestCode = requestCode;
    }

    /**
     * Run.
     */
    public void run() {
        request.enqueue(new BaseCallback(requestCallback, requestCode));
    }

}