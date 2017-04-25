package com.suminjin.mytownweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.suminjin.appbase.CustomProgressDialog;

/**
 * Created by parkjisun on 2017. 4. 25..
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String INTENT_EXTRA_IS_SIGNING_UP = "is_signing_up";
    public static final String INTENT_EXTRA_EMAIL = "email";
    private static final int REQUEST_CODE_SIGN_UP = 0;

    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordConfirm;

    private boolean isSigningUp; // 회원가입이면 true, 로그인이면 false
    private AuthManager authManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView btnLogin = (TextView) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        View textSignUp = findViewById(R.id.text_sign_up);
        textSignUp.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editPasswordConfirm = (EditText) findViewById(R.id.edit_password_confirm);

        isSigningUp = getIntent().getBooleanExtra(INTENT_EXTRA_IS_SIGNING_UP, false);

        if (isSigningUp) {
            editPasswordConfirm.setVisibility(View.VISIBLE);
            textSignUp.setVisibility(View.GONE);
            btnLogin.setText(R.string.sign_up);
        } else {
            String email = AppData.get(this, AppData.KEY_ID, null);
            if (email != null) {
                editEmail.setText(email);
            }
        }

        authManager = new AuthManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (isSigningUp) { // 회원가입
                    final String email = editEmail.getText().toString();
                    String password = editPassword.getText().toString();
                    String passwordConfirm = editPasswordConfirm.getText().toString();
                    if (!authManager.isValidEmail(email)) {
                        Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();
                    } else if (!authManager.isValidPassword(password)) {
                        Toast.makeText(this, "invalid password", Toast.LENGTH_SHORT).show();
                    } else if (!password.equals(passwordConfirm)) {
                        Toast.makeText(this, "password not match", Toast.LENGTH_SHORT).show();
                    } else {
                        final CustomProgressDialog dialog = new CustomProgressDialog(this);
                        dialog.show();
                        authManager.signUp(this, email, password, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    AppData.put(LoginActivity.this, AppData.KEY_ID, email);
                                    Toast.makeText(LoginActivity.this, "가입 완료", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra(INTENT_EXTRA_EMAIL, email);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "가입 실패 " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else { // 로그인
                    final String email = editEmail.getText().toString();
                    String password = editPassword.getText().toString();
                    if (!authManager.isValidEmail(email)) {
                        Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();
                    } else {
                        final CustomProgressDialog dialog = new CustomProgressDialog(this);
                        dialog.show();
                        authManager.login(this, email, password, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    AppData.put(LoginActivity.this, AppData.KEY_ID, email);
                                    Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra(INTENT_EXTRA_EMAIL, email);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "로그인 실패 " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                break;
            case R.id.text_sign_up:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(INTENT_EXTRA_IS_SIGNING_UP, true);
                startActivityForResult(intent, REQUEST_CODE_SIGN_UP);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_UP:
                if (resultCode == RESULT_OK) {
                    editEmail.setText(data.getStringExtra(INTENT_EXTRA_EMAIL));
                    editPassword.requestFocus();
                }
                break;
            default:
        }
    }
}
