package com.example.mariangeles.practica6pmdm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
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


public class Vista extends View {

    public int accion=Actividad.AC_PINCEL;
    /**
     * 1.-Rectas
     * 2.-Cuadrado
     * 3.-Circulos
     * 4.-Pincel
     * 5.-Goma
     */

    private float x0=-1, y0=-1;
    private float xi=-1, yi=-1;
    private Paint pincel;
    private double radio=0;
    public int color=Color.BLACK;
    public int grosor;

    public  Bitmap mapaDeBits;
    public  Canvas lienzoFondo;
    private Path rectaPoligonal;


    public Vista(Context context) {
        super(context);
        pincel = new Paint();
        pincel.setAntiAlias(true);

        rectaPoligonal = new Path();
        pincel.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //pulsamos en la pantalla -> empezamos a dibujar
                if(accion == Actividad.AC_GOMA)
                    pincel.setColor(Color.WHITE);
                else
                    pincel.setColor(color);

                x0=xi=x;
                y0=yi=y;
                rectaPoligonal.reset();
                rectaPoligonal.moveTo(x0, y0);

                break;
            case MotionEvent.ACTION_MOVE:
                //movemos
                if(accion==Actividad.AC_PINCEL ||
                        accion==Actividad.AC_GOMA){
                    rectaPoligonal.quadTo(xi, yi,(x+xi)/2, (y+yi)/2);
                }
                xi=x;
                yi=y;
                invalidate();//REDIBUJAR
                break;

            case MotionEvent.ACTION_UP:
                //levantamos -> dejamos de pintar
                dibujar();
                //invalidate();//REDIBUJAR
                rectaPoligonal.reset();
                //x0=y0=xi=yi=-1;
                break;
        }
        //return true si se captura el evento!!
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mapaDeBits = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);
        borrar();
    }

    @Override
    public void onDraw(Canvas lienzo){
        super.onDraw(lienzo);
        lienzo.drawBitmap(mapaDeBits, 0, 0, null);

        switch(accion){
            case Actividad.AC_RECTA:
                //Rectas
                lienzo.drawLine(x0,y0,xi,yi,pincel);
                break;

            case Actividad.AC_CUADRADO:
                //Cuadrados
                float xrec=xi,xrec0=x0,yrec=yi,yrec0=y0;
                //pintamos rectangulos en todos los sentidos
                if(xi<x0){
                    xrec=x0;
                    xrec0=xi;
                }
                if(yi<y0){
                    yrec=y0;
                    yrec0=yi;
                }
                Rect recta= new Rect((int)xrec0,(int)yrec0,(int)xrec,(int)yrec);
                //le pasamos las coordenadas a recta
                lienzo.drawRect(recta,pincel);
                break;

            case Actividad.AC_CIRCULO:
                //circulos
                radio=Math.sqrt(Math.pow(xi-x0,2)+Math.pow(yi-y0,2));
                lienzo.drawCircle(xi,yi,(float)radio,pincel);
                break;

            case Actividad.AC_PINCEL:
                //mano alzada

            case Actividad.AC_GOMA:
                //goma
                lienzoFondo.drawPath(rectaPoligonal, pincel);
                break;
        }
    }

    /*****    MEtodo *****/

    public void dibujar(){
        switch(accion){
            case Actividad.AC_RECTA:
                //Rectas
                lienzoFondo.drawLine(x0,y0,xi,yi,pincel);
                break;

            case Actividad.AC_CUADRADO:
                //Cuadrados
                float xrec=xi,xrec0=x0,yrec=yi,yrec0=y0;
                //pintamos rectangulos en todos los sentidos
                if(xi<x0){
                    xrec=x0;
                    xrec0=xi;
                }
                if(yi<y0){
                    yrec=y0;
                    yrec0=yi;
                }
                Rect recta= new Rect((int)xrec0,(int)yrec0,(int)xrec,(int)yrec);
                //le pasamos las coordenadas a recta
                lienzoFondo.drawRect(recta,pincel);
                break;

            case Actividad.AC_CIRCULO:
                //Circulos
                radio=Math.sqrt(Math.pow(xi-x0,2)+Math.pow(yi-y0,2));
                lienzoFondo.drawCircle(xi,yi,(float)radio,pincel);
                break;

            case Actividad.AC_PINCEL:

            case Actividad.AC_GOMA:
                lienzoFondo.drawPath(rectaPoligonal, pincel);
                break;
        }
    }

    public void borrar(){
        lienzoFondo.drawColor(Color.WHITE);
    }

    public void guardar(){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String formateDate = df.format(date);
        File file = new File(path, formateDate+".jpg");
        OutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mapaDeBits.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void grosor(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        alert.setTitle("Grosor");
        final View vista = inflater.inflate(R.layout.grosor, null);
        alert.setView(vista);
        final TextView valor = (TextView) vista.findViewById(R.id.tv);
        final SeekBar barra = (SeekBar) vista.findViewById(R.id.barra);

        barra.setMax(100);
        barra.setProgress((int) pincel.getStrokeWidth());

        valor.setText(barra.getProgress() + "");
        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valor.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                pincel.setStrokeWidth(barra.getProgress());
            }
        });
        alert.show();
    }
}