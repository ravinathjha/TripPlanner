package com.example.anirban.tripplanner.map_helper;

import java.util.List;

/**
 * Created by: anirban on 13/9/17.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}