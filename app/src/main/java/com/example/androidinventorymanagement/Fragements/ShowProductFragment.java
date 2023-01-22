package com.example.androidinventorymanagement.Fragements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidinventorymanagement.ExportScreens.PDFExportActivity;
import com.example.androidinventorymanagement.Models.ExportModel;
import com.example.androidinventorymanagement.Models.ProductsModel;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.CommonMethods;
import com.example.androidinventorymanagement.Utils.CustomRangeInputFilter;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.button.MaterialButton;
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

public class ShowProductFragment extends Fragment {

    Context mContext;
    ImageView barcodeImage,backBtn;
    EditText prodCode,prodName,prodMrp,prodGst;
    String prodCodeStr,prodNameStr,prodMrpStr,prodGstStr,key;
    MaterialButton exportQRCodeBtn,submitBtn;
    LinearLayout deleteBtn;
    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase firebaseDatabase;
    String userRole;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        View ShowProductView = inflater.inflate(R.layout.fragment_show_product, container, false);

        barcodeImage = ShowProductView.findViewById(R.id.barcodeImg);
        prodCode = ShowProductView.findViewById(R.id.showProductCode);
        prodName = ShowProductView.findViewById(R.id.showProductName);
        prodGst = ShowProductView.findViewById(R.id.gstShowProduct);
        prodMrp = ShowProductView.findViewById(R.id.mrpShowProduct);
        backBtn = ShowProductView.findViewById(R.id.productBackButton);
        exportQRCodeBtn = ShowProductView.findViewById(R.id.ExportBtn);
        deleteBtn = ShowProductView.findViewById(R.id.deleteBtn);
        submitBtn = ShowProductView.findViewById(R.id.submitShowBtn);
        userRole = SharedPreferenceMethods.getSharedPrefUserRole(mContext);

        prodMrp.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,100000f)});

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProduct = firebaseDatabase.getReference("Products");

        prodGst.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,28.0f)});

        if (!userRole.equals("admin"))
        {
            submitBtn.setEnabled(false);
            prodMrp.setVisibility(View.GONE);
            if (userRole.equals("user"))
            {
                prodCode.setEnabled(false);
                prodName.setEnabled(false);
                prodGst.setVisibility(View.GONE);
            }
            if (userRole.equals("agent"))
            {
                prodCode.setEnabled(false);
                prodName.setEnabled(false);
                prodGst.setEnabled(false);
            }

        }

        ProductsModel showProduct = SharedPreferenceMethods.getSharedPrefShowProduct(mContext);
        prodCodeStr = showProduct.getCode();
        prodNameStr = showProduct.getName().toLowerCase(Locale.ROOT);
        prodMrpStr = showProduct.getMrp();
        prodGstStr = showProduct.getGstAmt();
        String finalname = prodNameStr.substring(0,1).toUpperCase(Locale.ROOT) + prodNameStr.substring(1).toLowerCase(Locale.ROOT);
        key = showProduct.getKey();

        prodCode.setText(prodCodeStr);
        prodName.setText(finalname);
        prodMrp.setText(prodMrpStr);
        prodGst.setText(prodGstStr);

        exportQRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "work in progress", Toast.LENGTH_SHORT).show();
                String mProdName = prodName.getText().toString().toLowerCase(Locale.ROOT);
                String finalname = mProdName.substring(0,1).toUpperCase(Locale.ROOT) + mProdName.substring(1).toLowerCase(Locale.ROOT);
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdGst = prodGst.getText().toString();

                String hours,minutes,seconds;
                TextView prodCode,prodName,prodRate;
                ImageView barcodeImage;
                CardView barcodeCardview;

                prodCode = getActivity().findViewById(R.id.showProductExportexportProductCode);
                prodName = getActivity().findViewById(R.id.showProductExportexportProductName);
                prodRate = getActivity().findViewById(R.id.showProductExportexportProductRate);
                barcodeImage = getActivity().findViewById(R.id.showProductExportexportBarcodeImg);
                barcodeCardview = getActivity().findViewById(R.id.showProductExportBarcodeCardView);

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
//                    prodCode.setText(item.getCode());
                        prodName.setText(mProdCode+" "+finalname);
                        prodRate.setText("MRP:₹"+MProdMrp+"/-");
                        Key = key;
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
                            createPDF(barcodeCardview,finalname, mProdCode, height, width, folderName);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix matrix = null;
        try {
            matrix = multiFormatWriter.encode(key, BarcodeFormat.CODE_39,500,100);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder encoder = new BarcodeEncoder();
        Bitmap bitmap = encoder.createBitmap(matrix);
        barcodeImage.setImageBitmap(bitmap);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mProdName = prodName.getText().toString().toLowerCase(Locale.ROOT);
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdGst = prodGst.getText().toString();

                if (!mProdName.equals("") && !mProdCode.equals("") && !MProdMrp.equals("") && !MProdGst.equals(""))
                {
                    if (mProdCode.equals(prodCodeStr))
                    {
                        databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                        databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                        databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                        databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst).toString();
                        hideKeyboard(v);
                        Toast.makeText(mContext, "Successfully Updated", Toast.LENGTH_SHORT).show();
                        HomeFragment homeFragment = new HomeFragment();
                        getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                    }else
                    {
                        databaseReferenceProduct.orderByChild("code").equalTo(mProdCode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                databaseReferenceProduct.orderByChild("code").equalTo(mProdCode).removeEventListener(this);

                                if (snapshot.exists())
                                {
                                    Toast.makeText(mContext, "Code Already Used", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                                    databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                                    databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                                    databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst).toString();
                                    hideKeyboard(v);
                                    Toast.makeText(mContext, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                    HomeFragment homeFragment = new HomeFragment();
                                    getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else
                    Toast.makeText(mContext, "Please enter values to save", Toast.LENGTH_SHORT).show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                builder.setTitle("Remove")
                        .setMessage("Do you want to delete this product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReferenceProduct.child(key).removeValue();
                                HomeFragment homeFragment = new HomeFragment();
                                getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                            }
                        })
                        .setNegativeButton("No", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                Button btnPositive = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);

            }
        });

        return ShowProductView;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();

        requireView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                        return true;
                    }
                }
                return false;
            }
        });
        super.onActivityCreated(savedInstanceState);
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

    private void createPDF (View v, String name, String code, int height, int width, String folderName) throws FileNotFoundException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Download/PankajNX/QR/"+folderName+"/");
        dir.mkdirs();
        File file = new File(dir,name+"_"+code+".pdf");

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        PageSize pageSize = new PageSize(151, 115);
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
    }


}