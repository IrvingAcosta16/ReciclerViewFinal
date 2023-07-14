package com.example.recicleviewfinal;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
public class AgregarAlumno extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private DatabaseAlumno db;
    private ImageView imageView;
    private int alumnoId;

    private Spinner spnCarreras;

    private String carreraSeleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_alumno);


        spnCarreras = findViewById(R.id.spnCarreras);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                AgregarAlumno.this,
                android.R.layout.simple_expandable_list_item_1,
                getResources().getStringArray(R.array.carreras)
        );

        spnCarreras.setAdapter(adaptador);
        spnCarreras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtener el elemento seleccionado
                carreraSeleccionada = (String) adapterView.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No se seleccionó nada
            }
        });

        // lo de arriba es del spiner

        db = new DatabaseAlumno(this);
        imageView = findViewById(R.id.imageView);

        // Verifica si se enviaron datos extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            alumnoId = extras.getInt("ALUMNO_ID");

            String alumnoNombre = extras.getString("ALUMNO_NOMBRE");
            String alumnoMatricula = extras.getString("ALUMNO_MATRICULA");
            byte[] alumnoFoto = extras.getByteArray("ALUMNO_FOTO");


            EditText etNombre = findViewById(R.id.etNombre);
            EditText etMatricula = findViewById(R.id.etMatricula);

            etNombre.setText(alumnoNombre);
            etMatricula.setText(alumnoMatricula);

            // Carga la imagen del alumno si está disponible
            if (alumnoFoto != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(alumnoFoto, 0, alumnoFoto.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void guardarAlumno(View view) {
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etMatricula = findViewById(R.id.etMatricula);

        String carrera = carreraSeleccionada;
        String nombre = etNombre.getText().toString();
        String matricula = etMatricula.getText().toString();

        // Verifica que todos los campos estén completos
        if (nombre.isEmpty() || matricula.isEmpty() || imageView.getDrawable() == null) {
            Toast.makeText(this, "Por favor, ingresa todos los datos requeridos", Toast.LENGTH_SHORT).show();
            return; // Sale del método sin guardar el alumno
        }


        byte[] foto = obtenerDatosImagen();

        if (alumnoId != 0) {
            // Si hay un ID de alumno válido, se actualiza el alumno existente
            Alumno alumno = db.obtenerAlumnoPorId(alumnoId);
            alumno.setCarrera(carrera);
            alumno.setNombre(nombre);
            alumno.setMatricula(matricula);
            alumno.setFoto(foto);
            db.actualizarAlumno(alumno);
            Toast.makeText(this, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            // Si no hay ID de alumno, se agrega un nuevo alumno
            db.agregarAlumno(carrera, nombre, matricula, foto);
            Toast.makeText(this, "Alumno agregado correctamente", Toast.LENGTH_SHORT).show();
        }

        // Regresar
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void eliminarAlumno(View view) {

        if (alumnoId != 0) {
            // Si hay un ID de alumno válido, se actualiza el alumno existente
            db.eliminarAlumno(alumnoId);
            Toast.makeText(this, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show();

            // Regresar
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No existe alumno a eliminar", Toast.LENGTH_SHORT).show();
        }


    }


    private byte[] obtenerDatosImagen() {
        byte[] datosImagen = null;

        try {

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            datosImagen = stream.toByteArray();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datosImagen;
    }


    public void seleccionarImagen(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void regresar(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }

}