package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView pokenameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pokenameTextView = findViewById(R.id.pokename);

        int firstGeneration = 1;

        for (int i = 0; i <= firstGeneration; i++) {
            String relativeUrl = "pokemon/" + i;
            new GetPokemonInfo(pokenameTextView).execute(relativeUrl);
        }
    }
}
