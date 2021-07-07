package company.tap.gosellapi.open.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AhlaamK on 6/24/21.
 * <p>
 * Copyright (c) 2021    Tap Payments.
 * All rights reserved.
 **/
public class TopCustomerModel {
    @SerializedName("id")
    @Expose
    private String id;

    public TopCustomerModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
