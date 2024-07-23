package com.zentoodevs.login.repository;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.zentoodevs.login.repository.callbacks.StudentRepositoryCallback;
import com.zentoodevs.login.repository.models.PayRequest;
import com.zentoodevs.login.repository.models.DepositRequest;
import com.zentoodevs.login.repository.models.Payment;
import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.repository.models.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentRepository {
    @SuppressLint("StaticFieldLeak")
    public void getStudent(StudentRepositoryCallback callback, String xd){
        new AsyncTask<Void, Void, Student>() {
            @Override
            protected Student doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://192.168.18.12:5000/usuarios/" + xd);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        reader.close();

                        Gson gson = new Gson();
                        Student student = gson.fromJson(stringBuilder.toString(), Student.class);

                        return student;
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Llamar a onError en el hilo principal (UI thread)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e.getMessage());
                        }
                    });
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Student student) {
                if (student != null){
                    // Llamar a onStudentLoaded en el hilo principal (UI thread)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onStudentLoaded(student);
                        }
                    });
                }
            }
        }.execute();
    }
    @SuppressLint("StaticFieldLeak")
    public void addStudent(Student student, StudentRepositoryCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://192.168.18.12:5000/usuarios");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    Gson gson = new Gson();
                    String jsonStudent = gson.toJson(student);
                    Log.d("XD", jsonStudent);
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(jsonStudent.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        // Éxito al agregar el estudiante
                        Log.d("StudentRepository", "Estudiante agregado exitosamente");
                    } else {
                        // Error al agregar el estudiante
                        callback.onError("Error al agregar el estudiante");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Error de conexión
                    callback.onError("Error de conexión");
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void realizarPago(String usuario, Payment payment, StudentRepositoryCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://192.168.18.12:5000/pagar");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    // Añadir usuario al objeto Payment
                    payment.setUsuario(usuario);

                    Gson gson = new Gson();
                    String jsonPayment = gson.toJson(payment);
                    Log.d("Payment", "JSON enviado: " + jsonPayment);
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(jsonPayment.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = urlConnection.getResponseCode();
                    Log.d("Payment", "Código de respuesta: " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Éxito al realizar el pago
                        Log.d("StudentRepository", "Pago realizado exitosamente");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess();
                            }
                        });
                    } else {
                        // Error al realizar el pago
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        reader.close();
                        String errorResponse = stringBuilder.toString();
                        Log.e("Payment", "Error al realizar el pago: " + errorResponse);
                        callback.onError("Error al realizar el pago: " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Error de conexión
                    callback.onError("Error de conexión");
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                return null;
            }
        }.execute();
    }



    @SuppressLint("StaticFieldLeak")
    public void Deposit(String usuario, double monto, String descripcion, String bus, StudentRepositoryCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://192.168.18.12:5000/deposito");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    // Crear el JSON para la solicitud
                    Gson gson = new Gson();
                    DepositRequest depositoRequest = new DepositRequest(usuario, monto, descripcion, bus);
                    String jsonDeposito = gson.toJson(depositoRequest);
                    Log.d("StudentRepository", jsonDeposito);

                    // Enviar la solicitud
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(jsonDeposito.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Éxito al realizar el depósito
                        Log.d("StudentRepository", "Depósito realizado exitosamente");
                        callback.onSuccess();
                    } else {
                        // Error al realizar el depósito
                        callback.onError("Error al realizar el depósito");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Error de conexión
                    callback.onError("Error de conexión");
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                return null;
            }
        }.execute();
    }


    @SuppressLint("StaticFieldLeak")
    public void obtenerTransacciones(String usuario, StudentRepositoryCallback callback) {
        new AsyncTask<Void, Void, List<Transaction>>() {
            @Override
            protected List<Transaction> doInBackground(Void... voids) {
                List<Transaction> transacciones = new ArrayList<>();
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://192.168.18.12:5000/transacciones/" + usuario);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();

                    Gson gson = new Gson();
                    Transaction[] transArray = gson.fromJson(stringBuilder.toString(), Transaction[].class);
                    transacciones = Arrays.asList(transArray);

                    return transacciones;
                } catch (IOException e) {
                    e.printStackTrace();
                    // Llamar a onError en el hilo principal (UI thread)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e.getMessage());
                        }
                    });
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(List<Transaction> transactions) {
                if (transactions != null) {
                    // Llamar a onTransaccionesLoaded en el hilo principal (UI thread)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onTransactionsLoaded(transactions);
                        }
                    });
                }
            }
        }.execute();
    }


}


