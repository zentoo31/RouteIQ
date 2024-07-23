package com.zentoodevs.login.repository.models;

public class Transaction {
    private int transaccionID;
    private int billeteraID;
    private String bus;
    private String tipoTransaccion;
    private double monto;
    private String fechaTransaccion;
    private String descripcion;

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public String getBus() {
        return bus;
    }

    public int getBilleteraID() {
        return billeteraID;
    }

    public int getTransaccionID() {
        return transaccionID;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
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

    public void setBilleteraID(int billeteraID) {
        this.billeteraID = billeteraID;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public void setTransaccionID(int transaccionID) {
        this.transaccionID = transaccionID;
    }
}
