package com.example.poke_organizer;

import java.util.ArrayList;

public class UserData {

    private String nombre;
    private int lvl;
    private int exp;
    private int LastPokemon;
    private ArrayList<String> Pokedex;

    public UserData(String nombre, int lvl, int exp, int lastPokemon) {
        this.nombre = nombre;
        this.lvl = lvl;
        this.exp = exp;
        this.LastPokemon = lastPokemon;
        this.Pokedex = new ArrayList<String>();
    }

    public String getNombre() {
        return nombre;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExp() {
        return exp;
    }

    public int getLastPokemon() {
        return LastPokemon;
    }

    public void setLastPokemon(int lastPokemon) {
        this.LastPokemon = lastPokemon;
    }

    public ArrayList<String> getPokedex() {
        return Pokedex;
    }

    public void setPokedex(String pokemon) {
        this.Pokedex.add(pokemon);
    }
}
