package com.tucocalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        container = findViewById(R.id.reviewContainer);

        BillContext ctx = BillManager.getInstance().getContext();
        if (ctx == null) {
            finish();
            return;
        }
        render(ctx);

        MaterialButton back = findViewById(R.id.buttonBack);
        back.setOnClickListener(v -> finish());

        MaterialButton newBill = findViewById(R.id.buttonNewBill);
        newBill.setOnClickListener(v -> {
            BillManager.getInstance().reset();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void render(BillContext ctx) {
        int people = ctx.getPeopleCount();
        int onSurface = getColorFromAttr(android.R.attr.textColorPrimary);

        TextView header = new TextView(this);
        header.setText(getString(R.string.label_review_header));
        header.setTextSize(20);
        header.setGravity(Gravity.CENTER);
        header.setPadding(0, 0, 0, 8);
        container.addView(header);

        TextView grandTotal = new TextView(this);
        grandTotal.setText(String.format(Locale.getDefault(), getString(R.string.label_grand_total),
                ctx.getTotalOfPersonTotals()));
        grandTotal.setTextSize(16);
        grandTotal.setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorPrimary));
        grandTotal.setGravity(Gravity.CENTER);
        grandTotal.setPadding(0, 0, 0, 16);
        container.addView(grandTotal);

        for (int i = 0; i < people; i++) {
            container.addView(buildPersonCard(ctx, i, onSurface));
        }

        BigDecimal sum = ctx.getTotalOfPersonTotals();
        BigDecimal expected = ctx.getTotalWithTip();
        BigDecimal diff = sum.subtract(expected).abs();
        TextView check = new TextView(this);
        check.setPadding(0, 16, 0, 0);
        if (diff.compareTo(new BigDecimal("0.01")) <= 0) {
            check.setText(getString(R.string.label_review_match));
            check.setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorPrimary));
        } else {
            check.setText(getString(R.string.label_review_mismatch));
            check.setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorError));
        }
        container.addView(check);
    }

    private LinearLayout buildPersonCard(BillContext ctx, int index, int onSurface) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);
        card.setPadding(16, 16, 16, 16);
        int surface = getColorFromAttr(com.google.android.material.R.attr.colorSurface);
        card.setBackgroundColor(surface);

        TextView title = new TextView(this);
        title.setText(String.format(Locale.getDefault(), getString(R.string.label_person), index + 1));
        title.setTextSize(18);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setTextColor(onSurface);
        card.addView(title);

        TextView shared = new TextView(this);
        shared.setText(String.format(Locale.getDefault(), getString(R.string.label_review_shared_with_tip),
                ctx.getSharedPerPersonWithTip()));
        shared.setPadding(0, 4, 0, 0);
        shared.setTextColor(onSurface);
        card.addView(shared);

        for (BillItem item : ctx.getPersonItems(index)) {
            TextView itemLine = new TextView(this);
            itemLine.setText(String.format(Locale.getDefault(), "  - %s: R$ %.2f", item.getName(), item.getValue()));
            itemLine.setTextColor(onSurface);
            card.addView(itemLine);
        }

        TextView total = new TextView(this);
        total.setText(String.format(Locale.getDefault(), getString(R.string.label_review_person_total),
                ctx.getPersonSubtotalWithTip(index)));
        total.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        total.setTextColor(onSurface);
        total.setPadding(0, 8, 0, 0);
        card.addView(total);

        return card;
    }

    private int getColorFromAttr(int attr) {
        android.content.res.TypedArray a = getTheme()
                .obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, 0xFF000000);
        } finally {
            a.recycle();
        }
    }
}
