package com.example.riki.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddWifiActivity extends AppCompatActivity {

    private EditText name;
    private EditText password;
    private TextView passwordLabel;
    private CheckBox checkBox;
    private Button button;
    private ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.435));

        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        passwordLabel = (TextView) findViewById(R.id.passwordWifi);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        button = (Button) findViewById(R.id.button5);
        cancel = (ImageView) findViewById(R.id.cancelButton);

        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    passwordLabel.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                } else {
                    passwordLabel.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!name.getText().toString().equals("")){
                    if(checkBox.isChecked() && !password.getText().toString().equals("")){
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("name", name.getText().toString());
                        returnIntent.putExtra("password", password.getText().toString());
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("name", name.getText().toString());
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
            }
        });
    }
}
