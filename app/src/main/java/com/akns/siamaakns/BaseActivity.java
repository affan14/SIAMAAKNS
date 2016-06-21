package com.akns.siamaakns;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public FrameLayout contentLayout;
    NavigationView navigationView;
    private boolean allowBackToExit;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        CircleImageView imgAcct = (CircleImageView) header.findViewById(R.id.account_img);
        AccountInfo.getInstance().setImageProfil(imgAcct);
        TextView txtEmail = (TextView) header.findViewById(R.id.account_email);
        txtEmail.setText(AccountInfo.getInstance().getAccountEmail());
        TextView txtName = (TextView) header.findViewById(R.id.account_name);
        txtName.setText(AccountInfo.getInstance().getAccountName());
        TextView txtNrp = (TextView)header.findViewById(R.id.account_nrp);
        txtNrp.setText(AccountInfo.getInstance().getNrp());

        // step 1
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .setHostedDomain(C.AKNS_DOMAIN)
                .requestEmail()
                .build();
        // step 2
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (savedInstanceState == null) {
            selectMenu(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getFragmentManager();
            if(fm.getBackStackEntryCount()>1){
                fm.popBackStack();
            } else {
                if (allowBackToExit) {
                    super.onBackPressed();
                    return;
                }
                allowBackToExit = true;
                Toast.makeText(BaseActivity.this, getText(R.string.toast_on_back_press), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        allowBackToExit = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(BaseActivity.this);
            niftyDialogBuilder
                    .withTitle(getString(R.string.action_about))
                    .withTitleColor(getResources().getColor(R.color.text_dark))
                    .withDividerColor(getResources().getColor(R.color.text_secondary))
                    .withMessage(getString(R.string.text_about))
                    .withMessageColor(getResources().getColor(R.color.text_dark))
                    .withDialogColor(getResources().getColor(R.color.text_bright))
                    .withDuration(700)
                    .withEffect(Effectstype.RotateBottom)
                    .withButton1Text("OK")
                    .isCancelableOnTouchOutside(true)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            niftyDialogBuilder.dismiss();
                        }
                    })
                    .show();
            return true;
        } else if (id == R.id.action_quit) {
            final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(BaseActivity.this);
            niftyDialogBuilder
                    .withTitle(getString(R.string.action_quit))
                    .withTitleColor(getResources().getColor(R.color.text_bright))
                    .withDividerColor(getResources().getColor(R.color.text_color))
                    .withMessage(getString(R.string.exit_confirmation))
                    .withMessageColor(getResources().getColor(R.color.text_bright))
                    .withDialogColor(getResources().getColor(R.color.swipe_color_2))
                    .withDuration(700)
                    .withEffect(Effectstype.Fadein)
                    .withButton1Text("Yes")
                    .withButton2Text("No")
                    .isCancelableOnTouchOutside(true)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    })
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            niftyDialogBuilder.dismiss();
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectMenu(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectMenu(int id) {
        navigationView.setCheckedItem(id);
        boolean logout = false;
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                getSupportActionBar().setTitle(R.string.nav_home);
                fragment = HomeFragment.newInstance();
                break;
            case R.id.nav_info:
                getSupportActionBar().setTitle(R.string.nav_notification);
                fragment = InfoFragment.newInstance();
                break;
            case R.id.nav_agenda:
                getSupportActionBar().setTitle(R.string.nav_agenda);
                fragment = AgendaFragment.newInstance();
                break;
            case R.id.nav_reg_status:
                getSupportActionBar().setTitle(R.string.nav_reg_status);
                fragment = RegStatusFragment.newInstance();
                break;
            case R.id.nav_grades:
                getSupportActionBar().setTitle(R.string.nav_grades);
                fragment = GradesFragment.newInstance();
                break;
            case R.id.nav_schedule:
                getSupportActionBar().setTitle(R.string.nav_schedule);
                fragment = ScheduleFragment.newInstance();
                break;
            case R.id.nav_logout:
                logout = true;
                doLogout();
                break;
        }
        if (!logout) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void doLogout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    startActivity(new Intent(BaseActivity.this, SplashActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}
