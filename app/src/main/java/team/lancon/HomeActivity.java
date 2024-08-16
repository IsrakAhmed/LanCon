package team.lancon;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import java.util.ArrayList;
import android.os.AsyncTask;

import android.os.Handler;
import android.os.Looper;

public class HomeActivity extends AppCompatActivity {

    private String serverIp;
    private String serverName;
    private String userName;
    private String serverOwner;
    private TextView headerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        headerTextView = findViewById(R.id.headerTextView);

        // Get the username from the intent
        serverIp = getIntent().getStringExtra("serverIp");

        // Get the username from the intent
        serverName = getIntent().getStringExtra("serverName");

        // Get the username from the intent
        userName = getIntent().getStringExtra("USERNAME");

        // Get the username from the intent
        serverOwner = getIntent().getStringExtra("serverOwner");

        headerTextView.setText(serverIp + " " + serverName + " " + userName + " " + serverOwner);
    }

}