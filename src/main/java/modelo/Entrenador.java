/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author paabl
 */
public class Entrenador {
        private int id;
        private String nombre;
        private int edad;
        private int numMedallas;
        private String region;
        private String sexo;

        public Entrenador(int id, String nombre, int edad, int numMedallas, String region, String sexo) {
            this.id = id;
            this.nombre = nombre;
            this.edad = edad;
            this.numMedallas = numMedallas;
            this.region = region;
            this.sexo = sexo;
        }

        // Getters y setters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public void setID(int Id) { this.id = Id; }
        public int getEdad() { return edad; }
        public void setEdad(int edad) { this.edad = edad; }
        public int getNumMedallas() { return numMedallas; }
        public void setNumMedallas(int numMedallas) { this.numMedallas = numMedallas; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getSexo() { return sexo; }
        public void setSexo(String sexo) { this.sexo = sexo; }
    }
