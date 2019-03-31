package com.delivame.delivame.deliveryman.old_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.utilities.Constants;

public class ViewMessageActivity extends AppCompatActivity {


    private TextView textViewMessageDate;

    private TextView textViewMessageTo;

    private TextView textViewMessageSubject;

    private TextView textViewMessageBody;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        textViewMessageDate = findViewById(R.id.textViewMessageDate);
        textViewMessageTo = findViewById(R.id.textViewMessageTo);
        textViewMessageSubject = findViewById(R.id.textViewMessageSubject);
        textViewMessageBody = findViewById(R.id.textViewMessageBody);
        toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View OldMessage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (intent != null) {
            String to = intent.getStringExtra(Constants.MESSAGE_TO);
            String subject = intent.getStringExtra(Constants.MESSAGE_SUBJECT);
            String body = intent.getStringExtra(Constants.MESSAGE_BODY);
            String date = intent.getStringExtra(Constants.MESSAGE_DATE);

            textViewMessageDate.setText(date);
            textViewMessageBody.setText(body);
            textViewMessageSubject.setText(subject);
            textViewMessageTo.setText(to);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
