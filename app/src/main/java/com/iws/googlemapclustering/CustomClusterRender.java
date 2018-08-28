package com.iws.googlemapclustering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import android.widget.ImageView;

/**
 * Created by Subarata Talukder on 3/21/2017.
 */

public class CustomClusterRender extends DefaultClusterRenderer<MarkerItemModel> {

    GoogleMap googleMap;
    Context context;

    private final IconGenerator mIconGenerator = new IconGenerator(context);
    private final IconGenerator mClusterIconGenerator = new IconGenerator(context);
    private ImageView mImageView;
    private ImageView mClusterImageView;
    private int mDimension;

    public CustomClusterRender(Context context, GoogleMap map, ClusterManager<MarkerItemModel> clusterManager) {
        super(context, map, clusterManager);

        this.googleMap = map;
        this.context = context;

    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerItemModel item, MarkerOptions markerOptions) {

        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

        if (bounds.contains(item.getPosition())) {
           // markerOptions.position(item.getPosition()).icon(BitmapDescriptorFactory

                  //  .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    markerOptions.position(item.getPosition()).icon(BitmapDescriptorFactory.fromResource(R.drawable.locatio))

                    .title(item.getTitle()).snippet(item.getSnippet());


        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MarkerItemModel> cluster) {
        // Make cluster when makers are larger than 5
        return cluster.getSize() > 4;
    }
}