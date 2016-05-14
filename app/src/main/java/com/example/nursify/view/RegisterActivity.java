package com.example.nursify.view;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nursify.R;
import com.example.nursify.utils.SharedPreferenceHelper;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Handler mHandler;
    private static final int TASK_COMPLETE = 1;

    private RelativeLayout registerClickableRelativeLayout, agreementRelative;
    private EditText firstName, lastName, email, qualifications;
    private TextInputLayout firstNameInputLayout, lastNameInputLayout, emailInputLayout;
    private TextView registerTextView;
    private ProgressBar progressBar;
    private CheckBox agree;
    private boolean validFirstName = false, validLastName = false, validEmail = false, agreed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    private void initialize() {
        registerClickableRelativeLayout = (RelativeLayout) findViewById(R.id.register_layout);
        assert registerClickableRelativeLayout != null;
        registerClickableRelativeLayout.setEnabled(false);

        firstName = (EditText) findViewById(R.id.first_name);
        firstName.addTextChangedListener(new Watcher(firstName));

        lastName = (EditText) findViewById(R.id.last_name);
        lastName.addTextChangedListener(new Watcher(lastName));

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(new Watcher(email));

        qualifications = (EditText) findViewById(R.id.qualifications);

        firstNameInputLayout = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        lastNameInputLayout = (TextInputLayout) findViewById(R.id.input_layout_last_name);
        emailInputLayout = (TextInputLayout) findViewById(R.id.input_layout_email);

        agree = (CheckBox) findViewById(R.id.agree);
        assert agree != null;
        agree.setOnCheckedChangeListener(this);

        agreementRelative = (RelativeLayout) findViewById(R.id.agree_layout);
        assert agreementRelative != null;
        agreementRelative.setOnClickListener(this);

        registerTextView = (TextView) findViewById(R.id.register_text);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void register() {
        if (email.getText().toString().equals(SharedPreferenceHelper.get(getApplicationContext(), "Email", "default"))) {
            Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
        } else {
            showProgressBar();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = Message.obtain();
                            SharedPreferenceHelper.put(getApplicationContext(), "FirstName", firstName.getText().toString());
                            SharedPreferenceHelper.put(getApplicationContext(), "LastName", lastName.getText().toString());
                            SharedPreferenceHelper.put(getApplicationContext(), "Email", email.getText().toString());
                            SharedPreferenceHelper.put(getApplicationContext(), "Qualifications", qualifications.getText().toString());
                            msg.what = TASK_COMPLETE;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                    task();
                }
            }, 500);
        }
    }

    private void task() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case TASK_COMPLETE:
                        clear();
                        Toast.makeText(getApplicationContext(), "User successfully registered", Toast.LENGTH_LONG).show();
                        hideProgressBar();
                        return true;
                    default:
                        handleMessage(msg);
                        return false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_layout:
                register();
                break;
            case R.id.agree_layout:
                agree.performClick();
        }
    }

    private void clear() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        qualifications.setText("");

        firstNameInputLayout.setErrorEnabled(false);
        lastNameInputLayout.setErrorEnabled(false);
        emailInputLayout.setErrorEnabled(false);
    }

    private boolean checkFirstName() {
        return !TextUtils.isEmpty(firstName.getText().toString());
    }

    private boolean checkLastName() {
        return !TextUtils.isEmpty(lastName.getText().toString());
    }

    private boolean checkEmail() {
        return !TextUtils.isEmpty(email.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    private void validate() {
        if (validFirstName && validEmail && validLastName && agreed)
            onEnableButton();
        else
            onDisableButton();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        registerTextView.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        registerTextView.setVisibility(View.VISIBLE);
    }

    private void onDisableButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerTextView.setTextColor(getResources().getColor(R.color.disabledTextColor, getTheme()));
        } else {
            registerTextView.setTextColor(getResources().getColor(R.color.disabledTextColor));
        }

        registerClickableRelativeLayout.setEnabled(false);
        registerClickableRelativeLayout.setOnClickListener(null);
    }

    private void onEnableButton() {
        registerTextView.setTextColor(Color.WHITE);
        registerClickableRelativeLayout.setEnabled(true);
        registerClickableRelativeLayout.setOnClickListener(this);
    }

    private void onEmailError() {
        emailInputLayout.setErrorEnabled(true);
        emailInputLayout.setError(getResources().getString(R.string.no_valid_email));
    }

    private void onEmailPass() {
        emailInputLayout.setErrorEnabled(false);
    }

    private void onLastNameError() {
        lastNameInputLayout.setErrorEnabled(true);
        lastNameInputLayout.setError(getResources().getString(R.string.no_valid_last_name));
    }

    private void onLastNamePass() {
        lastNameInputLayout.setErrorEnabled(false);
    }

    private void onFirstNameError() {
        firstNameInputLayout.setErrorEnabled(true);
        firstNameInputLayout.setError(getResources().getString(R.string.no_valid_first_name));
    }

    private void onFirstNamePass() {
        firstNameInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.agree:
                agreed = isChecked;
                validate();
                break;
        }
    }

    private class Watcher implements TextWatcher {
        View v;

        public Watcher(View v) {
            this.v = v;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (v.getId()) {
                case R.id.first_name:
                    if (checkFirstName()) {
                        onFirstNamePass();
                        validFirstName = true;
                    } else {
                        onFirstNameError();
                        validFirstName = false;
                    }
                    break;
                case R.id.last_name:
                    if (checkLastName()) {
                        onLastNamePass();
                        validLastName = true;
                    } else {
                        onLastNameError();
                        validLastName = false;
                    }
                    break;
                case R.id.email:
                    if (checkEmail()) {
                        onEmailPass();
                        validEmail = true;
                    } else {
                        onEmailError();
                        validEmail = false;
                    }
                    break;
            }
            validate();
        }
    }
}