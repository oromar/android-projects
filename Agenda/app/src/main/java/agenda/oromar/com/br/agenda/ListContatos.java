package agenda.oromar.com.br.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import data.Contato;
import data.DBHelper;
import data.DBHelperContato;


public class ListContatos extends Activity implements View.OnClickListener{

    private Button btnPesquisar;
    private Button btnAddContato;
    private EditText edtPesquisar;
    private ListView listContatos;
    private DBHelper<Contato> dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contatos);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createList();
    }

    private void init() {
        listContatos = (ListView) findViewById(R.id.listContatos);
        edtPesquisar = (EditText) findViewById(R.id.edtPesquisar);
        btnAddContato = (Button) findViewById(R.id.btnAddContato);
        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        dbHelper = new DBHelperContato(this);
        createList();
        btnAddContato.setOnClickListener(this);
        btnPesquisar.setOnClickListener(this);
    }

    private void createList() {
        ArrayAdapter<Contato> adapter = new ArrayAdapter<Contato>(this, android.R.layout.simple_list_item_1);
        for (Contato c : dbHelper.list()) {
            adapter.add(c);
        }
        listContatos.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        if (v.equals(btnAddContato)) {
            Intent intent = new Intent(this, CreateEditContato.class);
            startActivity(intent);
        } else if (v.equals(btnPesquisar)) {
            search();
        }
    }

    private void search() {
        String text = edtPesquisar.getText().toString();
        Contato contato = new Contato();
        if (text.matches("[0-9]+")) {
            contato.setTelefone(text);
            contato.setCelular(text);
        } else if (text.contains("@")) {
            contato.setEmail(text);
        } else {
            contato.setNome(text);
        }
        ArrayAdapter<Contato> adapter = new ArrayAdapter<Contato>(this, android.R.layout.simple_list_item_1);
        try {
            for (Contato c : dbHelper.search(contato)) {
                adapter.add(c);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        listContatos.setAdapter(adapter);
    }
}
