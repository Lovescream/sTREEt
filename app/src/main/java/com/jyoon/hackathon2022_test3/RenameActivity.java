package com.jyoon.hackathon2022_test3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RenameActivity extends AppCompatActivity {

    EditText inputTreeName;
    EditText inputAuthor;
    Button btnCancel;
    Button btnOK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rename_tree);

        inputTreeName = findViewById(R.id.inputTreeName);
        inputAuthor = findViewById(R.id.inputAuthorName);
        btnCancel = findViewById(R.id.btnCancel);
        btnOK = findViewById(R.id.btnOK);

        Intent intent = getIntent();

        inputTreeName.setText(intent.getStringExtra("treeName"));
        inputAuthor.setText(intent.getStringExtra("treeAuthor"));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
