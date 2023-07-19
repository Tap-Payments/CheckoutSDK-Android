package company.tap.tapnetworkkit.connection;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

//import company.tap.nativenetworkkit.BuildConfig;
import company.tap.tapnetworkkit.exception.NoAuthTokenProvidedException;
import company.tap.tapnetworkkit.interfaces.APIRequestInterface;
import company.tap.tapnetworkkit.interfaces.APILoggInterface;
import company.tap.tapnetworkkit_android.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The type Retrofit helper.
 */
public final class RetrofitHelper {
    private static Retrofit retrofit;
    private static APIRequestInterface helper;
    private static APILoggInterface APILoggInterface;
    static OkHttpClient okHttpClient;
    private static final String TAG = "RetrofitHelper";


    /**
     * Gets api helper.
     *
     * @return the api helper
     */
    public static APIRequestInterface getApiHelper(String baseUrl, Context context , Boolean debugMode, String packageId, AppCompatActivity activity) {
        if (retrofit == null) {
            if (NetworkApp.getAuthToken() == null) {
                throw new NoAuthTokenProvidedException();
            }

                 okHttpClient = getOkHttpClient(context ,debugMode, packageId);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(buildGsonConverter())
                    .client(okHttpClient)
                    .build();
        }

        if (helper == null) {
            helper = retrofit.create(APIRequestInterface.class);
        }
        APILoggInterface = (APILoggInterface) activity;
        return helper;
    }

    private static OkHttpClient getOkHttpClientWithHeader(Context context) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(new NetworkConnectionInterceptor(context));
        httpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader(APIConstants.TOKEN_PREFIX, APIConstants.AUTH_TOKEN_PREFIX + NetworkApp.getHeaderToken())
                    //.addHeader(APIConstants.APPLICATION, NetworkApp.getApplicationInfo())
                    .addHeader(APIConstants.ACCEPT_KEY, APIConstants.ACCEPT_VALUE)
                    .addHeader(APIConstants.CONTENT_TYPE_KEY, APIConstants.CONTENT_TYPE_VALUE).build();
            return chain.proceed(request);
        });
        httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(!BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY));

        return httpClientBuilder.build();
    }


    private static GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson myGson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();
        // custom deserializers not added
        return GsonConverterFactory.create(myGson);
    }

    private static OkHttpClient getOkHttpClient(Context context , Boolean debugMode, String packageId) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(new NetworkConnectionInterceptor(context));

            httpClientBuilder.addInterceptor(chain -> {
                Request request = chain.request()
                        .newBuilder()
                       // .addHeader(APIConstants.TOKEN_PREFIX, APIConstants.AUTH_TOKEN_PREFIX + NetworkApp.getHeaderToken())
                        .addHeader(APIConstants.AUTH_TOKEN_KEY,  NetworkApp.getAuthToken())
                        .addHeader(APIConstants.PACKAGE_ID,NetworkApp.getPackageId())
                        .addHeader(APIConstants.SESSION_PREFIX , NetworkApp.getHeaderToken())
                        .addHeader(APIConstants.APPLICATION, NetworkApp.getApplicationInfo())
                        .addHeader(APIConstants.ACCEPT_KEY, APIConstants.ACCEPT_VALUE)
                        .addHeader(APIConstants.CONTENT_TYPE_KEY, APIConstants.CONTENT_TYPE_VALUE)
                        .addHeader(APIConstants.IP_ADDRESS, NetworkApp.getUserIpAddress())

                        .build();

                return chain.proceed(request);
            });
        httpClientBuilder.addInterceptor(getLogging(debugMode));

           /* if(debugMode|| NetworkApp.debugMode){
                httpClientBuilder.addInterceptor(getLogging(debugMode));

            }else{
                httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(!BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.NONE : HttpLoggingInterceptor.Level.NONE));

            }
*/
        return httpClientBuilder.build();
    }

    public static HttpLoggingInterceptor getLogging(Boolean debugMode){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                Log.d(TAG, "OkHttp: " + message);
                APILoggInterface.onLoggingEvent(message);
            }
        });
        if(debugMode|| NetworkApp.debugMode){
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        return logging;
    }
}