package com.manager.direct.recyclerclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.manager.direct.recyclerclient.modelRecepient.Receipt;
import com.manager.direct.recyclerclient.models.AccountInfo;
import com.manager.direct.recyclerclient.models.TxInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.w3c.dom.Text;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BluetoothActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {

    private ListView listView;
    Intent intentInfo;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    Button pay;
    TextView textView;
    String info;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        listView = (ListView) findViewById(R.id.listView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        pay = findViewById(R.id.pay);
        textView = findViewById(R.id.info);

        intentInfo = getIntent();
        info = intentInfo.getStringExtra("description");

        textView.setText(info.split(":")[2] + " - price\n" +  info.split(":")[9] + " - address\n" );
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        listView.setOnItemClickListener(this);
//        try {
////            BluetoothUtil.sendData(new byte[]{4,2},BluetoothUtil.getSocket(BluetoothUtil.getDevice(mBluetoothAdapter)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = accountInfoUrl + info.split(":")[0];


                        InputStream inputStream = null;
                        String result = "";

                        try {
                            // 1. create HttpClient
                            HttpClient httpclient = new DefaultHttpClient();
                            // 2. make POST request to the given URL
                            HttpPut httpPUT = new
                                    HttpPut(url);
                            String json = "";
                            // 3. build jsonObject
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id",new Integer(info.split(":")[0]));
                            jsonObject.put("type",info.split(":")[7]);
                            jsonObject.put("tx",info.split(":")[11]);
                            jsonObject.put("target_adr",info.split(":")[9]);
                            jsonObject.put("loc",info.split(":")[4]+":"+info.split(":")[5]);
                            jsonObject.put("amount",info.split(":")[2]);




                            // 4. convert JSONObject to JSON to String
                            json = jsonObject.toString();

                            // 5. set json to StringEntity
                            StringEntity se = new StringEntity(json);
                            // 6. set httpPost Entity
                            httpPUT.setEntity(se);
                            // 7. Set some headers to inform server about the type of the content
                            httpPUT.setHeader("Accept", "application/json");
                            httpPUT.setHeader("Content-type", "application/json");
                            // 8. Execute POST request to the given URL
                            HttpResponse httpResponse = httpclient.execute(httpPUT);
                            // 9. receive response as inputStream
                            //                  inputStream = httpResponse.getEntity().getContent();
                            //                  // 10. convert inputstream to string
                            //                  if(inputStream != null)
                            //                      result = convertInputStreamToString(inputStream);
                            //                  else
                            //                      result = "Did not work!";
                        } catch (Exception e) {
                            Log.d("InputStream", e.getLocalizedMessage());
                        }

                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast,
                                        (ViewGroup) findViewById(R.id.toast_layout_root));
                                ImageView image = (ImageView) layout.findViewById(R.id.image);
                                image.setImageResource(R.drawable.ok);
                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText("Hello! This is a custom toast!");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        });


                    }
                }).start();
            }
        });
    }
    private static String accountInfoUrl = "http://lykov.tech:3000/receipts/";
    public class AsyncPost extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
//        // Then you start a new Activity via Intent
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        intent.putExtra("position", position);
//        // Or / And
//        intent.putExtra("id", id);
//        startActivity(intent);
    }
}