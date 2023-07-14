package com.example.recicleviewfinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.SearchView;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private DatabaseAlumno db;
    private static final int REQUEST_EDIT_ALUMNO = 1;

    private RecyclerView recyclerViewAlumnos;
    private AlumnoAdapter alumnoAdapter;
    private List<Alumno> listaAlumnos;
    private SearchView searchView;

    public void agregarAlumno(View view) {
        Intent intent = new Intent(this, AgregarAlumno.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseAlumno(this);
        recyclerViewAlumnos = findViewById(R.id.recyclerViewAlumnos);

        listaAlumnos = new ArrayList<>();
        alumnoAdapter = new AlumnoAdapter(this, listaAlumnos);
        recyclerViewAlumnos.setAdapter(alumnoAdapter);
        recyclerViewAlumnos.setLayoutManager(new LinearLayoutManager(this));

        mostrarAlumnos();

        recyclerViewAlumnos.addOnItemTouchListener(
                new MainActivity.RecyclerItemClickListener(this, recyclerViewAlumnos,
                        new MainActivity.RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Alumno selectedAlumno = listaAlumnos.get(position);

                                // Abre la ventana de edición del alumno y pasa los datos del alumno seleccionado
                                Intent intent = new Intent(MainActivity.this, AgregarAlumno.class);
                                intent.putExtra("ALUMNO_ID", selectedAlumno.getId());
                                intent.putExtra("ALUMNO_CARRERA", selectedAlumno.getCarrera());
                                intent.putExtra("ALUMNO_NOMBRE", selectedAlumno.getNombre());
                                intent.putExtra("ALUMNO_MATRICULA", selectedAlumno.getMatricula());
                                intent.putExtra("ALUMNO_FOTO", selectedAlumno.getFoto());
                                startActivityForResult(intent, REQUEST_EDIT_ALUMNO);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Alumno> listaFiltrada = db.buscarAlumnos(newText);
        listaAlumnos.clear();

        // Filtrar por nombre y matrícula
        for (Alumno alumno : listaFiltrada) {
            if (alumno.getNombre().toLowerCase().contains(newText.toLowerCase()) ||
                    alumno.getMatricula().toLowerCase().contains(newText.toLowerCase()) ||
                    alumno.getCarrera().toLowerCase().contains(newText.toLowerCase()))  {
                listaAlumnos.add(alumno);
            }
        }

        alumnoAdapter.notifyDataSetChanged();
        return true;
    }

    private void mostrarAlumnos() {
        listaAlumnos.clear();
        listaAlumnos.addAll(db.obtenerAlumnos());
        alumnoAdapter.notifyDataSetChanged();
    }

    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private GestureDetectorCompat gestureDetector;
        private MainActivity.RecyclerItemClickListener.OnItemClickListener mListener;

        interface OnItemClickListener {
            void onItemClick(View view, int position);
            void onLongItemClick(View view, int position);
        }

        RecyclerItemClickListener(Context context, final RecyclerView recyclerView, MainActivity.RecyclerItemClickListener.OnItemClickListener listener) {
            mListener = listener;
            gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null && mListener != null) {
                        mListener.onLongItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            });

            recyclerView.addOnItemTouchListener(this);
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && gestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }







    public void mostrarDatos(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void Salir(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de querer salir?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acciones a realizar si se selecciona "Sí"
                finishAffinity(); // Cierra todas las actividades y sale de la aplicación
                System.exit(0); // Cierra la aplicación
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acciones a realizar si se selecciona "No"
                dialog.dismiss(); // Cierra el diálogo sin realizar ninguna acción adicional
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


}