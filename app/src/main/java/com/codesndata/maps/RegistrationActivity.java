package com.codesndata.maps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codesndata.back_up.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

public class RegistrationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = RegistrationActivity.class.getSimpleName();
    Bitmap bitmap;
    Button UploadImageServer;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    //Declare Java fields
    private ImageButton imageView;
    private EditText name_View;
    private EditText surname_View;
    private EditText secret_code_View;
    private ProgressDialog progressDialog;
    // UI references.
    private AutoCompleteTextView email_View;

    String name, surname, email, secret_code, serial_no;


    //@SuppressLint({"WrongViewCast", "CutPasteId"})
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_registration);

        //Connect Java fields to XML fields
        name_View = findViewById(R.id.name);
        surname_View = findViewById(R.id.surname);
        email_View = findViewById(R.id.email);
        secret_code_View = findViewById(R.id.secret_code);
        //Image handling
        imageView = findViewById(R.id.imageButton);
        ImageButton selectImageGallery = findViewById(R.id.imageButton);
        UploadImageServer = findViewById(R.id.submit_button);
        progressDialog  = new ProgressDialog(this);
        // Set up the login form.
        email_View = findViewById(R.id.email);
        populateAutoComplete();

        Button quitButton = findViewById(R.id.cancel_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quit = new Intent(RegistrationActivity.this, MapsActivity.class);
                startActivity(quit);
            }
        });

        Button regButton = findViewById(R.id.submit_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {
                // Store values at the time of the registration attempt.
                name = name_View.getText().toString();
                surname = surname_View.getText().toString();
                email = email_View.getText().toString();
                secret_code = secret_code_View.getText().toString();
                //Automatically extract device serial number
                serial_no = Build.SERIAL;

                signup(name, surname, email, secret_code, serial_no, bitmap);
            }
        });


        selectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);

            }
        });

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email_View, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private boolean isAccountValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.length() > 6 && email.contains(".");
    }


    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegistrationActivity.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(RegistrationActivity.ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        email_View.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("HardwareIds")
    public void signup(final String name, final String surname, final String email, final String secret_code, final String serial_no, final Bitmap bitmap) {

        try {
            // Reset errors.
            email_View.setError(null);

            boolean cancel = false;
            View focusView = null;

            // Check for a valid email.
            if (TextUtils.isEmpty(email)) {
                email_View.setError(getString(R.string.error_field_required));
                focusView = email_View;
                cancel = true;
            } else if (!isAccountValid(email)) {
                email_View.setError(getString(R.string.error_invalid_email));
                focusView = email_View;
                cancel = true;
            }
            // Check whether name was entered.
            if (TextUtils.isEmpty(name)) {
                name_View.setError(getString(R.string.error_name_required));
                focusView = name_View;
                cancel = true;
            }
            // Check whether surname was entered.
            if (TextUtils.isEmpty(surname)) {
                surname_View.setError(getString(R.string.error_surname_required));
                focusView = surname_View;
                cancel = true;
            }
            // Check whether secret code was entered.
            if (TextUtils.isEmpty(secret_code)) {
                secret_code_View.setError(getString(R.string.error_secret_code_required));
                focusView = secret_code_View;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Tag used to cancel the request
                String tag_string_req = "req_signup";
                progressDialog.setMessage("Signing up...");
                progressDialog.show();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Utils.REGISTER_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                startActivity(new Intent(RegistrationActivity.this, MapsActivity.class));
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                toast(errorMsg);
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            toast("Json error: " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Sign Up Error: " + error.getMessage());
                        toast("Server says: " + error.getMessage());
                        progressDialog.hide();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<>();
                        params.put("name", name);
                        params.put("surname", surname);
                        params.put("email", email);
                        params.put("secret_code", secret_code);
                        params.put("serial_no", serial_no);
                        params.put("image", String.valueOf(bitmap));

                        return params;
                    }

                };

                // Adding request to request queue
                LoginController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }


}
