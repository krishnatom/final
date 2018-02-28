package com.example.krishna.shop;

/**
 * Created by Ramya on 11-04-2017.
 */
public class Shops {
    int id;
    String name,description;
    Boolean status;
    Double lat,lon;

    public Shops(int id, String name, String description, Boolean status, Double lat, Double lon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.lat = lat;
        this.lon = lon;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getStatus() {
        return status;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}
