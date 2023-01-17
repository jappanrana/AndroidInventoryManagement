package com.example.androidinventorymanagement.ExportScreens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.androidinventorymanagement.HomeActivity;
import com.example.androidinventorymanagement.Models.ExportModel;
import com.example.androidinventorymanagement.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PDFExportActivity extends AppCompatActivity {

    String hours,minutes,seconds;
    TextView prodCode,prodName,prodRate;
    ImageView barcodeImage;
    CardView barcodeCardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_export);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("SelectedListIntent");
        ArrayList<ExportModel> SelectedList = (ArrayList<ExportModel>) args.getSerializable("SelectedList");

        prodCode = findViewById(R.id.ExpeachexportProductCode);
        prodName = findViewById(R.id.ExpeachexportProductName);
        prodRate = findViewById(R.id.ExpeachexportProductRate);
        barcodeImage = findViewById(R.id.ExpeachexportBarcodeImg);
        barcodeCardview = findViewById(R.id.ExpeachBarcodeCardView);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        Date dt = Calendar.getInstance().getTime();
        hours = String.valueOf(dt.getHours());
        minutes = String.valueOf(dt.getMinutes());
        seconds = String.valueOf(dt.getSeconds());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh-mm-ss aa", Locale.getDefault());
        String formattedDate = df.format(dt);
        String formattedTime = simpleDateFormat.format(calendar.getTime());
        String date = String.valueOf(dt.getDate());
        String month = getMonth(dt.getMonth());
        String year = String.valueOf(dt.getYear()+1900);

        String folderName = date+"-"+currentMonth+"-"+year+"_"+formattedTime;


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BarcodeEncoder encoder = new BarcodeEncoder();

        barcodeCardview.post(new Runnable() {
            @Override
            public void run() {
                String Key;
                BitMatrix matrix = null;
                Bitmap bitmap;
                for (ExportModel item:SelectedList) {
//                    prodCode.setText(item.getCode());
                    prodName.setText(item.getCode()+" "+item.getName());
                    prodRate.setText("MRP:₹"+item.getMrp()+"/-");
                    Key = item.getKey();
                    try {
                        matrix = multiFormatWriter.encode(Key, BarcodeFormat.CODE_39,500,100);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    bitmap = encoder.createBitmap(matrix);
                    barcodeImage.setImageBitmap(bitmap);

                    int width = barcodeCardview.getWidth();
                    int height = barcodeCardview.getHeight();
                    try {
                        createPDF(barcodeCardview,item.getName(), item.getCode(), height, width, folderName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(PDFExportActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Intent intent1 = new Intent(PDFExportActivity.this, HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
    }

    private void createPDF (View v, String name, String code, int height, int width, String folderName) throws FileNotFoundException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Download/PankajNX/QR/"+folderName+"/");
        dir.mkdirs();
        File file = new File(dir,name+"_"+code+".pdf");

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        PageSize pageSize = new PageSize(151, 115);
        Document document = new Document(pdfDocument,pageSize);
        document.setMargins(0,0,0,0);

        Bitmap bitmap = getBitmapFromView(v,height,width);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);

        document.add(image);
        document.close();
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    public String getMonth(int month){
        String monthString;
        switch (month) {
            case 1:  monthString = "January";
                break;
            case 2:  monthString = "February";
                break;
            case 3:  monthString = "March";
                break;
            case 4:  monthString = "April";
                break;
            case 5:  monthString = "May";
                break;
            case 6:  monthString = "June";
                break;
            case 7:  monthString = "July";
                break;
            case 8:  monthString = "August";
                break;
            case 9:  monthString = "September";
                break;
            case 10: monthString = "October";
                break;
            case 11: monthString = "November";
                break;
            case 12: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        return monthString;
    }


}