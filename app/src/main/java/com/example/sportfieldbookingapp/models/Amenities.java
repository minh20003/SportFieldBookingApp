package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class Amenities {
    @SerializedName("parking")
    private boolean parking;

    @SerializedName("shower")
    private boolean shower;

    @SerializedName("drinks")
    private boolean drinks;

    // Getters
    public boolean hasParking() {
        return parking;
    }

    public boolean hasShower() {
        return shower;
    }

    public boolean hasDrinks() {
        return drinks;
    }
}