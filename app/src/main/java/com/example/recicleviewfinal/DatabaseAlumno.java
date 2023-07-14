package com.example.recicleviewfinal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAlumno extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alumnos.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ALUMNO = "alumno";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CARRERA = "carrera";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_MATRICULA = "matricula";
    private static final String COLUMN_FOTO = "foto";

    public DatabaseAlumno(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Sentencia SQL para crear la tabla alumno con las columnas definidas
        String CREATE_ALUMNO_TABLE = "CREATE TABLE " + TABLE_ALUMNO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CARRERA + " TEXT,"
                + COLUMN_NOMBRE + " TEXT,"
                + COLUMN_MATRICULA + " TEXT,"
                + COLUMN_FOTO + " BLOB"
                + ")";
        // Ejecutar la sentencia SQL para crear la tabla
        db.execSQL(CREATE_ALUMNO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar la tabla alumno si existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUMNO);
        // Volver a crear la tabla llamando al método onCreate
        onCreate(db);
    }

    public void agregarAlumno(String carrera, String nombre, String matricula, byte[] foto) {
        // Obtener una referencia a la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();
        // Crear un objeto ContentValues para almacenar los valores de las columnas
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARRERA, carrera);
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_MATRICULA, matricula);
        values.put(COLUMN_FOTO, foto);
        // Insertar los valores en la tabla alumno y obtener el resultado
        long resultado = db.insert(TABLE_ALUMNO, null, values);
        if (resultado == -1) {
            // Manejar el error en caso de que la inserción falle
        }
        // Cerrar la base de datos
        db.close();
    }

    public void eliminarAlumno(int idAlumno) {
        // Obtener una referencia a la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();
        // Eliminar el registro de la tabla alumno donde el id coincide con el parámetro proporcionado
        db.delete(TABLE_ALUMNO, COLUMN_ID + " = ?", new String[]{String.valueOf(idAlumno)});
        // Cerrar la base de datos
        db.close();
    }

    public Alumno obtenerAlumnoPorId(int alumnoId) {
        // Obtener una referencia a la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        // Realizar una consulta a la tabla alumno para obtener el registro con el id proporcionado
        Cursor cursor = db.query(
                TABLE_ALUMNO,
                new String[] { COLUMN_ID, COLUMN_CARRERA, COLUMN_NOMBRE, COLUMN_MATRICULA, COLUMN_FOTO },
                COLUMN_ID + "=?",
                new String[] { String.valueOf(alumnoId) },
                null,
                null,
                null,
                null
        );

        Alumno alumno = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(COLUMN_ID);
            int columnIndexCarrera = cursor.getColumnIndex(COLUMN_CARRERA);
            int columnIndexNombre = cursor.getColumnIndex(COLUMN_NOMBRE);
            int columnIndexMatricula = cursor.getColumnIndex(COLUMN_MATRICULA);
            int columnIndexFoto = cursor.getColumnIndex(COLUMN_FOTO);

            if (columnIndexId != -1 && columnIndexNombre != -1 && columnIndexMatricula != -1 && columnIndexFoto != -1) {
                // Crear un objeto Alumno con los datos obtenidos de la consulta
                alumno = new Alumno(
                        cursor.getInt(columnIndexId),
                        cursor.getString(columnIndexCarrera),
                        cursor.getString(columnIndexNombre),
                        cursor.getString(columnIndexMatricula),
                        cursor.getBlob(columnIndexFoto)
                );
            }

            cursor.close();
        }

        // Cerrar la base de datos
        db.close();

        // Devolver el objeto Alumno
        return alumno;
    }

    public void actualizarAlumno(Alumno alumno) {
        // Obtener una referencia a la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Crear un objeto ContentValues con los nuevos valores del Alumno
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARRERA, alumno.getCarrera());
        values.put(COLUMN_NOMBRE, alumno.getNombre());
        values.put(COLUMN_MATRICULA, alumno.getMatricula());
        values.put(COLUMN_FOTO, alumno.getFoto());

        // Actualizar el registro de la tabla alumno donde el id coincide con el id del Alumno proporcionado
        db.update(
                TABLE_ALUMNO,
                values,
                COLUMN_ID + "=?",
                new String[] { String.valueOf(alumno.getId()) }
        );

        // Cerrar la base de datos
        db.close();
    }

    public List<Alumno> buscarAlumnos(String query) {
        List<Alumno> listaAlumnos = new ArrayList<>();

        // Obtener una referencia a la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        // Realizar una consulta a la tabla alumno para obtener los registros que coinciden con la consulta
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALUMNO +
                " WHERE " + COLUMN_NOMBRE + " LIKE '%" + query + "%' OR " +
                COLUMN_MATRICULA + " LIKE '%" + query + "%' OR " + COLUMN_CARRERA + " LIKE '%" + query + "%'", null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(COLUMN_ID);
            int columnIndexCarrera = cursor.getColumnIndex(COLUMN_CARRERA);
            int columnIndexNombre = cursor.getColumnIndex(COLUMN_NOMBRE);
            int columnIndexMatricula = cursor.getColumnIndex(COLUMN_MATRICULA);
            int columnIndexFoto = cursor.getColumnIndex(COLUMN_FOTO);

            do {
                Alumno alumno = new Alumno();
                if (columnIndexId != -1) {
                    alumno.setId(cursor.getInt(columnIndexId));
                }
                if (columnIndexCarrera != -1) {
                    alumno.setCarrera(cursor.getString(columnIndexCarrera));
                }
                if (columnIndexNombre != -1) {
                    alumno.setNombre(cursor.getString(columnIndexNombre));
                }
                if (columnIndexMatricula != -1) {
                    alumno.setMatricula(cursor.getString(columnIndexMatricula));
                }
                if (columnIndexFoto != -1) {
                    alumno.setFoto(cursor.getBlob(columnIndexFoto));
                }

                listaAlumnos.add(alumno);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        // Cerrar la base de datos
        db.close();

        // Devolver la lista de Alumnos
        return listaAlumnos;
    }

    public List<Alumno> obtenerAlumnos() {
        List<Alumno> listaAlumnos = new ArrayList<>();

        // Obtener una referencia a la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        // Realizar una consulta a la tabla alumno para obtener todos los registros
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALUMNO, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(COLUMN_ID);
            int columnIndexCarrera = cursor.getColumnIndex(COLUMN_CARRERA);
            int columnIndexNombre = cursor.getColumnIndex(COLUMN_NOMBRE);
            int columnIndexMatricula = cursor.getColumnIndex(COLUMN_MATRICULA);
            int columnIndexFoto = cursor.getColumnIndex(COLUMN_FOTO);

            do {
                Alumno alumno = new Alumno();
                if (columnIndexId != -1) {
                    alumno.setId(cursor.getInt(columnIndexId));
                }
                if (columnIndexCarrera != -1) {
                    alumno.setCarrera(cursor.getString(columnIndexCarrera));
                }
                if (columnIndexNombre != -1) {
                    alumno.setNombre(cursor.getString(columnIndexNombre));
                }
                if (columnIndexMatricula != -1) {
                    alumno.setMatricula(cursor.getString(columnIndexMatricula));
                }
                if (columnIndexFoto != -1) {
                    alumno.setFoto(cursor.getBlob(columnIndexFoto));
                }

                listaAlumnos.add(alumno);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        // Cerrar la base de datos
        db.close();

        // Devolver la lista de Alumnos
        return listaAlumnos;
    }
}
