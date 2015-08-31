package agenda.oromar.com.br.agenda;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import data.Contato;
import data.DBHelper;
import data.DBHelperContato;


public class CreateEditContato extends Activity implements View.OnClickListener {

    private Button btnSaveContato;
    private Button btnDeleteContato;
    private Button btnSearchCep;
    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtTelefone;
    private EditText edtCelular;
    private EditText edtLogradouro;
    private EditText edtNumero;
    private EditText edtBairro;
    private EditText edtCidade;
    private EditText edtCep;
    private DBHelper<Contato> dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_contato);
        init();
    }

    private void init() {
        btnSaveContato  = (Button)   findViewById(R.id.btnSaveContato);
        btnDeleteContato  = (Button)   findViewById(R.id.btnDeleteContato);
        btnSearchCep    = (Button)   findViewById(R.id.btnSearchCep);
        edtNome         = (EditText) findViewById(R.id.edtNome);
        edtEmail        = (EditText) findViewById(R.id.edtEmail);
        edtCelular      = (EditText) findViewById(R.id.edtCelular);
        edtTelefone     = (EditText) findViewById(R.id.edtTelefone);
        edtLogradouro   = (EditText) findViewById(R.id.edtLogradouro);
        edtNumero       = (EditText) findViewById(R.id.edtNumero);
        edtBairro       = (EditText) findViewById(R.id.edtBairro);
        edtCidade       = (EditText) findViewById(R.id.edtCidade);
        edtCep          = (EditText) findViewById(R.id.edtCep);
        btnSaveContato.setOnClickListener(this);
        dbHelper        = new DBHelperContato(this);
    }


    @Override
    public void onClick(View v) {
        if (v.equals(btnSaveContato)) {
            Contato contato = new Contato();
            contato.setNome(edtNome.getText().toString());
            contato.setTelefone(edtTelefone.getText().toString());
            contato.setCelular(edtCelular.getText().toString());
            contato.setEmail(edtEmail.getText().toString());
            contato.setLogradouro(edtLogradouro.getText().toString());
            contato.setNumero(edtNumero.getText().toString());
            contato.setBairro(edtBairro.getText().toString());
            contato.setCidade(edtCidade.getText().toString());
            contato.setCep(edtCep.getText().toString());
            dbHelper.create(contato);
        } else if (v.equals(btnDeleteContato)) {
            //AlertDialog.Builder builder = new AlertDialog.Builder();
            //builder.setMessage("Tem certeza que deseja remover o registro ?");
            String id = String.valueOf(getIntent().getExtras().get("id_contato_to_delete"));
            getIntent().getExtras().remove("id_contato_to_delete");
            dbHelper.delete(id);
        } else if (v.equals(btnSearchCep)) {
            /* tentar ir na base local primeiro depois ir no web sevice de cep para pesquisar o cep */
        }
    }
}
