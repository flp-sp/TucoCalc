package com.tucocalc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputPeople;
    private TextInputEditText inputTotal;
    private TextInputEditText inputTip;
    private TextInputLayout layoutPeople;
    private TextInputLayout layoutTotal;
    private TextInputLayout layoutTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputPeople = findViewById(R.id.inputPeople);
        inputTotal = findViewById(R.id.inputTotal);
        inputTip = findViewById(R.id.inputTip);
        layoutPeople = findViewById(R.id.layoutPeople);
        layoutTotal = findViewById(R.id.layoutTotal);
        layoutTip = findViewById(R.id.layoutTip);

        inputTip.setText("10");

        MaterialButton buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(v -> onCalculate());
    }

    private void onCalculate() {
        String peopleStr = inputPeople.getText().toString().trim();
        String totalStr = inputTotal.getText().toString().trim();
        String tipStr = inputTip.getText().toString().trim();

        layoutPeople.setError(null);
        layoutTotal.setError(null);
        layoutTip.setError(null);

        if (TextUtils.isEmpty(peopleStr)) {
            layoutPeople.setError(getString(R.string.error_invalid_number));
            return;
        }
        if (TextUtils.isEmpty(totalStr)) {
            layoutTotal.setError(getString(R.string.error_invalid_value));
            return;
        }
        if (TextUtils.isEmpty(tipStr)) {
            layoutTip.setError(getString(R.string.error_invalid_value));
            return;
        }

        int people;
        BigDecimal total;
        BigDecimal tipPercent;
        try {
            people = Integer.parseInt(peopleStr);
            total = new BigDecimal(totalStr);
            tipPercent = new BigDecimal(tipStr);
        } catch (NumberFormatException e) {
            layoutPeople.setError(getString(R.string.error_invalid_number));
            layoutTotal.setError(getString(R.string.error_invalid_value));
            layoutTip.setError(getString(R.string.error_invalid_value));
            return;
        }

        if (people <= 0) {
            layoutPeople.setError(getString(R.string.error_invalid_number));
            return;
        }
        if (total.signum() <= 0) {
            layoutTotal.setError(getString(R.string.error_invalid_value));
            return;
        }
        if (tipPercent.signum() < 0) {
            layoutTip.setError(getString(R.string.error_invalid_value));
            return;
        }

        BillContext context = new BillContext(people, total, tipPercent);
        BillManager.getInstance().setContext(context);

        Intent intent = new Intent(this, PersonActivity.class);
        startActivity(intent);
    }
}
