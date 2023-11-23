package com.example.poke_organizer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.content.Intent;
import android.view.View;
import java.io.File;
import android.view.KeyEvent;


public class MainActivity extends AppCompatActivity {


    private List<CheckBox> checkBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random random = new Random();
        int levelValue = 10;
        int progressValue = 35;
        TextView pokename = findViewById(R.id.pokename);
        TextView level = findViewById(R.id.level);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button pokemonChange = findViewById(R.id.pokemonChange);
        TextView experience = findViewById(R.id.exp);
        Button perfil = findViewById(R.id.perfil);
        Button agregar = findViewById(R.id.agregar);
        int min = 1;
        int max = 1010;
        UserData User1 = JsonHandler.loadJsonData(this);
        int numeroAleatorio = random.nextInt(max - min + 1) + min;
        String relativeUrl = "pokemon/"+numeroAleatorio+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);




        // Llamada para generar el archivo JSON con datos dummy
        level.append(String.valueOf(levelValue));
        String xp = String.valueOf(progressValue) +" /100 exp";
        experience.setText(xp);
        User1.setExp(progressValue);
        User1.setLvl(levelValue);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(progressValue);

        //Metodo para cambiar a pokemon aleatorio
        pokemonChange.setOnClickListener(view -> {
            int numeroAleatorio1 = random.nextInt(max - min + 1) + min;
            User1.setLastPokemon(numeroAleatorio1);
            JsonHandler.updateJsonData(this,User1);
            String relativeUrl1 = "pokemon/" + numeroAleatorio1 + "/";
            new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);

        });

        //Metodo para irse al perfil
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un nuevo intent
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);

                // Iniciar la segunda actividad
                startActivity(intent);

                // Actualizar el último Pokémon después de iniciar la segunda actividad
                User1.setLastPokemon(numeroAleatorio);
                JsonHandler.updateJsonData(MainActivity.this, User1);
            }
        });

        //Metodo para agregar y que se eliminen los checkbox agregados.
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = findViewById(R.id.linearLay);

                // Crear un cuadro de diálogo para ingresar la tarea
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Agregue la Tarea");

                // Agregar un campo de texto para la tarea
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);

                // Agregar botones "Cancelar" y "Aceptar"
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el texto ingresado por el usuario
                        String tarea = input.getText().toString();

                        // Crear un nuevo CheckBox con el texto de la tarea
                        CheckBox checkBox = new CheckBox(MainActivity.this);
                        checkBox.setText(tarea);

                        // Agregar el CheckBox al LinearLayout
                        linearLayout.addView(checkBox);

                        // Agregar el CheckBox a la lista
                        checkBoxList.add(checkBox);

                        // Agregar un listener al CheckBox para detectar el cambio de estado
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    // Eliminar el CheckBox del LinearLayout y de la lista
                                    linearLayout.removeView(buttonView);
                                    checkBoxList.remove(buttonView);
                                }
                            }
                        });
                    }
                });

                // Mostrar el cuadro de diálogo
                builder.show();
            }
        });



    }

    }



