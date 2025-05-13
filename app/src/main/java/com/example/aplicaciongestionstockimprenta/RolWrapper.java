package com.example.aplicaciongestionstockimprenta;

public class RolWrapper {
    public String jsonrpc;
    public int id;
    public RolResponse result;

    public static class RolResponse {
        public String rol;
        public String usuario;
        public int id;
    }
}

