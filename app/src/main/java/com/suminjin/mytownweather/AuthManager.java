package com.suminjin.mytownweather;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by parkjisun on 2017. 4. 25..
 */

public class AuthManager {
    private static FirebaseAuth mAuth;

    public AuthManager() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }

    public void signUp(final Activity context, String email, String password, OnCompleteListener<com.google.firebase.auth.AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, listener);
    }

    public void login(final Activity context, String email, String password, OnCompleteListener<com.google.firebase.auth.AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, listener);
    }

    public void logout() {
        mAuth.signOut();
    }

    public boolean isValidPassword(String target) {
        Pattern p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0-9])(?=.*[a-zA-Z]).*$)");
        Matcher m = p.matcher(target);
        if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            return true;
        } else {

            return false;
        }
    }

    public boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void hideSoftKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
