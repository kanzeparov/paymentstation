package com.manager.direct.recyclerclient;

import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.manager.direct.recyclerclient.modelRecepient.Receipt;
import com.manager.direct.recyclerclient.models.AccountInfo;
import com.manager.direct.recyclerclient.models.TxInfo;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity{
    // map embedded in the map fragment
    private Map map = null;
    private MapMarker m_map_marker;
    private String tx;
    private MapRoute m_mapRoute;
    Async async;
    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initialize();


    }




    private MapFragment getMapFragment() {
        return (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
    }


    private void initialize() {


        // Search for the map fragment to finish setup by calling init().
        mapFragment = getMapFragment();

        // Set up disk cache path for the map service for this application
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "com.here.android.tutorial.MapService");

        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();
                        // Set the map center to the Vancouver region (no animation)
                        map.setCenter(new GeoCoordinate(55.751244, 37.618523, 0.0),
                                Map.Animation.NONE);
                        // Set the zoom level to the average between min and max
                        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                        createRoute(new GeoCoordinate(55.751244, 37.618523, 0.0),new GeoCoordinate(55.7624, 37.6223, 0.0))
                        //createMapMarker(map.getCenter());
                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                    }


                    mapFragment.getMapGesture()
                            .addOnGestureListener( new
                                                           MapGesture.OnGestureListener() {
                                                               @Override
                                                               public void onPanStart() {

                                                               }

                                                               @Override
                                                               public void onPanEnd() {

                                                               }

                                                               @Override
                                                               public void onMultiFingerManipulationStart() {

                                                               }

                                                               @Override
                                                               public void onMultiFingerManipulationEnd() {

                                                               }

                                                               @Override
                                                               public boolean onMapObjectsSelected(List<ViewObject> list) {

                                                                   for (ViewObject viewObject : list) {
                                                                       if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                                                                           MapObject mapObject = (MapObject) viewObject;

                                                                           if (mapObject.getType() == MapObject.Type.MARKER) {

                                                                               MapMarker window_marker = ((MapMarker)mapObject);
                                                                               Log.d("danish","danish");
                                                                               System.out.println("Title is................."+window_marker.getInfoBubbleHashCode());
                                                                               Toast.makeText(getApplicationContext(),"Hello"+window_marker.getCoordinate(),Toast.LENGTH_LONG).show();
                                                                               Intent intent = new Intent();
                                                                               intent.setClass(getApplicationContext(), BluetoothActivity.class);
                                                                               intent.putExtra("description", window_marker.getDescription());
                                                                               startActivity(intent);
                                                                               finish();
                                                                               return false;
                                                                           }
                                                                       }
                                                                   }
                                                                   return false;
                                                               }

                                                               @Override
                                                               public boolean onTapEvent(PointF pointF) {

                                                                   return false;
                                                               }

                                                               @Override
                                                               public boolean onDoubleTapEvent(PointF pointF) {
                                                                   return false;
                                                               }

                                                               @Override
                                                               public void onPinchLocked() {

                                                               }

                                                               @Override
                                                               public boolean onPinchZoomEvent(float v, PointF pointF) {
                                                                   return false;
                                                               }

                                                               @Override
                                                               public void onRotateLocked() {

                                                               }

                                                               @Override
                                                               public boolean onRotateEvent(float v) {
                                                                   return false;
                                                               }

                                                               @Override
                                                               public boolean onTiltEvent(float v) {
                                                                   return false;
                                                               }

                                                               @Override
                                                               public boolean onLongPressEvent(PointF pointF) {
                                                                   return false;
                                                               }

                                                               @Override
                                                               public void onLongPressRelease() {

                                                               }

                                                               @Override
                                                               public boolean onTwoFingerTapEvent(PointF pointF) {
                                                                   return false;
                                                               }
                                                           }, 0, false);
                    async = new Async();
                    async.execute();
                }
            });


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        async.cancel(true);
    }

    private void createRoute(GeoCoordinate geoCoordinate1, GeoCoordinate geoCoordinate2) {
        /* Initialize a CoreRouter */
        CoreRouter coreRouter = new CoreRouter();

        /* Initialize a RoutePlan */
        RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        /* Disable highway in this route. */
        routeOptions.setHighwaysAllowed(false);
        /* Calculate the shortest route available. */
        routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        /* Calculate 1 route. */
        routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        /* Define waypoints for the route */
        /* START: 4350 Still Creek Dr */
        RouteWaypoint startPoint = new RouteWaypoint(geoCoordinate1);
        /* END: Langley BC */
        RouteWaypoint destination = new RouteWaypoint(geoCoordinate2);

        /* Add both waypoints to the route plan */
        routePlan.addWaypoint(startPoint);
        routePlan.addWaypoint(destination);

        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan,
                new Router.Listener<List<RouteResult>, RoutingError>() {
                    @Override
                    public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                    }

                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                            RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                        if (routingError == RoutingError.NONE) {
                            if (routeResults.get(0).getRoute() != null) {
                                /* Create a MapRoute so that it can be placed on the map */
                                m_mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                m_mapRoute.setManeuverNumberVisible(true);

                                /* Add the MapRoute to the map */
                                map.addMapObject(m_mapRoute);

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                                GeoBoundingBox gbb = routeResults.get(0).getRoute()
                                        .getBoundingBox();
                                map.zoomTo(gbb, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Error:route results returned is not valid",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error:route calculation returned error code: " + routingError,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private static String accountInfoUrl = "http://18.221.128.6:8080/account/0xbC742b5DC1C009D92621B4D80577c04a7228E0c7";
    public class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler response = new BasicResponseHandler();
            HttpGet http = new HttpGet(accountInfoUrl);
            String responseString = null;
            try {
                responseString = (String) hc.execute(http, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            AccountInfo activityInfo = gson.fromJson(responseString, AccountInfo.class);
            Credentials credentials = Credentials.create("5CCBC734E1FDFC4D773D273112C72F986A82EBD0FADB4252ACCAFB01D3002C7F");
            BigInteger nonce = new BigInteger(activityInfo.getNonce().toString());
            BigInteger gas_price = BigInteger.ZERO;
            BigInteger gas_limit = new BigInteger("27100");
            BigInteger value = BigInteger.ZERO;
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, gas_price, gas_limit, "0xFD00A5fE03CB4672e4380046938cFe5A18456Df4", value, "0x");
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Hex.toHexString(signedMessage);
            OkHttpClient httpClient = new OkHttpClient();
            String url = "http://18.221.128.6:8080/sendRawTransaction";
            RequestBody formBody = RequestBody.create(MediaType.parse("text/plain"),"0x"+hexValue);
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            Response responseInfo = null;
            TxInfo txInfo = null;
            try {
                responseInfo = httpClient.newCall(request).execute();
                if (responseInfo.isSuccessful()) {
                    txInfo = gson.fromJson(responseInfo.body().string(), TxInfo.class);
                    Handler handler = new Handler(getMainLooper());
                    final TxInfo finalTxInfo = txInfo;
                    tx = txInfo.getTxHash();


                }

            } catch (IOException e) {
            }



            while (true) {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<Receipt> receipts = getReceopt();
                for (int i = 0; i < receipts.size(); i++) {
                    boolean statusIot = false;
                    if(receipts.get(i).getType().equals("rpi")) {
                        statusIot = true;
                    }
                    createMapMarker(new GeoCoordinate(Double.parseDouble(receipts.get(i).getLoc().split(":")[0]),
                            Double.parseDouble(receipts.get(i).getLoc().split(":")[1])),receipts.get(i),statusIot);
                }
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });


            }

        }

        public List<Receipt> getReceopt() {
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler response = new BasicResponseHandler();
            HttpGet http = new HttpGet("http://lykov.tech:3000/db");
            String responseString = null;
            try {
                responseString = (String) hc.execute(http, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            com.manager.direct.recyclerclient.modelRecepient.Id activityInfo = gson.fromJson(responseString, com.manager.direct.recyclerclient.modelRecepient.Id.class);
            return activityInfo.getReceipts();
        }
    }


    private void createMapMarker(GeoCoordinate geoCoordinate, Receipt receipt, boolean iot) {
        // create an image from cafe.png.
        Image marker_img = new Image();
        try {
            if(!iot)
            marker_img.setImageResource(R.drawable.cafe);
            else
                marker_img.setImageResource(R.drawable.iot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // create a MapMarker centered at current location with png image.
        m_map_marker = new MapMarker(geoCoordinate, marker_img);
        m_map_marker.setDescription(receipt.getId() + ": id:" + receipt.getAmount() + ": price:" + receipt.getLoc() + ": loc:"+ receipt.getType() + ": type:" + receipt.getTargetAddr() + ": address:" + tx);
        // add a MapMarker to current active map.
        map.addMapObject(m_map_marker);

    }


}
