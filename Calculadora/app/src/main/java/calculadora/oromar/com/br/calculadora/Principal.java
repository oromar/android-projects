package calculadora.oromar.com.br.calculadora;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Principal extends ActionBarActivity implements View.OnClickListener {

    private EditText num01;
    private EditText num02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        initComponents();
    }

    private void initComponents() {
        Button buttonPlus;
        num01 = (EditText) findViewById(R.id.txtNumero01);
        num02 = (EditText) findViewById(R.id.txtNumero02);
        buttonPlus = (Button) findViewById(R.id.buttonSomar);
        buttonPlus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Integer num1 = Integer.parseInt(num01.getText().toString());
        Integer num2 = Integer.parseInt(num02.getText().toString());
        int result = num1 + num2;
        Toast.makeText(getApplicationContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
