package com.example.poke_organizer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;


public class TaskActivity extends AppCompatActivity {


    private List<CheckBox> checkBoxList = new ArrayList<>();
    private UserData User1; // Declarar la variable aquí

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User1 = JsonHandler.loadJsonData(this);

        if (User1 == null ) {
            // Redirigir al usuario a RegisterActivity
            Intent intent = new Intent(TaskActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Finalizar TaskActivity para que el usuario no pueda volver a ella con el botón de retroceso
            return; // Salir del método onCreate para evitar ejecutar el resto del código
        }
        // Verifico si hay tareas en User1
        ArrayList<String> tareas = User1.getTarea();
        if (tareas != null && !tareas.isEmpty()) {
            LinearLayout linearLayout = findViewById(R.id.linearLay);

            // Genero dinámicamente los CheckBox para las tareas existentes
            for (String tarea : tareas) {
                CheckBox checkBox = new CheckBox(TaskActivity.this);
                checkBox.setText(tarea);

                // Agrego el CheckBox al LinearLayout
                linearLayout.addView(checkBox);

                // Agrego el CheckBox a la lista
                checkBoxList.add(checkBox);

                // Agrego un listener al CheckBox para detectar el cambio de estado
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // Obtengo el texto del CheckBox marcado
                            String textoCheckBox = ((CheckBox) buttonView).getText().toString();
                            TextView experience = findViewById(R.id.exp);
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            // Cargo la experiencia
                            User1.setExp(User1.getExp() + 10);

                            // Actualizo el texto de experiencia
                            experience.setText(User1.getExp() + " / 100 exp");
                            progressBar.setProgress(User1.getExp());
                            // Elimino la tarea de mi User
                            User1.getTarea().remove(textoCheckBox);
                            JsonHandler.saveJsonData(TaskActivity.this, User1);

                            // Elimino el CheckBox del LinearLayout y de la lista
                            linearLayout.removeView(buttonView);
                            checkBoxList.remove(buttonView);
                        }
                    }
                });
            }
        }

        //inicialización y declaración de variables
        Random random = new Random();
        TextView pokename = findViewById(R.id.pokename);
        TextView level = findViewById(R.id.level);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button pokemonChange = findViewById(R.id.pokemonChange);
        TextView experience = findViewById(R.id.exp);
        Button perfil = findViewById(R.id.perfil);
        Button login = findViewById(R.id.login);
        Button agregar = findViewById(R.id.agregar);
        int min = 1;
        int max = 1010;
        //int numeroAleatorio = random.nextInt(max - min + 1) + min;
        //String relativeUrl = "pokemon/"+numeroAleatorio+"/";
        //new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
        //termino la inicializacion




        // Llamada para generar el archivo JSON con datos dummy
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                experience.setText(User1.getExp() +" /100 exp");
            }
        });
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(User1.getExp());
        ColorStateList colorStateList = ColorStateList.valueOf(Color.YELLOW);
        progressBar.setProgressTintList(colorStateList);
        agregar.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColor));
        int numeroPokemon = User1.getLastPokemon();
        String relativeUrl = "pokemon/"+numeroPokemon+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
        level.append(String.valueOf(User1.getLvl()));
        //termino la inicializacion


        //Metodo para cambiar a pokemon aleatorio
        pokemonChange.setOnClickListener(view -> {
            final int pokemonAleatorio = random.nextInt(max - min + 1) + min;
            User1.setLastPokemon(pokemonAleatorio);
            JsonHandler.saveJsonData(TaskActivity.this,User1);
            String relativeUrl1 = "pokemon/" + pokemonAleatorio + "/";
            User1.addPokemon(String.valueOf(pokemonAleatorio));
            new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);
            Toast.makeText(this, "Cambio de Pokémon: " + pokemonAleatorio, Toast.LENGTH_SHORT).show();
        });

        //Metodo para irse al perfil
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un nuevo intent
                Intent intent = new Intent(TaskActivity.this, ProfileActivity.class);

                // Iniciar la segunda actividad
                startActivity(intent);
                Log.d("Perfil", "El pokemon es: "+ User1.getLastPokemon() + ".");
            }
        });

        //Metodo para agregar y que se eliminen los checkbox agregados.
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = findViewById(R.id.linearLay);

                // Crear un cuadro de diálogo para ingresar la tarea
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                builder.setTitle("Agregue la Tarea");

                // Agregar un campo de texto para la tarea
                final EditText input = new EditText(TaskActivity.this);
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
                        CheckBox checkBox = new CheckBox(TaskActivity.this);
                        checkBox.setText(tarea);
                        //guardar tarea

                        User1.addTarea(tarea);
                        JsonHandler.saveJsonData(TaskActivity.this,User1);
                        // Agregar el CheckBox al LinearLayout
                        linearLayout.addView(checkBox);

                        // Agregar el CheckBox a la lista
                        checkBoxList.add(checkBox);

                        // Agregar un listener al CheckBox para detectar el cambio de estado
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    // Obtengo el texto del CheckBox marcado
                                    String textoCheckBox = ((CheckBox) buttonView).getText().toString();
                                    // Cargo la experiencia
                                    User1.setExp(User1.getExp() + 10);
                                    // Si la experiencia es mayor o igual a 100, sube de nivel y cambia el Pokémon
                                    if (User1.getExp() >= 100) {
                                        User1.setExp(0); // Resta 100 de la experiencia
                                        User1.setLvl(User1.getLvl() + 1); // Aumenta el nivel
                                        int pokemonAleatorio = random.nextInt(max - min + 1) + min; // Genera un nuevo Pokémon
                                        User1.setLastPokemon(pokemonAleatorio); // Cambia el Pokémon
                                        String relativeUrl1 = "pokemon/" + pokemonAleatorio + "/"; // Actualiza la URL del Pokémon
                                        Toast.makeText(TaskActivity.this, "¡Felicidades has subido de nivel! ¡haz descubierto un nuevo pokemon! ", Toast.LENGTH_SHORT).show();
                                        level.setText("Nivel: " + User1.getLvl());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JsonHandler.saveJsonData(TaskActivity.this,User1);
                                                User1.addPokemon(String.valueOf(pokemonAleatorio));
                                                new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1); // Actualiza la vista del Pokémon
                                                level.setText("Nivel: " + User1.getLvl()); // Actualiza la vista del nivel
                                                experience.setText(User1.getExp() + " / 100 exp");
                                                progressBar.setProgress(User1.getExp());
                                            }
                                        });
                                    } else {
                                        // Actualizo el texto de experiencia en el hilo principal
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                experience.setText(User1.getExp() + " / 100 exp");
                                                progressBar.setProgress(User1.getExp());
                                            }
                                        });
                                    }
                                    // Elimino la tarea de mi User
                                    User1.getTarea().remove(textoCheckBox);
                                    JsonHandler.saveJsonData(TaskActivity.this, User1);
                                    // Elimino el CheckBox del LinearLayout y de la lista
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

        //BOTON TESTIN DE LOGIN

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    // Crear un nuevo intent
                    Intent intent = new Intent(TaskActivity.this, RegisterActivity.class);

                    // Iniciar la actividad de inicio de sesión
                    startActivity(intent);
                } catch (Exception e) {
                    // Manejar la excepción aquí
                    e.printStackTrace(); // Imprime la traza de la excepción en la consola (puedes cambiar esto según tus necesidades)
                    // También puedes mostrar un mensaje de error o realizar otras acciones según tus necesidades.
                }
            }
        });




    }
    protected void onPause() {
        super.onPause();

        JsonHandler.saveJsonData(this, User1);
    }

    protected void onDestroy() {
        super.onDestroy();
        JsonHandler.saveJsonData(this, User1);
    }

    }



