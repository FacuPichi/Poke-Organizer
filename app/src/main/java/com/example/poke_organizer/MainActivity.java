package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView pokenameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pokenameTextView = findViewById(R.id.pokename);

        int firstGeneration = 3;

        for (int i = 0; i <= firstGeneration; i++) {
            String relativeUrl = "pokemon/" + i;
            new GetPokemonInfoTask().execute(relativeUrl);
        }
    }

    private class GetPokemonInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String relativeUrl = params[0];
            APIClient apiClient = new APIClient();
            return apiClient.sendGetRequest(relativeUrl);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject pokemonData = new JSONObject(response);
                    String name = pokemonData.getString("name");
                    pokenameTextView.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Error al realizar la solicitud a la API");
            }
        }
    }
}
