package com.msulov.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msulov.myproject.Models.User;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnLogIn;
    RelativeLayout relativeLayout;

    FirebaseAuth auth;              // Для авторизации
    FirebaseDatabase db;            // Для подключения к базе данных
    DatabaseReference users;        // Для работы с табличками в базе данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.button_sign);
        btnLogIn = findViewById(R.id.button_login);
        relativeLayout = findViewById(R.id.relative_layout);

        auth = FirebaseAuth.getInstance();          // Запускаем авторизацию в базе данных
        db = FirebaseDatabase.getInstance();        // Подключаемся к базе данных
        users = db.getReference("Users");     // Даем название табличке

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginWindow();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignWindow();
            }
        });
    }

    private void showLoginWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);  // Создаем диалоговое окно
        dialog.setTitle("Зарегистрироваться");
        dialog.setMessage("Введите все данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);    // Создаем объект для получения нашего шаблона
        View loginWindow = inflater.inflate(R.layout.login_window, null);   // Передаем шаблон в другой obj

        dialog.setView(loginWindow);

        final EditText email = loginWindow.findViewById(R.id.emailField);
        final EditText password = loginWindow.findViewById(R.id.passwordField);
        final EditText name = loginWindow.findViewById(R.id.nameField);
        final EditText phone = loginWindow.findViewById(R.id.phoneField);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(relativeLayout, "Введите Email", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().length() < 6) {
                    Snackbar.make(relativeLayout, "Пароль должен состоять как минимум из 6 символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(relativeLayout, "Введите ваше имя", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(relativeLayout, "Введите номер телефона", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Регистрация пользователя
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            User user = new User();
                            user.setEmail(email.getText().toString());
                            user.setPassword(password.getText().toString());
                            user.setName(name.getText().toString());
                            user.setPhone(phone.getText().toString());

                            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(relativeLayout, "Авторизация прошла успешно!", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(relativeLayout, e.getMessage().toString(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }

    private void showSignWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);  // Создаем диалоговое окно
        dialog.setTitle("Войти");
        dialog.setMessage("Введите данные для входа");

        LayoutInflater inflater = LayoutInflater.from(this);    // Создаем объект для получения нашего шаблона
        View singWindow = inflater.inflate(R.layout.sing_window, null);   // Передаем шаблон в другой obj

        dialog.setView(singWindow);

        final EditText email = singWindow.findViewById(R.id.emailField);
        final EditText password = singWindow.findViewById(R.id.passwordField);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(relativeLayout, "Введите Email", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(relativeLayout, "Введите пароль", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                     .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                         @Override
                         public void onSuccess(AuthResult authResult) {
                             startActivity(new Intent(MainActivity.this, UserActivity.class));
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(relativeLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                });
            }
        });

        dialog.show();
    }
}