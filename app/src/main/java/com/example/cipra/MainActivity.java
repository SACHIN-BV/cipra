package com.example.cipra;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorMessageTextView;
    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.sign_in_button);
        ImageView eyeIcon = findViewById(R.id.eye_icon);
        ImageView profileIcon = findViewById(R.id.profile_icon);
        ImageView keyIcon = findViewById(R.id.key_icon);


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    profileIcon.setImageResource(R.drawable.profilefill);
                } else {
                    profileIcon.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    keyIcon.setImageResource(R.drawable.keyfill);
                } else {
                    keyIcon.setImageResource(R.drawable.key);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });

        eyeIcon.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide Password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeIcon.setImageResource(R.drawable.hide);
                } else {
                    // Show Password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeIcon.setImageResource(R.drawable.view);
                }
                // Move the cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.length());
                isPasswordVisible = !isPasswordVisible;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    new SignInTask().execute(email, password);
                } else {
                    Toast.makeText(MainActivity.this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class SignInTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "SignInTask";
        private int responseCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String result = "";
            try {
                String urlString = "https://api.cipra.ai:5000/takehome/signin?email=" + email + "&password=" + password;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                responseCode = connection.getResponseCode();
                BufferedReader br;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Navigate to Dashboard activity
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
