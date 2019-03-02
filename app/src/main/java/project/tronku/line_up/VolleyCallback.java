package project.tronku.line_up;


public interface VolleyCallback {

    void onSuccess(String response);

    void onError(int status, String error);
}
