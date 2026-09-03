package com.tucocalc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class PersonActivity extends AppCompatActivity {

    private int currentIndex = 0;
    private LinearLayout container;
    private TextView titleView;
    private Button buttonPrev;
    private Button buttonNext;
    private final java.util.ArrayList<ViewGroup> itemRows = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        container = findViewById(R.id.personContainer);
        buttonPrev = findViewById(R.id.buttonPrev);
        buttonNext = findViewById(R.id.buttonNext);
        MaterialButton buttonReview = findViewById(R.id.buttonReview);
        buttonPrev.setOnClickListener(v -> navigate(-1));
        buttonNext.setOnClickListener(v -> navigate(1));
        buttonReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            startActivity(intent);
        });
        render();
    }

    private void navigate(int delta) {
        itemRows.clear();
        BillContext ctx = BillManager.getInstance().getContext();
        if (ctx == null) {
            return;
        }
        int next = currentIndex + delta;
        if (next < 0 || next >= ctx.getPeopleCount()) {
            return;
        }
        currentIndex = next;
        render();
    }

    private void render() {
        container.removeAllViews();
        itemRows.clear();

        BillContext ctx = BillManager.getInstance().getContext();
        if (ctx == null) {
            finish();
            return;
        }

        int people = ctx.getPeopleCount();
        int index = currentIndex;

        titleView = new TextView(this);
        titleView.setTextSize(20);
        titleView.setText(String.format(Locale.getDefault(), getString(R.string.label_person), index + 1));
        titleView.setGravity(Gravity.CENTER);
        container.addView(titleView);

        TextView totals = new TextView(this);
        totals.setTextSize(16);
        totals.setPadding(0, 8, 0, 16);
        if (people == 1) {
            totals.setText(String.format(Locale.getDefault(), "%s\n%s",
                    String.format(Locale.getDefault(), getString(R.string.label_total),
                            ctx.getPersonSubtotalWithTip(index)),
                    String.format(Locale.getDefault(), getString(R.string.label_grand_total),
                            ctx.getTotalWithoutTip())));
        } else {
            totals.setText(String.format(Locale.getDefault(), "%s\n%s",
                    String.format(Locale.getDefault(), getString(R.string.label_shared_portion_with_tip),
                            ctx.getSharedPerPersonWithTip()),
                    String.format(Locale.getDefault(), getString(R.string.label_individual_portion_with_tip),
                            individualWithTip(ctx, index))));
        }
        container.addView(totals);

        TextView labelIndividual = new TextView(this);
        labelIndividual.setText(getString(R.string.label_individual_items));
        labelIndividual.setTextSize(16);
        labelIndividual.setPadding(0, 8, 0, 8);
        container.addView(labelIndividual);

        List<BillItem> items = ctx.getPersonItems(index);
        if (items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Nenhum item individual");
            empty.setTextColor(0xFF888888);
            container.addView(empty);
        } else {
            for (BillItem item : items) {
                addItemRow(item);
            }
        }

        MaterialButton addButton = new MaterialButton(this);
        addButton.setText(getString(R.string.button_add_item));
        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addParams.topMargin = 16;
        addButton.setLayoutParams(addParams);
        addButton.setOnClickListener(v -> showAddItemDialog());
        container.addView(addButton);

        buttonPrev.setEnabled(index > 0);
        buttonNext.setEnabled(index < people - 1);
    }

    private BigDecimal individualWithTip(BillContext ctx, int index) {
        BigDecimal individual = ctx.getIndividualSum(index);
        BigDecimal tip = individual.multiply(ctx.getTipPercent())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return individual.add(tip);
    }

    private void addItemRow(BillItem item) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = 8;
        row.setLayoutParams(rowParams);

        TextView name = new TextView(this);
        name.setText(item.getName());
        name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(name);

        TextView value = new TextView(this);
        value.setText(String.format(Locale.getDefault(), "R$ %.2f", item.getValue()));
        row.addView(value);

        container.addView(row);
    }

    private void showAddItemDialog() {
        BillContext ctx = BillManager.getInstance().getContext();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, 0);

        TextInputLayout nameLayout = new TextInputLayout(this);
        nameLayout.setHint(getString(R.string.hint_item_name));
        TextInputEditText nameInput = new TextInputEditText(this);
        nameLayout.addView(nameInput);
        layout.addView(nameLayout);

        TextInputLayout valueLayout = new TextInputLayout(this);
        valueLayout.setHint(getString(R.string.hint_item_value));
        TextInputEditText valueInput = new TextInputEditText(this);
        valueInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valueLayout.addView(valueInput);
        layout.addView(valueLayout);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_add_item_title)
                .setView(layout)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_add, null)
                .create();

        dialog.setOnShowListener(d -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(btn -> {
                String name = nameInput.getText().toString().trim();
                String valueStr = valueInput.getText().toString().trim();
                if (name.isEmpty() || valueStr.isEmpty()) {
                    Toast.makeText(this, R.string.error_empty_item, Toast.LENGTH_SHORT).show();
                    return;
                }
                BigDecimal value;
                try {
                    value = new BigDecimal(valueStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.error_invalid_value, Toast.LENGTH_SHORT).show();
                    return;
                }
                ctx.addPersonItem(currentIndex, new BillItem(name, value));
                dialog.dismiss();
                render();
            });
        });

        dialog.show();
    }
}
