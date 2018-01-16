package com.qiscus.internship.sudutnegeri.ui.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiscus.internship.sudutnegeri.R;
import com.qiscus.internship.sudutnegeri.data.model.DataUser;
import com.qiscus.internship.sudutnegeri.ui.about.AboutActivity;
import com.qiscus.internship.sudutnegeri.ui.dashboard.DashboardActivity;
import com.qiscus.internship.sudutnegeri.ui.landing.LandingActivity;
import com.qiscus.internship.sudutnegeri.ui.register.RegisterActivity;
import com.qiscus.internship.sudutnegeri.util.Constant;

public class UserActivity extends AppCompatActivity implements UserView {

    private UserPresenter userPresenter;
    private DataUser dataUser;
    Button btnUserSave, btnDrawerLogout, btnPopupFRetry, btnPopupSNext;;
    DrawerLayout drawerLayout;
    EditText etUserName, etUserEmail, etUserIdNumber, etUserAddress, etUserPhone;
    ImageView ivDrawerPhoto;
    NavigationView navigationView;
    String email, passwd, param;
    TextView title, tvDrawerName, tvPopupFMsg, tvPopupFType, tvPopupSMsg, tvPopupSType;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user);
        setTitle("");
        initPresenter();
        initPreference();
        initView();
        initNavigation();
        initDataIntent();
        initDataPresenter();
        initDataDrawer();
        initEditable();
        setupToolbar();

        putUser();
        logout();
    }

    private void initPresenter() {
        userPresenter = new UserPresenter(this);
    }

    private void initPreference(){
        SharedPreferences preferences = getSharedPreferences("LoginPreference", MODE_PRIVATE);
        email = preferences.getString("email", "");
        passwd = preferences.getString("password", "");
    }

    private void initView() {
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etuserEmail);
        etUserIdNumber = findViewById(R.id.etUserIdNumber);
        etUserAddress = findViewById(R.id.etUserAddress);
        etUserPhone = findViewById(R.id.etUserPhone);
        btnUserSave = findViewById(R.id.btnUserSave);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.tvToolbarTitle);
        tvDrawerName = findViewById(R.id.tvDrawerName);
        ivDrawerPhoto = findViewById(R.id.ivDrawerUser);
        btnDrawerLogout = findViewById(R.id.btnDrawerLogout);
        drawerLayout = findViewById(R.id.dlUser);
        navigationView = findViewById(R.id.nvUser);
        title.setText("Profile");
    }

    private void initNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.navigation1:
                        Intent dashboard = new Intent(UserActivity.this, DashboardActivity.class);
                        dashboard.putExtra(Constant.Extra.DATA, dataUser);
                        startActivity(dashboard);
                        finish();
                        return false;
                    case R.id.navigation2:
                        Toast.makeText(getApplicationContext(),"Anda Berada di Profil",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation3:
                        Intent aboutUs = new Intent(UserActivity.this, AboutActivity.class);
                        aboutUs.putExtra(Constant.Extra.DATA, dataUser);
                        startActivity(aboutUs);
                        finish();
                        return false;
                    default:
                        Toast.makeText(getApplicationContext(),"Kesalahan Terjadi ",Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
    }

    private void initDataIntent() {
        dataUser = getIntent().getParcelableExtra(Constant.Extra.DATA);
        param = getIntent().getStringExtra(Constant.Extra.param);
        if (dataUser == null) finish();
    }

    private void initDataPresenter(){
        userPresenter.getUserById(dataUser);
    }

    private void initDataDrawer() {
        tvDrawerName.setText(dataUser.getName());

        //Picasso.with(this)
        //        .load(dataUser.getAddress())
        //        .into(ivDrawerPhoto);
    }

    private void initEditable() {
        if (param.equalsIgnoreCase("admin")){
            btnUserSave.setText("Verifikasi");
            etUserName.setEnabled(false);
            etUserEmail.setEnabled(false);
            etUserIdNumber.setEnabled(false);
            etUserAddress.setEnabled(false);
            etUserPhone.setEnabled(false);
            navigationView.setVisibility(View.GONE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            btnUserSave.setText("Simpan");
            etUserEmail.setEnabled(false);
            etUserIdNumber.setEnabled(false);
        }
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void putUser() {
        btnUserSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPresenter.putUser(dataUser);
            }
        });
    }

    private void popupWindow(String messsage) {
        try {
            LayoutInflater inflater = (LayoutInflater) UserActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (messsage.equals("success")){
                View layout = inflater.inflate(R.layout.layout_popup_success, null);
                final PopupWindow pw = new PopupWindow(layout, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                pw.setOutsideTouchable(false);
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                btnPopupSNext = layout.findViewById(R.id.btnPopupSNext);
                tvPopupSMsg = layout.findViewById(R.id.tvPopupSMsg);
                tvPopupSType = layout.findViewById(R.id.tvPopupSType);

                tvPopupSType.setText("Selamat");
                tvPopupSMsg.setText("Profil anda berhasil diperbarui");
                btnPopupSNext.setVisibility(View.GONE);

            } else {
                View layout = inflater.inflate(R.layout.layout_popup_failed, null);
                final PopupWindow pw = new PopupWindow(layout, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                tvPopupFMsg = layout.findViewById(R.id.tvPopupFMsg);
                tvPopupFType = layout.findViewById(R.id.tvPopupFType);
                tvPopupFMsg.setText(messsage);
                tvPopupFType.setText("Gagal merubah profil");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout(){
        btnDrawerLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                builder.setMessage("Apakah anda yakin ingin keluar ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        userPresenter.logout();
                    }
                });
                builder.setNegativeButton("Tidak", null);
                builder.show();
            }
        });
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return email;
    }

    @Override
    public String getName() {
        return etUserName.getText().toString();
    }

    @Override
    public String getPhone() {
        return etUserPhone.getText().toString();
    }

    @Override
    public String getAddress() {
        return etUserAddress.getText().toString();
    }

    @Override
    public String getVerify() {
        return dataUser.getVerify();
    }

    @Override
    public void successUserbyId(DataUser dataUser) {
        etUserName.setText(dataUser.getName());
        etUserEmail.setText(dataUser.getEmail());
        etUserIdNumber.setText(dataUser.getIdentityNumber());
        etUserAddress.setText(dataUser.getAddress());
        etUserPhone.setText(dataUser.getPhone());
    }

    @Override
    public void successPut(DataUser dataUser) {
        popupWindow("success");
        this.dataUser = dataUser;
        initDataDrawer();
    }

    @Override
    public void successLogout() {
        Intent logout = new Intent(UserActivity.this, LandingActivity.class);
        startActivity(logout);
        finish();
    }

}