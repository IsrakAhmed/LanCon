package team.lancon;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameEditText = findViewById(R.id.userNameEditText);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEditText.getText().toString().trim();

                if (userName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter A Username", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Navigate to SelectionActivity and pass the userName
                Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            }
        });
    }
}