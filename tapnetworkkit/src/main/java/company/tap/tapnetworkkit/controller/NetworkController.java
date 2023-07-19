package company.tap.tapnetworkkit.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;


import company.tap.tapnetworkkit.connection.RetrofitHelper;
import company.tap.tapnetworkkit.enums.TapMethodType;
import company.tap.tapnetworkkit.interfaces.APIRequestCallback;
import company.tap.tapnetworkkit.interfaces.APIRequestInterface;
import company.tap.tapnetworkkit.request.TapRequest;

public class NetworkController {

    private APIRequestInterface apiRequestInterface;

    /**
     * Required to set baseURL on retrofit
     */
    public void setBaseUrl(String baseURL, Context context, Boolean debugMode, String packageId, AppCompatActivity activity) {
        apiRequestInterface = RetrofitHelper.getApiHelper(baseURL, context, debugMode,packageId, activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void processRequest(TapMethodType method, String apiName, String requestBody, APIRequestCallback callback, int requestCode) {
        switch (method) {
            case GET:
                new TapRequest(apiRequestInterface.getRequest(apiName), callback, requestCode).run();
                break;
            case POST:
                Map<String, Object> requestMap = new Gson().fromJson(
                        requestBody, new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                new TapRequest(apiRequestInterface.postRequest(apiName,requestMap), callback, requestCode).run();
                break;
            case PUT:
                new TapRequest(apiRequestInterface.putRequest(apiName), callback, requestCode).run();
                break;
            case DELETE:
                new TapRequest(apiRequestInterface.delete(apiName), callback, requestCode).run();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
    }

    private static class SingletonCreationAdmin {
        private static final NetworkController INSTANCE = new NetworkController();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static NetworkController getInstance() {
        return SingletonCreationAdmin.INSTANCE;
    }

}


