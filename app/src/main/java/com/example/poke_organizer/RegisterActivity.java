package com.example.poke_organizer;

import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference(); // Inicializa la referencia a la base de datos
        EditText name = findViewById(R.id.editTextName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editPassword2);  // Agregamos la referencia al campo de contraseña
        Button register = findViewById(R.id.register);
        int min = 1;
        int max = 1010;
        Random random = new Random();

        register.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColorDark));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString(); // Obtener la contraseña

                if (nameText.isEmpty() || emailText.isEmpty() ||  passwordText.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar si el correo electrónico ya está registrado en la base de datos de Firebase
                    database.child("users").orderByChild("email").equalTo(emailText).get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().exists()) {
                                        // Si el correo no está registrado, lo guardamos en Firebase Database
                                        saveUserToDatabase(nameText, emailText, passwordText, random);
                                    } else {
                                        // El correo ya está registrado
                                        Toast.makeText(RegisterActivity.this, "Este correo ya está registrado.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void saveUserToDatabase(String name, String email, String password, Random random) {
        // Crea un objeto User con los datos
        UserData user = new UserData(name, email, password);
        final int pokemonAleatorio = random.nextInt(1010 - 1 + 1) + 1;
        user.setLastPokemon(pokemonAleatorio);
        user.addPokemon(String.valueOf(pokemonAleatorio));
        user.setLvl(1);

        // Guardamos los datos en Firebase Database bajo un nodo "users"
        String userId = database.child("users").push().getKey();
        if (userId != null) {
            database.child("users").child(userId).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Informar al usuario que el registro fue exitoso
                                Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_LONG).show();

                                // Finalizar la actividad actual
                                finish();
                            } else {
                                // En caso de error, notificar al usuario
                                Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
