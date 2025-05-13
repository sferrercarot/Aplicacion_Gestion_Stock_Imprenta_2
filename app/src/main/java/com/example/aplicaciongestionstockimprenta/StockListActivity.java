package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductsAdapter adapter;
    private OdooService service;
    private final List<Product> productos = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private String rol;
    private int uid;
    private String usuario;
    private String password;

    private static final int REQUEST_UPDATE_STOCK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        Log.d("STOCK", "Entrando en StockListActivity");

        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");

        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(this, productos, uid, password, this::abrirDetalleProducto);
        recyclerView.setAdapter(adapter);

        service = RetrofitClient.getOdooService();

        cargarProductos();
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObject body = OdooRequestBuilder.buildSearchReadRequest(
                "gestion_almacen", uid, password, "gestion_almacen.producto",
                new String[]{"id", "name", "cantidad_stock", "stock_bajo"}
        );

        service.searchRead(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                executor.execute(() -> {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject json = response.body();
                            Log.d("STOCK", "📦 Respuesta: " + json.toString());

                            List<Product> nuevosProductos = new ArrayList<>();

                            // CORRECCIÓN AQUÍ
                            if (json.has("result") && json.get("result").isJsonArray()) {
                                JsonArray resultArray = json.getAsJsonArray("result");

                                for (JsonElement elem : resultArray) {
                                    if (elem.isJsonObject()) {
                                        JsonObject p = elem.getAsJsonObject();
                                        int id = p.get("id").getAsInt();
                                        String name = p.get("name").getAsString();
                                        int stock = p.get("cantidad_stock").getAsInt();
                                        boolean bajo = p.get("stock_bajo").getAsBoolean();
                                        nuevosProductos.add(new Product(id, name, stock, bajo));
                                    }
                                }
                            }

                            runOnUiThread(() -> {
                                productos.clear();
                                productos.addAll(nuevosProductos);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                if (nuevosProductos.isEmpty()) {
                                    Toast.makeText(StockListActivity.this, "No hay productos", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(StockListActivity.this, "Error al obtener productos", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(StockListActivity.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                            Log.e("STOCK", "❌ Excepción al procesar productos", e);
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StockListActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                    Log.e("STOCK", "❌ Error de red", t);
                });
            }
        });
    }

    private void abrirDetalleProducto(Product producto) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("id", producto.getId());
        intent.putExtra("name", producto.getName());
        intent.putExtra("cantidad_stock", producto.getCantidadStock());
        intent.putExtra("uid", uid);
        intent.putExtra("password", password);
        startActivityForResult(intent, REQUEST_UPDATE_STOCK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_STOCK && resultCode == RESULT_OK && data != null) {
            int idActualizado = data.getIntExtra("id_actualizado", -1);
            int nuevoStock = data.getIntExtra("nuevo_stock", -1);

            if (idActualizado != -1 && nuevoStock != -1) {
                for (Product p : productos) {
                    if (p.getId() == idActualizado) {
                        p.setCantidadStock(nuevoStock);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Stock actualizado correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No recargamos toda la lista para no sobreescribir el cambio manual
    }
}
