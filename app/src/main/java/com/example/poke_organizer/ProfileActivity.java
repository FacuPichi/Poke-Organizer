package com.example.poke_organizer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private UserData User; // Declarar la variable User aquí
    private FirebaseFirestore firestore;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance();

        Button tareas = findViewById(R.id.tareas);
        // Cargar datos del usuario desde Firestore
        loadUserDataFromFirestore();
        // Configurar el botón de tareas
        tareas.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TaskActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserDataFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Redirigir al usuario a la actividad de registro o inicio de sesión
            redirectToLoginActivity();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Log.e("Firestore", "No se pudo obtener el correo electrónico del usuario");
            return;
        }

        firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User = document.toObject(UserData.class); // Asignar los datos a User
                        if (User != null) {
                            // Actualizar la UI con los datos del usuario
                            updateUI(User);
                        } else {
                            Log.e("Firestore", "El documento del usuario es nulo");
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener los datos del usuario", task.getException());
                    }
                });
    }

    private void updateUI(UserData user) {
        TextView level = findViewById(R.id.nivelUsuario);
        TextView name = findViewById(R.id.nombreUsuario);
        TextView experience = findViewById(R.id.profileExp);
        TextView pokename = findViewById(R.id.pokename2);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        progressBar = findViewById(R.id.profileProgressBar);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));

        // Actualizar los textos y la imagen
        level.setText("Nivel: " + user.getLevel());
        name.setText(user.getNombre());
        experience.setText(user.getExperience() + " / 100 exp");
        progressBar.setProgress(user.getExperience());
        // Cargar el sprite del último Pokémon
        String relativeUrl = "pokemon/" + user.getLastPokemon() + "/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);

        // Actualizar la Pokédex
        updatePokedexUI(user.getPokedex());
    }

    private void updatePokedexUI(ArrayList<String> pokedex) {
        TableLayout tableLayout = findViewById(R.id.pokedex);
        tableLayout.removeAllViews(); // Limpiar la tabla antes de agregar nuevas filas

        int maxImagesPerRow = 4; // Número máximo de imágenes por fila
        TableRow tableRow = new TableRow(this);

        for (int i = 0; i < pokedex.size(); i++) {
            ImageView imageView = new ImageView(this);
            String relativeUrl = "pokemon/" + pokedex.get(i) + "/";

            // Configurar el clic en la imagen
            int finalI = i;
            imageView.setOnClickListener(v -> showPokemonOptions(pokedex, finalI, relativeUrl));

            // Cargar la imagen del Pokémon
            TextView dummyTextView = new TextView(this); // TextView dummy, no se mostrará
            GetPokemonInfo getPokemonInfo = new GetPokemonInfo(dummyTextView, imageView);
            getPokemonInfo.execute(relativeUrl);

            // Añadir la imagen a la fila
            tableRow.addView(imageView);

            // Si se alcanza el número máximo de imágenes por fila o es el último Pokémon, añadir la fila al TableLayout
            if ((i + 1) % maxImagesPerRow == 0 || i == pokedex.size() - 1) {
                tableLayout.addView(tableRow);
                tableRow = new TableRow(this); // Crear una nueva fila para la siguiente línea de imágenes
            }
        }
    }

    private void showPokemonOptions(ArrayList<String> pokedex, int index, String relativeUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Qué quieres hacer?");
        builder.setMessage("¿Quieres que este Pokémon pase a ser tu nuevo compañero o quieres ver sus estadísticas?");

        // Opción para hacer que el Pokémon sea el nuevo compañero
        builder.setPositiveButton("Hacer compañero", (dialog, which) -> {
            int selectedPokemon = Integer.parseInt(pokedex.get(index)); // Obtiene el ID del Pokémon seleccionado
            updateLastPokemonInFirestore(selectedPokemon); // Actualiza el último Pokémon en Firestore
        });

        // Opción para ver las estadísticas del Pokémon
        builder.setNegativeButton("Ver estadísticas", (dialog, which) -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("POKEMON_URL", relativeUrl);
            startActivity(intent);
        });

        builder.show();
    }

    private void updateLastPokemonInFirestore(int lastPokemon) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Firestore", "No hay usuario autenticado");
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Log.e("Firestore", "No se pudo obtener el correo electrónico del usuario");
            return;
        }

        firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId(); // Obtiene el ID del documento
                        firestore.collection("users").document(userId)
                                .update("lastPokemon", lastPokemon)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Compañero actualizado en Firestore");
                                    loadUserDataFromFirestore(); // Recargar los datos del usuario
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar compañero", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario en Firestore", e));
    }

    private void redirectToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDataFromFirestore(); // Recargar los datos del usuario desde Firestore
    }
}