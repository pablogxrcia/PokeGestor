/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
public class Pokemon {
    private int idPokemon;
    private String nombrePokemon;
    private int nivelPokemon;
    private String tipo;
    private int numPokedex;
    private String imagen;

    // Constructor
    public Pokemon(int idPokemon, String nombrePokemon, int nivelPokemon, String tipo, int numPokedex, String URLimagen) {
        this.idPokemon = idPokemon;
        this.nombrePokemon = nombrePokemon;
        this.nivelPokemon = nivelPokemon;
        this.tipo = tipo;
        this.numPokedex = numPokedex;
        this.imagen = URLimagen;
    }

    // Getters y Setters
    public int getIdPokemon() {
        return idPokemon;
    }

    public void setIdPokemon(int idPokemon) {
        this.idPokemon = idPokemon;
    }

    public String getNombrePokemon() {
        return nombrePokemon;
    }

    public void setNombrePokemon(String nombrePokemon) {
        this.nombrePokemon = nombrePokemon;
    }

    public int getNivelPokemon() {
        return nivelPokemon;
    }

    public void setNivelPokemon(int nivelPokemon) {
        this.nivelPokemon = nivelPokemon;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getNumPokedex() {
        return numPokedex;
    }

    public void setNumPokedex(int numPokedex) {
        this.numPokedex = numPokedex;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
}

