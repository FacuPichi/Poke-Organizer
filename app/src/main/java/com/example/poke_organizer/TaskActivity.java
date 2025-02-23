package com.example.poke_organizer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TaskActivity extends AppCompatActivity {

    // Declarar Firestore
    private FirebaseFirestore firestore;
    private UserData User1; // Variable para almacenar los datos del usuario

    // Declarar vistas
    private TextView pokename, level, experience;
    private ImageView pokeSprite;
    private ProgressBar progressBar;
    private Button pokemonChange, perfil, login, agregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews(); // Inicializar vistas
        initializeFirestore(); // Inicializar Firestore
        loadUserData(); // Cargar datos del usuario desde Firestore
        setupPokemonChangeButton(); // Configurar botón de cambio de Pokémon
        setupProfileButton(); // Configurar botón de perfil
        setupAddTaskButton(); // Configurar botón para agregar tareas
        setupLogoutButton(); // Configurar botón de cierre de sesión
    }

    // Inicializar las vistas
    private void initializeViews() {
        pokename = findViewById(R.id.pokename);
        level = findViewById(R.id.level);
        pokeSprite = findViewById(R.id.PokeSprite);
        progressBar = findViewById(R.id.progressBar);
        pokemonChange = findViewById(R.id.pokemonChange);
        experience = findViewById(R.id.exp);
        perfil = findViewById(R.id.perfil);
        login = findViewById(R.id.login);
        agregar = findViewById(R.id.agregar);

        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        agregar.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColor));
    }

    // Inicializar Firestore
    private void initializeFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Cargar datos del usuario desde Firestore
    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            redirectToRegisterActivity(); // Redirigir si no hay usuario autenticado
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
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            User1 = document.toObject(UserData.class); // Convertir documento a UserData
                            if (User1 != null) {
                                // Actualizar la UI con los datos del usuario
                                level.setText("Nivel: " + User1.getLevel());
                                loadTasks(); // Cargar tareas del usuario
                                updateUI(); // Actualizar la interfaz de usuario
                                loadPokemonSprite(User1.getLastPokemon()); // Cargar sprite del Pokémon
                            } else {
                                Log.e("Firestore", "El documento del usuario es nulo");
                            }
                        } else {
                            Log.e("Firestore", "No se encontró ningún documento para el usuario");
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener los datos del usuario", task.getException());
                    }
                });
    }

    // Redirigir a la actividad de registro
    private void redirectToRegisterActivity() {
        Intent intent = new Intent(TaskActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    // Cargar tareas del usuario desde Firestore
    private void loadTasks() {
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

        // Obtener las tareas desde Firestore
        firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        List<String> tareas = (List<String>) document.get("tareas");

                        if (tareas != null && !tareas.isEmpty()) {
                            LinearLayout linearLayout = findViewById(R.id.linearLay);
                            for (String tarea : tareas) {
                                createCheckBoxForTask(tarea, linearLayout); // Crear CheckBox para cada tarea
                            }
                        } else {
                            Log.d("Firestore", "No hay tareas para este usuario");
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener las tareas", task.getException());
                    }
                });
    }

    // Crear un CheckBox para una tarea y agregarlo al layout
    private void createCheckBoxForTask(String tarea, LinearLayout linearLayout) {
        CheckBox checkBox = new CheckBox(TaskActivity.this);
        checkBox.setText(tarea);
        linearLayout.addView(checkBox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleTaskCompletion(buttonView, linearLayout); // Manejar la completitud de la tarea
            }
        });
    }

    // Manejar la completitud de una tarea
    private void handleTaskCompletion(CompoundButton buttonView, LinearLayout linearLayout) {
        String textoCheckBox = ((CheckBox) buttonView).getText().toString();
        User1.setExperience(User1.getExperience() + 10); // Sumar experiencia ganada
        updateUI(); // Actualizar la UI
        User1.removeTarea(textoCheckBox); // Eliminar tarea completada

        // Actualizar Firestore
        updateUserProfileInFirestore(User1);

        linearLayout.removeView(buttonView); // Eliminar CheckBox del layout
    }

    // Actualizar la interfaz de usuario
    private void updateUI() {
        experience.setText(User1.getExperience() + " / 100 exp");
        progressBar.setProgress(User1.getExperience());
        if (User1.getExperience() >= 100) {
            handleLevelUp(); // Manejar subida de nivel
        }
    }

    // Manejar la subida de nivel
    private void handleLevelUp() {
        User1.setExperience(0);
        User1.setLevel(User1.getLevel() + 1);
        Log.d("TaskActivity", "Nuevo nivel: " + User1.getLevel());

        int pokemonAleatorio = new Random().nextInt(1010) + 1; // Generar un Pokémon aleatorio
        User1.setLastPokemon(pokemonAleatorio);
        User1.addPokemon(String.valueOf(pokemonAleatorio));

        level.setText("Nivel: " + User1.getLevel());
        experience.setText(User1.getExperience() + " / 100 exp");
        progressBar.setProgress(User1.getExperience());

        Toast.makeText(TaskActivity.this, "¡Felicidades has subido de nivel! ¡Haz descubierto un nuevo Pokémon!", Toast.LENGTH_SHORT).show();
        loadPokemonSprite(pokemonAleatorio); // Cargar el sprite del nuevo Pokémon
    }

    // Configurar el botón de cambio de Pokémon
    private void setupPokemonChangeButton() {
        pokemonChange.setOnClickListener(view -> {
            int pokemonAleatorio = new Random().nextInt(1010) + 1; // Generar un Pokémon aleatorio
            User1.setLastPokemon(pokemonAleatorio);
            User1.addPokemon(String.valueOf(pokemonAleatorio));

            // Actualizar Firestore
            updateUserProfileInFirestore(User1);

            loadPokemonSprite(pokemonAleatorio); // Cargar el sprite del nuevo Pokémon
            Toast.makeText(this, "Cambio de Pokémon: " + pokemonAleatorio, Toast.LENGTH_SHORT).show();
        });
    }

    // Configurar el botón de perfil
    private void setupProfileButton() {
        perfil.setOnClickListener(v -> {
            Intent intent = new Intent(TaskActivity.this, ProfileActivity.class);
            startActivity(intent);
            Log.d("Perfil", "El pokemon es: " + User1.getLastPokemon() + ".");
        });
    }

    // Configurar el botón para agregar tareas
    private void setupAddTaskButton() {
        agregar.setOnClickListener(v -> {
            LinearLayout linearLayout = findViewById(R.id.linearLay);
            showAddTaskDialog(linearLayout); // Mostrar diálogo para agregar tarea
        });
    }

    // Mostrar diálogo para agregar una tarea
    private void showAddTaskDialog(LinearLayout linearLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
        builder.setTitle("Agregue la Tarea");

        final EditText input = new EditText(TaskActivity.this);
        input.setTextColor(Color.BLACK);
        builder.setView(input);

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String tarea = input.getText().toString();
            if (tarea.isEmpty()) {
                Toast.makeText(TaskActivity.this, "No se puede agregar una tarea vacía. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
            } else {
                User1.addTarea(tarea); // Agregar tarea al usuario

                // Actualizar Firestore
                updateUserProfileInFirestore(User1);

                createCheckBoxForTask(tarea, linearLayout); // Crear CheckBox para la nueva tarea
            }
        });

        builder.show();
    }

    // Configurar el botón de cierre de sesión
    private void setupLogoutButton() {
        login.setOnClickListener(v -> {
            try {
                FirebaseAuth.getInstance().signOut(); // Cerrar sesión
                Intent intent = new Intent(TaskActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(TaskActivity.this, "Error al cerrar sesión. Inténtelo nuevamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Actualizar el perfil del usuario en Firestore
    private void updateUserProfileInFirestore(UserData user) {
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("level", user.getLevel());
                        updates.put("experience", user.getExperience());
                        updates.put("lastPokemon", user.getLastPokemon());
                        updates.put("pokedex", user.getPokedex());
                        updates.put("tareas", user.getTareas());

                        document.getReference()
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Perfil de usuario actualizado correctamente");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al actualizar el perfil de usuario", e);
                                });
                    } else {
                        Log.e("Firestore", "No se encontró el documento del usuario o hubo un error", task.getException());
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserProfileInFirestore(User1); // Actualizar Firestore al pausar la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserProfileInFirestore(User1); // Actualizar Firestore al destruir la actividad
    }

    // Cargar el sprite del Pokémon desde la API
    private void loadPokemonSprite(int pokemonId) {
        String relativeUrl = "pokemon/" + pokemonId + "/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
    }
}