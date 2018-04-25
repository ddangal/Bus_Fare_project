package com.example.deependra97.bus_fare_project.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deependra97.bus_fare_project.R;
import com.example.deependra97.bus_fare_project.app.Constants;
import com.example.deependra97.bus_fare_project.app.Utilities;
import com.example.deependra97.bus_fare_project.model.Places;
import com.example.deependra97.bus_fare_project.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button display;
    TextView bsfare;
    AutoCompleteTextView cur_place;
    AutoCompleteTextView destination;
    EditText show;
    Spinner spn_from;
    Spinner spn_to;
    String from = "";
    String to = "";
    RadioGroup radio_group;
    RadioButton button_normal,button_student;

    List<Places> placeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bs_fare);
        radio_group= (RadioGroup) findViewById(R.id.radio_group);
        bsfare = (TextView) findViewById(R.id.bsfare);
        display = (Button) findViewById(R.id.btnready);
        cur_place = (AutoCompleteTextView) findViewById(R.id.cur_place);
        destination = (AutoCompleteTextView) findViewById(R.id.destination);
        //spn_from = (Spinner) findViewById(R.id.spn_from);
        //spn_to= (Spinner) findViewById(R.id.spn_to);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        placeList = new ArrayList<>();
        String places = preferences.getString("place_list", "");
        if(preferences.contains("place_list")){
            Utilities.log("Contents key");
        }
        if(!places.isEmpty()){
            try {
                JSONArray array = new JSONArray(places);
                placeList.addAll(Places.getPlaceList(array));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //TODO : GET PLACE LIST FROM SERVER
        ArrayAdapter<Places> adapter = new ArrayAdapter<Places>(this, android.R.layout.simple_list_item_1, placeList );
        cur_place.setAdapter(adapter);

        destination.setAdapter(adapter);

        cur_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {

                String from_place = cur_place.getText().toString();
                for (int i = 0; i < placeList.size(); i++) {
                    if(from_place.equals(placeList.get(i).name))
                    {
                        from = placeList.get(i).id;
                        break;
                    }
                }

                Utilities.log("From = " + from );
                if(!from.isEmpty() && !to.isEmpty()){
                    callServer(from, to);
                }
            }
        });

        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String to_place= destination.getText().toString();

                for (int i1 = 0; i1 < placeList.size(); i1++) {
                    if(to_place.equals(placeList.get(i1).name))
                    {
                        to = placeList.get(i1).id;
                        break;
                    }
                }

                Utilities.log("On item selected listener " + to  );
                if(!from.isEmpty() && !to.isEmpty()){
                    callServer(from, to);
                }

            }
        });

        final ArrayAdapter<Places> finalAdapter1 = adapter;
        new ServerRequest(Constants.BASE_URL + "api.php?action=get_places", new ServerRequest.OnDataReceiver()
        {

            @Override
            public void onSuccess(String res) {
                try {
                    Utilities.log("Response= " + res);
                    preferences.edit().putString("place_list", res).commit();
                    JSONObject ob = new JSONObject(res);
                    placeList.clear();
                    placeList.addAll(Places.getPlaceList(ob.getJSONArray("data")));
                    finalAdapter1.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        }).execute();


    }


    public void callServer(String from, String to){
        Utilities.log("Call server =" + from + " " + to) ;

        final ProgressDialog pg = new ProgressDialog(this);
        pg.setMessage("Please wait....");
        pg.show();
        Utilities.log(Constants.BASE_URL + "api.php?action=view&FROM=" + from + "&TO=" + to);
        new ServerRequest(Constants.BASE_URL + "api.php?action=view&FROM=" + from + "&TO=" + to, new ServerRequest.OnDataReceiver()
        {
            @Override
            public void onSuccess(String res) {
                pg.dismiss();
                Utilities.log(res);
                parseJSON(res);
            }

            @Override
            public void onError(String message) {
                pg.dismiss();
                Utilities.toast(MainActivity.this, message);
            }
        }).execute();


    }



    private void parseJSON(String res) {
        try {
            JSONObject object = new JSONObject(res);
            String response = object.optString("res");

            if(response.equals("success")){
                JSONObject data = object.optJSONObject("data");
                if(data!=null){
                    //// TODO: 7/23/2017 check student or not
                    // TODO: 7/23/2017 if student fare = 55% of  fare

                    final String fare = data.optString("fare");
                    final int actual_fare = Integer.parseInt(fare);
                    radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            switch (i)
                            {
                                case R.id.button_normal: display.setText("Your fare  is Rs. "+actual_fare+"");break;
                                case R.id.button_student: display.setText("Your fare after discount is Rs. "+(actual_fare*0.55)+"");break;
                            }
                        }
                    });

                }
            }else{
                Utilities.toast(this, response);
            }
        } catch (JSONException e) {
            Utilities.toast(this,"Something went wrong");
            e.printStackTrace();
        }
    }

    @Override

    public void onClick(View v) {
//        if(v.getId() == R.id.btnready){
//            //show.setText("Your fare is");
//            Toast.makeText(MainActivity.this, "Your fare is:10", Toast.LENGTH_SHORT).show();
//
//
//        }
    }
}
