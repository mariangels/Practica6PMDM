package com.example.mariangeles.practica6pmdm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Actividad extends Activity implements Picker.OnColorChangedListener{

    private Vista v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v=new Vista(this);
        setContentView(v);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    static final int AC_RECTA =1;
    static final int AC_CUADRADO =2;
    static final int AC_CIRCULO =3;
    static final int AC_PINCEL =4;
    static final int AC_GOMA =5;
    private boolean fondo=false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.linea:
                v.accion =AC_RECTA;
                break;
            case R.id.cuadrado:
                v.accion =AC_CUADRADO;
                break;
            case R.id.circulo:
                v.accion =AC_CIRCULO;
                break;
            case R.id.pincel:
                v.accion =AC_PINCEL;
                break;
            case R.id.goma:
                v.accion =AC_GOMA;
                break;

            case R.id.guardar:
                v.guardar();
                Toast.makeText(this, "guardado", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nuevo:
                v.guardar();
            case R.id.borrar:
                limpiar();
                break;

            case R.id.grosor:
                v.grosor();
                break;
            case R.id.color:
                new Picker(this, Actividad.this, Color.WHITE).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void colorChanged(int color) {
        v.color = color;
    }

    public void  limpiar(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("¿Estas seguro? Se borraran los cámbios");
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.borrar, null);
        alert.setView(vista);
        alert.setPositiveButton("borrar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        v.borrar();
                    }
                });
        alert.setNegativeButton("cancelar", null);
        alert.show();
    }
}
