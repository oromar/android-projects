package data;

import android.content.Context;

/**
 * Created by OROMAR on 09/07/2015.
 */
public class DBHelperContato extends GenericDBHelper<Contato> {

    public DBHelperContato(Context context) {
        super(context, "CONTATO", Contato.class);
    }

    @Override
    public String getCreateStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS CONTATO ( ");
        builder.append(" _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        builder.append(" NOME VARCHAR(100), ");
        builder.append(" CELULAR VARCHAR(15), ");
        builder.append(" TELEFONE VARCHAR(15), ");
        builder.append(" EMAIL VARCHAR(100), ");
        builder.append(" LOGRADOURO VARCHAR(100), ");
        builder.append(" NUMERO VARCHAR(10), ");
        builder.append(" BAIRRO VARCHAR(50), ");
        builder.append(" CIDADE VARCHAR(50), ");
        builder.append(" CEP VARCHAR(8))");
        return builder.toString();
    }
}
