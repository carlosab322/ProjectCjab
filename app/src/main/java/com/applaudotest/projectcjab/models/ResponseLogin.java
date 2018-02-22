package com.applaudotest.projectcjab.models;

/**
 * Created by victor.hernandez on 13/07/2017.
 */

public class ResponseLogin {

    private int id_usuario;
    private String nombres;
    private String apellidos;
    private String email;
    private String lifemiles;
    private int os;
    private int pais;
    private boolean inicio_sesion_exitoso;
    private String telefono;
    private String codigo_respuesta;
    private String mensaje;

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLifemiles() {
        return lifemiles;
    }

    public void setLifemiles(String lifemiles) {
        this.lifemiles = lifemiles;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public int getPais() {
        return pais;
    }

    public void setPais(int pais) {
        this.pais = pais;
    }

    public boolean isInicio_sesion_exitoso() {
        return inicio_sesion_exitoso;
    }

    public void setInicio_sesion_exitoso(boolean inicio_sesion_exitoso) {
        this.inicio_sesion_exitoso = inicio_sesion_exitoso;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCodigo_respuesta() {
        return codigo_respuesta;
    }

    public void setCodigo_respuesta(String codigo_respuesta) {
        this.codigo_respuesta = codigo_respuesta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
