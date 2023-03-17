package com.example.androidinventorymanagement.Activities;

import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.getSharedPrefGenerateType;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Adapters.PdfDocumentAdapter;
import com.example.androidinventorymanagement.Adapters.SqlQuoteAdapter;
import com.example.androidinventorymanagement.Models.QuotationModel;
import com.example.androidinventorymanagement.Models.QuoteModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.CommonMethods;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FinalQuoteActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{

    Context mContext;
    RecyclerView recyclerView;
    TextView subTotalAmt,totalGstAmt,grandTotal,discountAmt,delete,editedTextView,quoteSaleTextView;
    ImageView finalQuoteBackBtn;
    MaterialButton sharedPDF, printPDF, DownloadPDF;
    LinearLayout quoteLayout;
    TextView customerName,customerNo,date;
    String discount = "no";
    String getCustomerName,getCustomerNo;
    SqlQuoteAdapter sqlQuoteAdapter;
    LinearLayout discountLayout;
    boolean edited;
    ArrayList<QuoteModel> dataholder = new ArrayList<>();
    DatabaseReference quotationDatabase;
    String key;
    String uniqueKey;


    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_quote);

        mContext = FinalQuoteActivity.this;
        recyclerView = findViewById(R.id.finalQuoteRecyclerview);
        subTotalAmt = findViewById(R.id.finalQuotesubTotalAmt);
        totalGstAmt = findViewById(R.id.finalQuotetotalGstAmt);
        grandTotal = findViewById(R.id.finalQuotegrandTotal);
        discountAmt = findViewById(R.id.finalQuotedisAmt);
        finalQuoteBackBtn = findViewById(R.id.finalQuoteBackButton);
        delete = findViewById(R.id.finalQuotedeleted);
        sharedPDF = findViewById(R.id.finalQuotesharePDF);
        quoteLayout = findViewById(R.id.finalQuotequoteDetailsLayout);
        customerNo = findViewById(R.id.finalQuotecustomerNoQuote);
        customerName = findViewById(R.id.finalQuotecustomerNameQuote);
        date = findViewById(R.id.finalQuotedateOnQuote);
        editedTextView = findViewById(R.id.editTextViewFinal);
        DownloadPDF = findViewById(R.id.finalQuoteDownloadPDF);
        discountLayout = findViewById(R.id.finalQuotediscountQuoteLayout);
        printPDF = findViewById(R.id.finalQuotePrintPDF);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        discount = SharedPreferenceMethods.getSharedPrefDisAvailable(mContext);
        getCustomerName = SharedPreferenceMethods.getSharedPrefCustomerName(mContext);
        getCustomerNo  = SharedPreferenceMethods.getSharedPrefCustomerNumber(mContext);
        customerName.setText(getCustomerName);
        customerNo.setText(getCustomerNo);
        quoteSaleTextView = findViewById(R.id.quoteSaleTextView);

        quoteSaleTextView.setText(getSharedPrefGenerateType(mContext));

        key = SharedPreferenceMethods.getSharedPrefCustomerKey(mContext);

        quotationDatabase = FirebaseDatabase.getInstance().getReference("quotations");

        //save contact permission

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_CONTACTS},
                PackageManager.PERMISSION_GRANTED);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String textViewDate = df.format(c);

        date.setText(textViewDate);
        Date dt = new Date();


        edited = SharedPreferenceMethods.getSharedPrefEditable(mContext);

        if (edited)
        {
            editedTextView.setVisibility(View.VISIBLE);
        }
        if (!discount.equals("no"))
        {
            discountLayout.setVisibility(View.VISIBLE);
            discountAmt.setText(discount);
        }

        sqlQuoteAdapter = new SqlQuoteAdapter(dataholder,subTotalAmt,totalGstAmt,grandTotal,discountAmt);
        recyclerView.setAdapter(sqlQuoteAdapter);
        sqlQuoteAdapter.setVisibility();

        Cursor cursor = new DbManager(this).readalldata();

        while (cursor.moveToNext())
        {
            QuoteModel model = new QuoteModel(cursor.getString(2),cursor.getFloat(4),cursor.getString(3),
                    cursor.getString(5),cursor.getString(3),cursor.getString(6));
            dataholder.add(model);
            Set<QuoteModel> set = new HashSet<>(dataholder);
            dataholder.clear();
            dataholder.addAll(set);
        }


        Set<QuoteModel> set=new HashSet<>(dataholder);
        List<QuoteModel> allQuoteItemList = new ArrayList<>();

        allQuoteItemList.addAll(set);

        Log.e("listvalue",allQuoteItemList.toString());

        finalQuoteBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.finalEditQuotePDF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceMethods.setSharedPrefEditable(mContext, true);
                finish();
            }
        });
        final String[] savedPath = new String[1];
        final String[] formattedDate = new String[1];
        final String[] fileName = new String[1];

        DownloadPDF.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Date dt = Calendar.getInstance().getTime();
                    String hours = String.valueOf(dt.getHours());
                    String minutes = String.valueOf(dt.getMinutes());
                    String seconds = String.valueOf(dt.getSeconds());
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    formattedDate[0] = df.format(dt);

                    fileName[0] = hours+"-"+minutes+"-"+seconds;
                    savedPath[0] = createNPrintPDF(quoteLayout, formattedDate[0], fileName[0],quoteLayout.getHeight(),quoteLayout.getWidth());
                    DownloadPDF.setVisibility(View.GONE);
                    sharedPDF.setVisibility(View.VISIBLE);
                    printPDF.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(FinalQuoteActivity.this, "some error occurs", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sharedPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceMethods.setSharedPrefDisAvailable(mContext, "0");
                sharePDF(formattedDate[0],fileName[0]);
            }
        });


        printPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPrint(savedPath[0]);
            }
        });
    }

    private void doPrint(String path) {
//        Uri pdfUri = FileProvider.getUriForFile(this, "com.simple.simpleqrcode.fileprovider", path);

        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(FinalQuoteActivity.this, path);
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }catch (Exception ex){
            Toast.makeText(FinalQuoteActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveClientContact() {
        ArrayList<ContentProviderOperation> contentProviderOperations
                = new ArrayList<ContentProviderOperation>();

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        if (!contactExists(getApplicationContext(),customerNo.getText().toString())) {

            Toast.makeText(this, "Unique", Toast.LENGTH_SHORT).show();
            // Adding Name
            contentProviderOperations.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            customerName.getText().toString())
                    .build());

            // Adding Number
            contentProviderOperations.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                            customerNo.getText().toString())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());

            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "Already Exist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferenceMethods.setSharedPrefDisAvailable(mContext,"0");
        SharedPreferenceMethods.setSharedPrefEditable(mContext,true);
        finish();
    }

    public void sharePDF(String folderName, String fileName){
        File dir = new File(Environment.getExternalStorageDirectory() + "/Download/PankajNX/Quote/"+folderName+"/");
        dir.mkdirs();
        File file = new File(dir,customerName.getText().toString()+"_"+customerNo.getText().toString()+"_"+fileName+".pdf");

        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.androidinventorymanagement.fileprovider", file);

        if (file.exists())
        {
            Log.e("Hey", file.getAbsolutePath().toString());

            String toNumber = customerNo.getText().toString(); // contains spaces.
            toNumber = toNumber.replace("+", "").replace(" ", "");

            Intent shareIntent = new Intent();
            shareIntent.putExtra(Intent.EXTRA_STREAM,pdfUri);
            shareIntent.putExtra("jid",toNumber + "@s.whatsapp.net");
            shareIntent.setAction(Intent.ACTION_SEND);

            try {
                shareIntent.setPackage("com.whatsapp");
                shareIntent.setType("application/pdf");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(shareIntent);
            }catch (Exception e)
            {
                try {
                    shareIntent.setPackage("com.whatsapp.w4b");
                    shareIntent.setType("application/pdf");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(shareIntent);
                } catch (Exception exception){
                    Toast.makeText(this, "Install Whatsapp", Toast.LENGTH_SHORT).show();
                }
            }

//            startActivity(Intent.createChooser(shareIntent,"Share Via"));

        }
        else
        {
            Log.e("Hey", file.getAbsolutePath().toString());
            Toast.makeText(this, "File Does not Exist", Toast.LENGTH_SHORT).show();
        }

    }


    private String createNPrintPDF (View v, String folderName, String fileName, int height, int width) throws FileNotFoundException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Download/PankajNX/Quote/"+folderName+"/");
        dir.mkdirs();
        File file = new File(dir,customerName.getText().toString()+"_"+customerNo.getText().toString()+"_"+fileName+".pdf");

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        int newHeight = (int) ((int) height/1.8);
        PageSize pageSize = new PageSize(590, newHeight);
        Document document = new Document(pdfDocument,pageSize);
        document.setMargins(0,0,0,0);

        Bitmap bitmap = CommonMethods.getBitmapFromView(v,height,width);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);

        document.add(image);
        document.close();
        if (edited)
        {
            uniqueKey = key;
        }
        else
        {
            uniqueKey = quotationDatabase.push().getKey();
        }

        QuotationModel quotation = new QuotationModel(
                getCustomerName,
                getCustomerNo,
                folderName+"_"+fileName,
                uniqueKey,
                customerName.getText().toString().toLowerCase(Locale.ROOT)+"_"+customerNo.getText().toString()+"_"+folderName,
                getSharedPrefGenerateType(mContext),
                Integer.parseInt(discountAmt.getText().toString()),
                Integer.parseInt(totalGstAmt.getText().toString()),
                Integer.parseInt(subTotalAmt.getText().toString()),
                Integer.parseInt(grandTotal.getText().toString()),
                dataholder
        );

        quotationDatabase.child(uniqueKey).setValue(quotation);
        Toast.makeText(this, "Quotation Generated Successfully!", Toast.LENGTH_SHORT).show();
        return dir.getPath()+"/"+customerName.getText().toString()+"_"+customerNo.getText().toString()+"_"+fileName+".pdf";
    }

    public boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        }
        finally {
            if (cur != null){
                cur.close();
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferenceMethods.setSharedPrefEditable(mContext,true);
    }
}