package com.zentoodevs.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zentoodevs.login.R;
import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.viewmodel.StudentViewModel;
import com.zentoodevs.login.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private StudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        TextView btnLoginRegistrate = findViewById(R.id.textViewRegistrate);
        btnLoginRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Boton test
        Button btnTest = findViewById(R.id.testButton);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        // Obtener el id del boton para ingresar dentro de la actividad LoginActivity
        Button btnLogin = findViewById(R.id.btnIngresar);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el contenido de etxtMail

                TextView editTextMail = findViewById(R.id.etxtMail);
                String mail = editTextMail.getText().toString().trim();
                // Verificar si el campo de correo electrónico no está vacío
                if (!TextUtils.isEmpty(mail.trim())) {
                    // Cargar los datos del estudiante
                    studentViewModel.loadStudent(mail);
                } else {
                    // Mostrar un mensaje de error si el campo de correo electrónico está vacío
                    Toast.makeText(LoginActivity.this, "El campo de correo electrónico está vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observar los cambios en el LiveData de error
        studentViewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                // Mostrar un Toast con el mensaje de error
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar los cambios en los datos del estudiante
        studentViewModel.getStudentLiveData().observe(this, new Observer<Student>() {
            @Override
            public void onChanged(Student student) {
                if (student != null) {
                    // Verificar si el correo electrónico y la contraseña son correctos
                    TextView editTextMail = findViewById(R.id.etxtMail);
                    String mail = editTextMail.getText().toString().trim();
                    TextView editTextPassword = findViewById(R.id.etxtPassword);
                    String password = editTextPassword.getText().toString().trim();
                    if (student.getUsuario().equalsIgnoreCase(mail) && student.getContrasena().equalsIgnoreCase(password)) {
                        // Abrir la actividad de mapas
                        Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                        intent.putExtra("user",mail);
                        startActivity(intent);
                        finish();
                    } else {
                        // Mostrar un mensaje de error si el correo electrónico o la contraseña son incorrectos
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("DEBUG", "student.getId(): \"" + student.getUsuario() + "\", length: " + student.getContrasena().length());
                    Log.d("DEBUG", "mail: \"" + mail + "\", length: " + mail.length());
                    Log.d("DEBUG", "student.getDni(): \"" + student.getUsuario() + "\", length: " + student.getContrasena().length());
                    Log.d("DEBUG", "password: \"" + password + "\", length: " + password.length());

                }
            }
        });
    }
}