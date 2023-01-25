package com.example.androidinventorymanagement.ExportScreens;

import static com.example.androidinventorymanagement.Utils.CommonMethods.getBitmapFromView;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.androidinventorymanagement.HomeActivity;
import com.example.androidinventorymanagement.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PDFExportAllActivity extends AppCompatActivity
{
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceExport;
    String hours,minutes,seconds;

    TextView prodCode,prodName,prodRate;
    ImageView barcodeImage;
    ConstraintLayout barcodeCardview;
//rename to export all
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_export_all);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceExport = firebaseDatabase.getReference("Products");

        prodCode = findViewById(R.id.pdfExpexportProductCode);
        prodName = findViewById(R.id.pdfExpexportProductName);
        prodRate = findViewById(R.id.pdfExpexportProductRate);
        barcodeImage = findViewById(R.id.pdfExpexportBarcodeImg);
        barcodeCardview = findViewById(R.id.pdfExpBarcodeCardView);

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

        databaseReferenceExport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReferenceExport.removeEventListener(this);
                if(snapshot!=null){
                String Key;
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    BitMatrix matrix = null;
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap;

                    for(DataSnapshot product: snapshot.getChildren()){
                        prodCode.setText(product.child("code").getValue().toString());
                        prodName.setText(product.child("name").getValue().toString());
                        prodRate.setText("MRP:"+product.child("mrp").getValue().toString()+"/-");
                        Key = product.child("key").getValue().toString();
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
                            createPDF(barcodeCardview,product.child("name").getValue().toString(), product.child("code").getValue().toString(),height, width, folderName);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(PDFExportAllActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(PDFExportAllActivity.this, "No  Items Found", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(PDFExportAllActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PDFExportAllActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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