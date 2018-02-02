package com.qiscus.internship.sudutnegeri.ui.addproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiscus.internship.sudutnegeri.R;
import com.qiscus.internship.sudutnegeri.data.model.DataProject;
import com.qiscus.internship.sudutnegeri.data.model.DataUser;
import com.qiscus.internship.sudutnegeri.ui.dashboard.DashboardActivity;
import com.qiscus.internship.sudutnegeri.util.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Random;

public class AddProjectActivity extends AppCompatActivity implements AddProjectView {

    private AddProjectPresenter addProjectPresenter;
    private DataProject dataProject;
    private DataUser dataUser;
    private Uri uri;
    AnimationDrawable animationDrawable;
    Button btnAddPCreate, btnAddPTaget, btnAddPPhoto;
    ConstraintLayout constraintLayout;
    DatePickerDialog datePickerDialog;
    EditText etAddPName, etAddPLocation, etAddPInformation, etAddPTFund;
    File file, newFile;
    int target_fund;
    ProgressDialog progressDialog = null;
    String project_name, location, target_at, information, photo, random;
    TextView tvPopupMsg, tvPopupType, tvPopupSType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        initPresenter();
        initView();
        initAnimation();
        initDataIntent();

        postProject();
        setupDate();
        pickImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }

    private void pickImage() {
        btnAddPPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, 200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            String filePath = getRealPathFromURIPath(uri, AddProjectActivity.this);
            file = new File(filePath);
            newFile = saveBitmapToFile(file);
            btnAddPPhoto.setText(newFile.getName());
        }
    }

    @NonNull
    private static String random(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(Constant.ALLOWED_CHARACTERS.charAt(random.nextInt(Constant.ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private String getRealPathFromURIPath(Uri uri, AddProjectActivity addProjectActivity) {
        Cursor cursor = addProjectActivity.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public File saveBitmapToFile(File file){
        try {
            final int REQUIRED_SIZE=75;
            int scale = 1;

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private void initPresenter() {
        addProjectPresenter = new AddProjectPresenter(this);
    }

    private void initView() {
        btnAddPCreate = findViewById(R.id.btnAddPCreate);
        btnAddPTaget = findViewById(R.id.btnAddPTarget);
        btnAddPPhoto = findViewById(R.id.btnAddPPhoto);
        constraintLayout = findViewById(R.id.rlAddP);
        etAddPName = findViewById(R.id.etAddPName);
        etAddPLocation = findViewById(R.id.etAddPLocation);
        etAddPInformation = findViewById(R.id.etAddPInformation);
        etAddPTFund = findViewById(R.id.etAddPTFund);
    }

    private void initAnimation() {
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
    }

    private void initDataIntent() {
        dataUser = getIntent().getParcelableExtra(Constant.Extra.User);
    }

    private void setupDate(){
        btnAddPTaget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(AddProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                btnAddPTaget.setText(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void setupVariable(){
        project_name = etAddPName.getText().toString();
        location = etAddPLocation.getText().toString();
        information = etAddPInformation.getText().toString();
        target_at = btnAddPTaget.getText().toString();
        photo = btnAddPPhoto.getText().toString();
        target_fund = Integer.parseInt(etAddPTFund.getText().toString());
    }

    private boolean validate(){
        validName();
        validLocation();
        validTarget();
        validTargetFund();
        validPhoto();
        validInformation();

        if (validName()==false || validLocation()==false || validTarget()==false || validTargetFund()==false || validPhoto()==false || validInformation()==false){
            return false;
        }
        return true;
    }

    private boolean validName(){
        if (project_name.isEmpty()){
            etAddPName.setBackgroundResource(R.drawable.bg_rounded_trans_red);
            etAddPName.setHint("Isikan nama projek");
            return false;
        }
        etAddPName.setBackgroundResource(R.drawable.bg_rounded_trans_green);
        return true;
    }

    private boolean validLocation(){
        if(location.isEmpty()) {
            etAddPLocation.setBackgroundResource(R.drawable.bg_rounded_trans_red);
            etAddPLocation.setHint("Isikan lokasi");
            return false;
        }
        etAddPLocation.setBackgroundResource(R.drawable.bg_rounded_trans_green);
        return true;
    }

    private boolean validTarget(){
        if (target_at.isEmpty()) {
            btnAddPTaget.setBackgroundResource(R.drawable.bg_rounded_trans_red);
            btnAddPTaget.setHint("Pilih tanggal target");
            return false;
        }
        btnAddPTaget.setBackgroundResource(R.drawable.bg_rounded_trans_green);
        return true;
    }

    private boolean validTargetFund(){
        if (target_fund<=10000){
            etAddPTFund.setBackgroundResource(R.drawable.bg_rounded_trans_red);
            etAddPTFund.setHint("Isikan target dana minimal Rp 10000");
            etAddPTFund.setText("");
            return false;
        }
        etAddPTFund.setBackgroundResource(R.drawable.bg_rounded_trans_green);
        return true;
    }

    private boolean validPhoto(){
        if (photo.isEmpty()){
            btnAddPPhoto.setBackgroundResource(R.drawable.bg_rounded_trans_red);
            btnAddPPhoto.setHint("Pilih foto dari gallery");
            return false;
        } else {
            btnAddPPhoto.setBackgroundResource(R.drawable.bg_rounded_trans_green);
            return true;
        }
    }

    private boolean validInformation(){
        if (information.isEmpty()) {
            etAddPInformation.setBackgroundResource(R.drawable.bg_rounded_trans_red);
            etAddPInformation.setHint("Isikan informasi");
            return false;
        }
        etAddPInformation.setBackgroundResource(R.drawable.bg_rounded_trans_green);
        return true;
    }

    private void postProject() {
        btnAddPCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupVariable();
                if (validate()) {
                    progressDialog = new ProgressDialog(AddProjectActivity.this);
                    progressDialog.setTitle(null);
                    progressDialog.setMessage("Mengunggah projek");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    random = random(32);
                    addProjectPresenter.uploadFile(newFile, dataUser, random);
                }
            }
        });
    }

    private void initPopupWindow(String messsage, String error) {
        try {
            LayoutInflater inflater = (LayoutInflater) AddProjectActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (messsage.equals("success")){
                View layout = inflater.inflate(R.layout.layout_popup_success,
                        null);
                final PopupWindow pw = new PopupWindow(layout, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, false);
                pw.setOutsideTouchable(false);
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                tvPopupSType = layout.findViewById(R.id.tvPopupSType);
                tvPopupSType.setText("Berhasil");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Intent user = new Intent(AddProjectActivity.this, DashboardActivity.class);
                        user.putExtra(Constant.Extra.DATA, dataUser);
                        startActivity(user);
                        finish();
                    }

                    private void finish() {
                        // TODO Auto-generated method stub

                    }
                }, 1000);

            } else {
                View layout = inflater.inflate(R.layout.layout_popup_failed, null);
                final PopupWindow pw = new PopupWindow(layout, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                tvPopupMsg = layout.findViewById(R.id.tvPopupFMsg);
                tvPopupType = layout.findViewById(R.id.tvPopupFType);
                tvPopupMsg.setText(error);
                tvPopupType.setText("Gagal Bergabung");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProjectName() {
        return project_name;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getTarget() {
        return target_at;
    }

    @Override
    public String getInformation() {
        return information;
    }

    @Override
    public String getPhoto() {
        return photo;
    }

    @Override
    public void successPostProject() {
        progressDialog.dismiss();
        initPopupWindow("success", null);
    }

    @Override
    public void failedPostProject(String message) {
        progressDialog.dismiss();
        initPopupWindow("failed", message);
    }

    @Override
    public void failedUploadFile() {
        progressDialog.dismiss();
        initPopupWindow("failed", "Tidak dapat mengunggah gambar");
    }

    @Override
    public int getTargetFunds() {
        return 0;
    }
}
