package com.iws.googlemapclustering;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Subarata Talukder on 3/21/2017.
 */

public final class MarkerItemModel implements ClusterItem {

    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    public MarkerItemModel(LatLng mPosition, String mTitle, String mSnippet) {
        this.mPosition = mPosition;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}

