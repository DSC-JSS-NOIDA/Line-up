package project.tronku.line_up;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Helper {

    private static final String TAG = "Helper";

    public static void fetchUserInfo(final String accessToken, final VolleyCallback volleyCallback) {
        String url = API.BASE + API.USER_INFO ;
        StringRequest sr = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, " Helper onResponse: " + response);
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Helper onErrorResponse: " + error.toString());
                if(error.networkResponse != null)
                    volleyCallback.onError(error.networkResponse.statusCode, new String(error.networkResponse.data));
                else
                    volleyCallback.onError(HttpStatus.SERVICE_UNAVAILABLE.value(), "");
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }
        };

        LineUpApplication.getInstance().addToRequestQueue(sr);
    }


    public static List<PlayerPOJO> getPlayersFromResponse(String response) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(response).getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("teammatesFound") instanceof JsonNull ? new JsonArray() : jsonObject.get("teammatesFound").getAsJsonArray();
        List<PlayerPOJO> playerPOJOList = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject user = jsonArray.get(i).getAsJsonObject();
            playerPOJOList.add(getPlayerFromJsonObject(user));
        }
        return playerPOJOList;
    }

    public static PlayerPOJO getPlayerFromJsonObject(JsonObject user){
        PlayerPOJO playerPOJO = new PlayerPOJO();
        playerPOJO.setName(user.get("firstName") instanceof JsonNull ? ""  : user.get("firstName").getAsString());
        playerPOJO.setScore(user.get("score").getAsString());
        playerPOJO.setPosition(user.get("position").getAsString());
        playerPOJO.setLat(user.get("lat").getAsString());
        playerPOJO.setLng(user.get("lng").getAsString());
        playerPOJO.setZealId(user.get("zeal_id").getAsString());

        long millis = user.get("totalTimeTaken").getAsLong() / 1000;

        if(user.has("uniqueCode")){
            playerPOJO.setUniqueCode(user.get("uniqueCode").getAsString());
        }
        playerPOJO.setTimeTaken(String.format(Locale.UK, "%d:%02d:%02d", millis / 3600, (millis % 3600) / 60, (millis % 60)));
        return playerPOJO;
    }


    public static PlayerPOJO getPlayerFromJsonString(String response) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(response).getAsJsonObject();
        return getPlayerFromJsonObject(jsonObject);
    }


    public static void fetchEventDetails(final VolleyCallback volleyCallback) {
        String url = API.BASE + API.EVENT_DETAILS ;
        StringRequest sr = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, " Helper onResponse Event Details: " + response);
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Helper onErrorResponse  Event Details: " + error.toString());
                if(error.networkResponse != null)
                    volleyCallback.onError(error.networkResponse.statusCode, new String(error.networkResponse.data));
                else
                    volleyCallback.onError(HttpStatus.SERVICE_UNAVAILABLE.value(), "");
            }
        }){

            public Map<String, String> getHeaders() throws AuthFailureError {
                String credentials = API.USERNAME + ":" + API.PASSWORD;
                String encoding = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "Basic " + encoding);

                return params;
            }
        };

        LineUpApplication.getInstance().addToRequestQueue(sr);


    }

    public static EventDetails getEventDetailsFromJsonResponse(String response) throws ParseException {

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
        EventDetails eventDetails = new EventDetails();

        eventDetails.setStartTime(getDateFromString(jsonObject.get("startTime").getAsString()));
        eventDetails.setEndTime(getDateFromString(jsonObject.get("endTime").getAsString()));
        eventDetails.setSignUpStartTime(getDateFromString(jsonObject.get("signUpStartTime").getAsString()));
        eventDetails.setSignUpEndTime(getDateFromString(jsonObject.get("signUpEndTime").getAsString()));
        return eventDetails;
    }

    private static Date getDateFromString(String dateString) throws ParseException {
        DateFormat formatterIST = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        formatterIST.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date date = formatterIST.parse(dateString);
        Log.i(TAG, "Helper parse date {}" + formatterIST.format(date));
        return date;
    }

}
