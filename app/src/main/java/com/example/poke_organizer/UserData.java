package com.example.poke_organizer;

import java.util.ArrayList;

public class UserData {

    private String nombre;
    private String email;
    private String password;
    private int level;
    private int experience;
    private int lastPokemon;
    private ArrayList<String> pokedex;
    private ArrayList<String> tareas;

    // Constructor con parámetros
    public UserData(String nombre, String password, String email) {
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.pokedex = new ArrayList<>();
        this.tareas = new ArrayList<>();
    }

    // Constructor vacío para Firestore
    public UserData() {
        this.pokedex = new ArrayList<>();
        this.tareas = new ArrayList<>();
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLastPokemon() {
        return lastPokemon;
    }

    public void setLastPokemon(int lastPokemon) {
        this.lastPokemon = lastPokemon;
    }

    public ArrayList<String> getPokedex() {
        return pokedex;
    }

    public void setPokedex(ArrayList<String> pokedex) {
        this.pokedex = pokedex;
    }

    public ArrayList<String> getTareas() {
        return tareas;
    }

    public void setTareas(ArrayList<String> tareas) {
        this.tareas = tareas;
    }

    // Métodos adicionales
    public void addPokemon(String pokemon) {
        if (this.pokedex == null) {
            this.pokedex = new ArrayList<>();
        }
        this.pokedex.add(pokemon);
    }

    public void addTarea(String tarea) {
        if (this.tareas == null) {
            this.tareas = new ArrayList<>();
        }
        this.tareas.add(tarea);
    }

    public void removeTarea(String tarea) {
        if (this.tareas != null && this.tareas.contains(tarea)) {
            this.tareas.remove(tarea);

        }
    }
}