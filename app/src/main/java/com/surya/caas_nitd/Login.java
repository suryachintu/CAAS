package com.surya.caas_nitd;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @BindView(R.id.username_edittext)
    EditText usernameField;
    @BindView(R.id.password_edittext)
    EditText passwordField;
    @BindView(R.id.login_btn)
    Button login_btn;
    @BindView(R.id.rol_no_edittext)
    EditText rollField;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.content_login)
    RelativeLayout relativeLayout;
            
    private static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();


    }

    @OnClick({R.id.login_btn,R.id.fab})
    public void onClick(final View view) {

        if (view.getId() == R.id.fab){
            //show circular reveal effect with new user or existing user
            int cx = relativeLayout.getWidth();
            int cy = relativeLayout.getHeight();
            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(relativeLayout, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                view.setVisibility(View.VISIBLE);
                anim.setDuration(400);

                anim.start();
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (login_btn.getText().toString().equals(getString(R.string.login))){
                            rollField.setVisibility(View.VISIBLE);
                            login_btn.setText(getString(R.string.sigu_up));
                            fab.setImageResource(R.drawable.ic_person_white_24dp);
                        }else {
                            rollField.setVisibility(View.GONE);
                            login_btn.setText(getString(R.string.login));
                            fab.setImageResource(R.drawable.ic_person_add_white_24dp);

                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }


        }else {
            //process the credentials
            authenticateUser();

        }

    }

    private void authenticateUser() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.authentication_dialog);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        boolean isValidEmail = checkEmail(username);
        boolean isValidPass = checkPassword(password);

        if (!isValidEmail || !isValidPass) {
            Toast.makeText(Login.this, "Check the credentials", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        if (rollField.getVisibility() == View.GONE) {

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG,task.getException().getMessage() + "");
                            } else {
                                Intent intent = new Intent(Login.this, MapsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            // ...
                        }
                    });

        } else {

            mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(Login.this, MapsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }

                            // ...
                        }
                    });

        }

    }

    private boolean checkPassword(String password) {

        if (password.length()< 8){
            passwordField.setError("password must be 8 characters");
            return false;
        }else {
            return true;
        }

    }

    private boolean checkEmail(String email) {

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() /*&& email.contains("@nitdelhi.ac.in")*/) {
            return true;
        } else {
            usernameField.setError("Check Email");
            return false;
        }
    }

}
