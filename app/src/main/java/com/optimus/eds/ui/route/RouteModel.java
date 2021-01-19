
package com.optimus.eds.ui.route;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.BaseModel;
import com.optimus.eds.db.entities.Route;

import java.util.List;

public class RouteModel extends BaseModel {

    @SerializedName("Route")
    private List<Route> mRoute;


    public List<Route> getRoute() {
        return mRoute;
    }


    public static class Builder {

        private List<Route> mRoute;

        public RouteModel.Builder withRoute(List<Route> route) {
            mRoute = route;
            return this;
        }


        public RouteModel build() {
            RouteModel routeModel = new RouteModel();
            routeModel.mRoute = mRoute;
            return routeModel;
        }

    }

}
