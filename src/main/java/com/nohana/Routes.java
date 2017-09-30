package com.nohana;



import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Routes {

    private static Routes instance;

    public static Routes getInstance(){
        if(null == instance){
            instance = new Routes();
        }
        return instance;
    }

    private Map<String,Route> entities = new TreeMap<>();

    public Route add(String name,String uriPattern){
        Route route = Route.create(uriPattern);

        entities.put(name,route);

        return route;
    }

    public Collection<Route> all(){
        return entities.values();
    }

}
