
package ma.projet.cycledevie;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLifecycle";
    private EditText editTextVille;
    private ListView listViewMeteo;
    List<MeteoItem> data = new ArrayList<>();
    private MeteoListModel model;
    private ImageButton buttonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextVille = findViewById(R.id.editTextVille);
        listViewMeteo = findViewById(R.id.listViewMeteo);
        buttonOK = findViewById(R.id.buttonOK);

        model = new MeteoListModel(this, R.layout.list_item_layout, data);
        listViewMeteo.setAdapter(model);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MyLog", "......");
                data.clear();
                model.notifyDataSetChanged();
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String ville = editTextVille.getText().toString().trim();
                if (ville.isEmpty()) {
                    editTextVille.setError("Entrer une ville");
                    return;
                }
                Log.i("MyLog", ville);
                String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                        + ville + "&appid=0f7062a59546903b7d37fc47a7f7c22e";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("MyLog", "----------------------------");
                                    Log.i("MyLog", response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    String cod = jsonObject.getString("cod");
                                    if (!cod.equals("200")) {
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(MainActivity.this, "API Error: " + message, Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        MeteoItem meteoItem = new MeteoItem();
                                        JSONObject d = jsonArray.getJSONObject(i);
                                        Date date = new Date(d.getLong("dt") * 1000);
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM yyyy'T'HH:mm");
                                        String dateString = sdf.format(date);
                                        JSONObject main = d.getJSONObject("main");
                                        JSONArray weather = d.getJSONArray("weather");
                                        int tempMin = (int) (main.getDouble("temp_min") - 273.15);
                                        int tempMax = (int) (main.getDouble("temp_max") - 273.15);
                                        int pression = main.getInt("pressure");
                                        int humidity = main.getInt("humidity");

                                        meteoItem.tempMax = tempMax;
                                        meteoItem.tempMin = tempMin;
                                        meteoItem.pression = pression;
                                        meteoItem.humidite = humidity;
                                        meteoItem.date = dateString;
                                        meteoItem.image = weather.getJSONObject(0).getString("main");
                                        data.add(meteoItem);
                                    }
                                    model.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "Error parsing data.", Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse != null) {
                                    Log.e("MyLog", "Error Status Code: " + error.networkResponse.statusCode);
                                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                    Log.e("MyLog", "Error Response Body: " + responseBody);
                                }
                                Log.e("MyLog", "Connection problem!", error);
                                Toast.makeText(MainActivity.this, "Connection problem or invalid city. Check API key.", Toast.LENGTH_LONG).show();
                            }
                        });
                queue.add(stringRequest);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Toast.makeText(this, "L'application devient visible à l'écran.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Toast.makeText(this, "L'application est prête à être utilisée.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Toast.makeText(this, "L'application perd le focus.", Toast.LENGTH_SHORT).show();
        // Log.i(TAG, "Application mise en pause.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // new AlertDialog.Builder(this)
        //         .setMessage("L'application est désormais en arrière-plan.")
        //         .setPositiveButton(android.R.string.ok, null)
        //         .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // new AlertDialog.Builder(this)
        //         .setMessage("Souhaitez-vous continuer l'utilisation de l'application ou annuler?")
        //         .setPositiveButton("Continuer", null)
        //         .setNegativeButton("Annuler", (dialog, which) -> finish())
        //         .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Fermeture définitive de l'application.");
    }
}
