package com.manager.direct.recyclerclient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private TextView textView;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    private static String accountInfoUrl = "http://18.221.128.6:8080/account/0xbC742b5DC1C009D92621B4D80577c04a7228E0c7";
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.txInfo);


        new  AsyncPost().execute();

    }

    private class AsyncPost extends AsyncTask<Void, Void, Void> {

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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(finalTxInfo.getTxHash());
                        }
                    });

                }

            } catch (IOException e) {
            }
            return null;
        }
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            removeItem(v);
        }

        private void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            boolean stateRem = true;
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            String selectedName = (String) textViewName.getText();
            int selectedItemId = -1;
            for (int i = 0; i < MyData.nameArray.length; i++) {
                if (selectedName.equals(MyData.nameArray[i])) {
                    selectedItemId = MyData.id_[i];
                    if (MyData.state[i] == false) {
                        stateRem = false;
                        showToast(v, false);
                        MyData.state[i] = true;
                    } else if (MyData.state[i] == true) {
                        showToast(v, true);
                    }
                }
            }

            if (stateRem) {
                removedItems.add(selectedItemId);
                data.remove(selectedItemPosition);
                adapter.notifyItemRemoved(selectedItemPosition);
            }
        }

        public void showToast(View v, Boolean b) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast,
                    (ViewGroup) v.findViewById(R.id.toast_layout_root));

            ImageView image = (ImageView) layout.findViewById(R.id.image);
            if (b == true) {
                image.setImageResource(R.drawable.ok);
            }

            if (b == false) {
                image.setImageResource(R.drawable.no);
            }
            TextView text = (TextView) layout.findViewById(R.id.text);
            //  text.setText("Hello! This is a custom toast!");
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }

    }

    public void showToast(View v) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) v.findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ok);
        TextView text = (TextView) layout.findViewById(R.id.text);
        //  text.setText("Hello! This is a custom toast!");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_item) {
            //check if any items to add
            if (removedItems.size() != 0) {
                addRemovedItemToList();
            } else {
                Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void addRemovedItemToList() {
        int addItemAtListPosition = 3;
        data.add(addItemAtListPosition, new DataModel(
                MyData.nameArray[removedItems.get(0)],
                MyData.versionArray[removedItems.get(0)],
                MyData.id_[removedItems.get(0)],
                MyData.drawableArray[removedItems.get(0)]
        ));
        adapter.notifyItemInserted(addItemAtListPosition);
        removedItems.remove(0);
    }
}