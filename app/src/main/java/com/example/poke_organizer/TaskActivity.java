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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class TaskActivity extends AppCompatActivity {

    // Declarar Firestore
    private FirebaseFirestore firestore;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private UserData User1; // Declarar la variable aquí

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización y declaración de variables
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
        User1 = JsonHandler.loadJsonData(this);

        // Inicializa Firestore
        firestore = FirebaseFirestore.getInstance();

        if (User1 == null) {
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
                            JsonHandler.saveJsonData(TaskActivity.this, User1); // Guardar localmente

                            // Actualizar Firestore
                            updateUserProfileInFirestore(User1);

                            // Elimino el CheckBox del LinearLayout y de la lista
                            linearLayout.removeView(buttonView);
                            checkBoxList.remove(buttonView);

                            // Verificar si el usuario sube de nivel
                            if (User1.getExp() >= 100) {
                                User1.setExp(0); // Restablecer la experiencia
                                User1.setLvl(User1.getLvl() + 1); // Aumentar el nivel
                                int pokemonAleatorio = random.nextInt(max - min + 1) + min; // Generar un nuevo Pokémon
                                User1.setLastPokemon(pokemonAleatorio); // Cambiar el último Pokémon
                                User1.addPokemon(String.valueOf(pokemonAleatorio)); // Agregar el nuevo Pokémon a la lista

                                // Actualizar Firestore
                                updateUserProfileInFirestore(User1);

                                // Actualizar la interfaz de usuario
                                String relativeUrl1 = "pokemon/" + pokemonAleatorio + "/";
                                new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);
                                Toast.makeText(TaskActivity.this, "¡Felicidades has subido de nivel! ¡Haz descubierto un nuevo Pokémon!", Toast.LENGTH_SHORT).show();
                                level.setText("Nivel: " + User1.getLvl());
                                experience.setText(User1.getExp() + " / 100 exp");
                                progressBar.setProgress(User1.getExp());
                            }
                        }
                    }
                });
            }
        }



        // Llamada para generar el archivo JSON con datos dummy
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                experience.setText(User1.getExp() + " / 100 exp");
            }
        });

        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(User1.getExp());
        ColorStateList colorStateList = ColorStateList.valueOf(Color.YELLOW);
        progressBar.setProgressTintList(colorStateList);
        agregar.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColor));
        int numeroPokemon = User1.getLastPokemon();
        String relativeUrl = "pokemon/" + numeroPokemon + "/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
        level.append(String.valueOf(User1.getLvl()));

        // Método para cambiar a Pokémon aleatorio
        pokemonChange.setOnClickListener(view -> {
            final int pokemonAleatorio = random.nextInt(max - min + 1) + min;
            User1.setLastPokemon(pokemonAleatorio);
            User1.addPokemon(String.valueOf(pokemonAleatorio)); // Agregar el Pokémon a la lista
            JsonHandler.saveJsonData(TaskActivity.this, User1); // Guardar localmente

            // Actualizar el perfil en Firestore
            updateUserProfileInFirestore(User1);

            String relativeUrl1 = "pokemon/" + pokemonAleatorio + "/";
            new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);
            Toast.makeText(this, "Cambio de Pokémon: " + pokemonAleatorio, Toast.LENGTH_SHORT).show();
        });

        // Método para irse al perfil
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un nuevo intent
                Intent intent = new Intent(TaskActivity.this, ProfileActivity.class);

                // Iniciar la segunda actividad
                startActivity(intent);
                Log.d("Perfil", "El pokemon es: " + User1.getLastPokemon() + ".");
            }
        });

        // Método para agregar y que se eliminen los checkbox agregados
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = findViewById(R.id.linearLay);

                // Crear un cuadro de diálogo para ingresar la tarea
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                builder.setTitle("Agregue la Tarea");

                // Agregar un campo de texto para la tarea
                final EditText input = new EditText(TaskActivity.this);
                input.setTextColor(Color.BLACK); // Establece el color del texto en negro
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

                        if (tarea.isEmpty()) {
                            // Mostrar un Toast indicando que no se puede agregar una tarea vacía
                            Toast.makeText(TaskActivity.this, "No se puede agregar una tarea vacía. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                        } else {
                            User1.addTarea(tarea); // Agregar la tarea a la lista
                            JsonHandler.saveJsonData(TaskActivity.this, User1); // Guardar localmente

                            // Actualizar Firestore
                            updateUserProfileInFirestore(User1);

                            // Agregar el CheckBox al LinearLayout
                            linearLayout.addView(checkBox);

                            // Aplica el estilo personalizado al CheckBox
                            checkBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));

                            // Agregar el CheckBox a la lista
                            checkBoxList.add(checkBox);
                        }
                    }
                });

                // Mostrar el cuadro de diálogo
                builder.show();
            }
        });

        // Botón para cerrar sesión
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Cerrar la sesión de Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Crear un nuevo intent para volver al login
                    Intent intent = new Intent(TaskActivity.this, LoginActivity.class);

                    // Limpiar la pila de actividades para evitar que el usuario vuelva atrás con el botón "atrás"
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Iniciar la actividad de inicio de sesión
                    startActivity(intent);
                    finish(); // Finaliza TaskActivity para evitar que quede en segundo plano
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(TaskActivity.this, "Error al cerrar sesión. Inténtelo nuevamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserProfileInFirestore(UserData user1) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Si no hay usuario autenticado, no se puede actualizar Firestore
            Log.e("Firestore", "No hay usuario autenticado");
            return;
        }

        String userEmail = currentUser.getEmail(); // Obtener el correo electrónico del usuario actual

        if (userEmail == null) {
            // Si no hay correo electrónico, no se puede buscar el documento
            Log.e("Firestore", "No se pudo obtener el correo electrónico del usuario");
            return;
        }

        // Buscar el documento en Firestore que tenga el mismo correo electrónico
        firestore.collection("users")
                .whereEqualTo("email", userEmail) // Buscar por correo electrónico
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Obtener el primer documento que coincida (debería ser único)
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Crear un mapa con los datos que quieres actualizar
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("level", user1.getLvl());
                        updates.put("experience", user1.getExp());
                        updates.put("lastPokemon", user1.getLastPokemon());
                        updates.put("pokedex", user1.getPokedex()); // Enviar la lista completa de Pokémon
                        updates.put("tareas", user1.getTarea()); // Enviar la lista completa de tareas

                        // Actualizar el documento encontrado
                        document.getReference()
                                .update(updates) // Actualizar solo los campos especificados
                                .addOnSuccessListener(aVoid -> {
                                    // La actualización fue exitosa
                                    Log.d("Firestore", "Perfil de usuario actualizado correctamente");
                                    Log.d("Firestore", "Pokedex actualizada: " + user1.getPokedex());
                                    Log.d("Firestore", "Tareas actualizadas: " + user1.getTarea());
                                })
                                .addOnFailureListener(e -> {
                                    // Hubo un error al actualizar el perfil
                                    Log.e("Firestore", "Error al actualizar el perfil de usuario", e);
                                });
                    } else {
                        // No se encontró el documento o hubo un error
                        Log.e("Firestore", "No se encontró el documento del usuario o hubo un error", task.getException());
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonHandler.saveJsonData(this, User1); // Guardar datos locales al pausar la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JsonHandler.saveJsonData(this, User1); // Guardar datos locales al destruir la actividad
    }
}