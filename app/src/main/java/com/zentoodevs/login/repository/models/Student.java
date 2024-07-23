package com.zentoodevs.login.repository.models;

public class Student {
    private String usuario;
    private String contrasena;
    private String apellido;
    private String nombre;
    private String correo;
    private String photo;

    public Student(String usuario, String contrasena, String apellido, String nombre, String correo){
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.apellido = apellido;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPhoto(){
        return photo;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }
}
