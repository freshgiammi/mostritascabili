package com.example.mostritascabili;

import org.json.JSONObject;

/**
 * ServerCallback
 * Interface to fetch data from volley requests
 */

public interface ServerCallback{
    void onSuccess(JSONObject response);
}