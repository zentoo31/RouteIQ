package com.zentoodevs.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.zentoodevs.login.R;
import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.viewmodel.StudentViewModel;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private StudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView btnRegisterLogin = findViewById(R.id.textViewIniciarSesion);
        btnRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Inicializar el ViewModel del estudiante
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        createStudent();
    }

    public void createStudent() {
        Button btnRegister = findViewById(R.id.btnRegistrarse);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtUser = findViewById(R.id.user);
                String user = txtUser.getText().toString().trim();
                TextView txtMail = findViewById(R.id.mail);
                String mail =  txtMail.getText().toString().trim();
                TextView txtName = findViewById(R.id.nombres);
                String nombre = txtName.getText().toString().trim();
                TextView txtApellido = findViewById(R.id.apellidos);
                String apellido = txtApellido.getText().toString().trim();
                TextView txtPassword = findViewById(R.id.password);
                String password = txtPassword.getText().toString().trim();

                // Verificar si se han ingresado todos los datos
                if (user.isEmpty() || nombre.isEmpty() ||mail.isEmpty() ||apellido.isEmpty() || password.isEmpty()) {
                    // Mostrar un Toast indicando que falta algún dato
                    Toast.makeText(RegisterActivity.this, "Falta algún dato", Toast.LENGTH_SHORT).show();
                } else {
                    // Crear un nuevo objeto Student con los datos ingresados
                    Student newStudent = new Student(user, password, apellido, nombre, mail);

                    // Llamar al método addStudent del ViewModel para agregar el estudiante
                    studentViewModel.addStudent(newStudent);
                    // Mostrar un Toast indicando que el estudiante se ha registrado correctamente
                    Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
