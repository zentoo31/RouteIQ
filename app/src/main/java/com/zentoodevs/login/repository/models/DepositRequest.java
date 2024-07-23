package com.zentoodevs.login.repository.models;

public class DepositRequest {
    private String usuario;
    private double monto;
    private String descripcion;
    private String bus;

    public DepositRequest(String usuario, double monto, String descripcion, String bus) {
        this.usuario = usuario;
        this.monto = monto;
        this.descripcion = descripcion;
        this.bus = bus;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public double getMonto() {
        return monto;
    }

    public String getBus() {
        return bus;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}