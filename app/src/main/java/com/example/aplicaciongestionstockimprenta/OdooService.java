package com.example.aplicaciongestionstockimprenta;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OdooService {

    @Headers("Content-Type: application/json")
    @POST("/web/session/authenticate")
    Call<JsonObject> login(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/api/rol_usuario")
    Call<RolWrapper> obtenerRol(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/jsonrpc")
    Call<JsonObject> searchRead(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/api/actualizar_stock")
    Call<ResultadoWrapper> actualizarStock(@Body JsonObject body);
}
