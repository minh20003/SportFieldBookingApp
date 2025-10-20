package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SportField {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("sport_type")
    private String sportType;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("amenities")
    private Amenities amenities;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSportType() { return sportType; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public List<String> getImages() { return images; }
    public Amenities getAmenities() { return amenities; }
}