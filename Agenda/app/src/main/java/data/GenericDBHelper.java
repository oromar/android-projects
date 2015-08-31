package data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OROMAR on 09/07/2015.
 */
public abstract class GenericDBHelper<T extends BasicEntity> extends SQLiteOpenHelper implements DBHelper<T> {

    private String tableName;
    private Class<?> clazz;

    public GenericDBHelper(Context context, String name, Class<?> clazz) {
        super(context, name, null, 1);
        this.tableName = name.toUpperCase();
        this.clazz = clazz;
    }

    @Override
    public void onCreate(SQLiteDatabase db) { db.execSQL(getCreateStatement()); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }

    public abstract String getCreateStatement();

    @Override
    public void create(T t) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.insert(tableName, null, getContentValuesFromObject(t));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database.isOpen()) {
                database.close();
            }
        }
    }

    @Override
    public void delete(Serializable id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(tableName, "_id = ?", new String[]{String.valueOf(id)});
        if (database.isOpen()) {
            database.close();
        }
    }

    @Override
    public void edit(T t) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.update(tableName, getContentValuesFromObject(t), "_id=?", new String[]{String.valueOf(t.getId())});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (database.isOpen()) {
                database.close();
            }
        }
    }

    @Override
    public T get(Serializable id) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ").append(tableName).append(" WHERE _id = ?");
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(builder.toString(), new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        if (database.isOpen()) database.close();
        return getBeanFromCursor(cursor);
    }

    @Override
    public List<T> list() {
        List<T> result = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName.toUpperCase(), null);
        while(cursor.moveToNext()) {
            result.add(getBeanFromCursor(cursor));
        }
        if (database.isOpen()) database.close();
        return result;
    }

    @Override
    public List<T> search(T t) throws IllegalAccessException {
        Map<String, String> filter = getBeanAsMap(t);
        List<T> result = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(getSelectToSearch(filter), getValuesFromMap(filter));
        while(cursor.moveToNext()) {
            result.add(getBeanFromCursor(cursor));
        }
        if (database.isOpen()) database.close();
        return result;
    }

    public Map<String, String> getBeanAsMap(Object object) throws IllegalArgumentException, IllegalAccessException {
        if (object == null) {
            throw new IllegalArgumentException("Object to GenericDBHelper#getBeanAsMap() method cannot be null !");
        }
        Map<String, String> result = new LinkedHashMap<>();
        Class<?> clazz = object.getClass();
        List<Field> list = new ArrayList<>();
        while (clazz.getSuperclass() != null) {
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for (Field f : list) {
            f.setAccessible(true);
            if (f.getType().equals(String.class)) {
                String ret = String.valueOf(f.get(object));
                if (ret != null && !ret.equals("")) {
                    result.put(f.getName(), ret);
                }
            }
        }
        return result;

    }

    public String getSelectToSearch(Map<String, String> map){
        return new StringBuilder(getSelect(tableName, null)).append(getWhereClauseFromMapOR(map)).toString();
    }

    public String getSelect(String tableName, String[] columns) {
        StringBuilder builder = new StringBuilder();
        if (columns == null || columns.length == 0) {
            return builder.append("SELECT * FROM ").append(tableName.toUpperCase()).append(" ").toString();
        } else {
            builder.append("SELECT ");
            for (int i = 0; i < columns.length; i++) {
                builder.append(columns[i].toUpperCase());
                if (i < (columns.length - 1)) {
                    builder.append(", ");
                }
            }
            return builder.append(" FROM ").append(tableName.toUpperCase()).toString();
        }
    }

    public String[] getColumnsFromMap(Map<String, String> map) {
        return map.entrySet().toArray(new String[map.size()]);
    }

    public String[] getValuesFromMap(Map<String, String> map) {
        return map.values().toArray(new String[map.size()]);
    }

    public String getWhereClauseFromMap(Map<String, String> map, String connector) {
        StringBuilder builder = new StringBuilder(" WHERE ");
        String sqlConector = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(sqlConector).append(entry.getKey()).append(" LIKE ?").append("\n");
            sqlConector = connector;
        }
        return builder.toString();
    }

    public String getWhereClauseFromMapOR(Map<String, String> map) {
        return getWhereClauseFromMap(map, " OR ");
    }

    public String getWhereClauseFromMapAND(Map<String, String> map) {
        return getWhereClauseFromMap(map, " AND ");
    }

    public ContentValues getContentValuesFromObject(Object obj) throws IllegalAccessException {
        if (obj == null) {
            throw new IllegalArgumentException("Object in GenericDBHelper#getContentValuesFromObject cannot be null !");
        }
        ContentValues retorno = new ContentValues();
        Class<?> clazz = obj.getClass();
        List<Field> list = new ArrayList<>();
        while(clazz.getSuperclass() != null) {
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for (Field f : list) {
            f.setAccessible(true);
            Object result = f.get(obj);
            if (result != null) {
                retorno.put(f.getName(), String.valueOf(result));
            }
        }
        return retorno;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public T getBeanFromCursor(final Cursor cursor) {
        try {
            int position = 0;
            int columnType = 0;
            Field field = null;
            Object value = null;
            String[] columnNames = cursor.getColumnNames();
            T instance = (T) clazz.newInstance();
            for (String resultSetColumnName : columnNames) {
                field = getField(resultSetColumnName);
                if (field != null) {
                    position = cursor.getColumnIndex(resultSetColumnName);
                    columnType = cursor.getType(position);
                    if (columnType == Cursor.FIELD_TYPE_FLOAT) {
                        value = cursor.getFloat(position);
                    } else if (columnType == Cursor.FIELD_TYPE_INTEGER){
                        value = cursor.getInt(position);
                    } else if (columnType == Cursor.FIELD_TYPE_BLOB) {
                        value = cursor.getBlob(position);
                    } else if (columnType == Cursor.FIELD_TYPE_STRING) {
                        value = cursor.getString(position);
                    } else if (columnType == Cursor.FIELD_TYPE_NULL) {
                        value = null;
                    } else {
                        throw new IllegalStateException("Type from database not supported yet in method GenericDBHelper#getBeanFromCursor() !");
                    }
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Field getField(String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(fieldName)){
                return field;
            }
        }
        return null;
    }

}
