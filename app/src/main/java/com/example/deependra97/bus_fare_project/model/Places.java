package com.example.deependra97.bus_fare_project.model;

/**
 * Created by deependra97 on 7/22/2017.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Places {
    public  String id, name;

    public Places(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Places> getPlaceList(JSONArray array){
        List<Places> list = new ArrayList<>();
        for (int i = 0 ; i < array.length() ; i ++){
            try {
                list.add(new Places(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  list;
    }
    public Places(JSONObject object){
        this.id = object.optString("pid");
        this.name = object.optString("name");
    }

    @Override
    public String toString() {
        return this.name;
    }
}


