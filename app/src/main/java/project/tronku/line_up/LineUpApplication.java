package project.tronku.line_up;

import android.app.Application;

public class LineUpApplication extends Application {

    private LocationFinder locationFinder;

    public LineUpApplication(LocationFinder locationFinder) {
        this.locationFinder = locationFinder;
        startLocating();
    }

    private void startLocating() {
        locationFinder.getLocation();
    }

}
