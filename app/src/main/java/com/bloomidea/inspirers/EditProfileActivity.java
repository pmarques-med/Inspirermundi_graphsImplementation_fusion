package com.bloomidea.inspirers;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;*/
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.CountryListAdapter;
import com.bloomidea.inspirers.adapter.EditProfileAdapter;
import com.bloomidea.inspirers.adapter.LanguageListAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.events.MedicinesChangedEvent;
import com.bloomidea.inspirers.listener.ArrayListListener;
import com.bloomidea.inspirers.model.Country;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static com.bloomidea.inspirers.application.AppController.getmInstance;

public class EditProfileActivity extends MyActiveActivity {
    public static final String EXTRA_HIDE_BACK_BTN = "EXTRA_HIDE_BACK_BTN";

    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 300;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 400;

    public static final int REQUEST_IMAGE_CAPTURE_AVATAR = 1;
    public static final int RESULT_LOAD_IMAGE_AVATAR = 2;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditProfileAdapter editProfileAdapter;

    private boolean hideBackBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        hideBackBtn = getIntent().getBooleanExtra(EXTRA_HIDE_BACK_BTN,false);

        if(!hideBackBtn) {
            findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }else{
            findViewById(R.id.back_btn_imageView).setVisibility(View.GONE);
        }

        ((ImageView) findViewById(R.id.icon_imageView)).setImageResource(R.drawable.icon_edit_profile);
        ((TextView) findViewById(R.id.title_textView)).setText(R.string.edit_profile);

        recyclerView = (RecyclerView) findViewById(R.id.edit_profile_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        editProfileAdapter = new EditProfileAdapter(this, new EditProfileAdapter.InteractionListener() {
            @Override
            public void avatarClick() {
                loadImageFrom();
            }

            @Override
            public void countryBoxClick(int previousSelection) {
                EditProfileActivity.this.countryBoxClick(previousSelection);
            }

            /*
            @Override
            public void medicineDelete(final UserMedicine medicineToDelete) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                deleteMedicine(medicineToDelete);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.cancel();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setMessage(R.string.delete_medicine).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }

            @Override
            public void medicineEdit(UserMedicine medicineToEdit) {
                if(medicineToEdit instanceof UserNormalMedicine){
                    Intent i = new Intent(EditProfileActivity.this, AddMedicineActivity.class);
                    i.putExtra(AddMedicineActivity.EXTRA_MEDICINE_EDIT,medicineToEdit);

                    Utils.openIntent(EditProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
*/
            @Override
            public void languageBoxClick(boolean[] previousSelection) {
                EditProfileActivity.this.languageBoxClick(previousSelection);
            }

            @Override
            public void saveBtnClick(String name, String userProvidedId, String countryName, String countryFlagUrl, String countryIso, Bitmap newPicture, ArrayList<String> newLanguages, ArrayList<String> newHobbies) {
                if(name!=null && !name.isEmpty()) {
                    AppController.getmInstance().updateUserInfo(name,userProvidedId, countryName, countryFlagUrl, countryIso, newPicture,newLanguages,newHobbies);

                    if(hideBackBtn) {
                        finish();
                    }else{
                        onBackPressed();
                    }
                }else{
                    Toast.makeText(EditProfileActivity.this,R.string.name_empty,Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(editProfileAdapter);
    }

    private void deleteMedicine(UserMedicine medicineToDelete){
        if(AppController.getmInstance().deleteMedicine(medicineToDelete)){
            AppController.getmInstance().getMyBus().send(new MedicinesChangedEvent());

            editProfileAdapter.loadMedicineList();
            editProfileAdapter.notifyDataSetChanged();
        }
    }

    private void languageBoxClick(boolean[] previousSelection) {
        AlertDialog.Builder adb = new AlertDialog.Builder(EditProfileActivity.this);
        LayoutInflater inflater = EditProfileActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_country, null);
        adb.setView(dialogView);
        adb.setNegativeButton(getString(R.string.cancel), null);

        IndexFastScrollRecyclerView auxR = (IndexFastScrollRecyclerView) dialogView.findViewById(R.id.country_recycler);
        auxR.setIndexbarWidth(getResources().getDimension(R.dimen.index_bar_h));
        auxR.setLayoutManager(new LinearLayoutManager(EditProfileActivity.this));


        auxR.setAdapter(new LanguageListAdapter(AppController.getmInstance().getListLanguages(), previousSelection, EditProfileActivity.this, new LanguageListAdapter.LanguageListAdapterListener() {
            @Override
            public void languageSelectionChanged(int selectedLanguagePos, boolean selected) {
                editProfileAdapter.setLanguageSelected(selectedLanguagePos,selected);
            }
        }));

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editProfileAdapter.languagesEndEditing();
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = adb.create();
        alertDialog.show();
        /*AlertDialog.Builder adb = new AlertDialog.Builder(EditProfileActivity.this);

        adb.setMultiChoiceItems(AppController.getmInstance().getListLanguages(), previousSelection, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                editProfileAdapter.setLanguageSelected(i,b);
            }
        });

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editProfileAdapter.languagesEndEditing();
                dialogInterface.dismiss();
            }
        });

        adb.setNegativeButton("Cancel", null);

        adb.show();
        */
    }


    private void countryBoxClick(final int previousSelection){
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

        getmInstance().getCountryListAsArrayList(new ArrayListListener() {
            @Override
            public void onResponse(List response) {
                ringProgressDialogNoText.dismiss();

                final AlertDialog.Builder adb = new AlertDialog.Builder(EditProfileActivity.this);
                LayoutInflater inflater = EditProfileActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_country, null);
                adb.setView(dialogView);
                adb.setNegativeButton(getString(R.string.cancel), null);

                IndexFastScrollRecyclerView auxR = (IndexFastScrollRecyclerView) dialogView.findViewById(R.id.country_recycler);
                auxR.setIndexbarWidth(getResources().getDimension(R.dimen.index_bar_h));
                auxR.setLayoutManager(new LinearLayoutManager(EditProfileActivity.this));


                final AlertDialog alertDialog = adb.create();

                auxR.setAdapter(new CountryListAdapter((ArrayList<Country>) response, EditProfileActivity.this, new CountryListAdapter.CountryListAdapterListener() {
                    @Override
                    public void countrySelected(int selectedCountryPos) {
                        editProfileAdapter.setSelectedCountry(selectedCountryPos);
                        alertDialog.dismiss();
                    }
                }));

                /*adb.setSingleChoiceItems(new CountryListAdapter((ArrayList<Country>) response,EditProfileActivity.this), previousSelection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editProfileAdapter.setSelectedCountry(i);
                        dialogInterface.dismiss();
                    }
                });*/

                alertDialog.show();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
            }
        });
    }

    private void loadImageFrom(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.choose_img))
                .setItems(R.array.photo_souce, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                dispatchGalleryIntent();
                                break;
                        }


                    }
                });
        AlertDialog dialog = builder.show();
    }

    private void dispatchGalleryIntent() {
        if(checkPermissionsReadWrite()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

            startActivityForResult(intent, RESULT_LOAD_IMAGE_AVATAR);
        }
    }

    private boolean checkPermissionsReadWrite(){
        boolean permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!permissionCheck) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_WRITE);
        }

        return permissionCheck;
    }

    private boolean checkPermissionsCamera(){
        boolean permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==  PackageManager.PERMISSION_GRANTED;

        if(!permissionCheck) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        return permissionCheck;
    }

    private void dispatchTakePictureIntent() {
        if(checkPermissionsCamera()) {
            Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE_AVATAR);
        }
    }

    @Override
    public void onBackPressed() {
        if(!hideBackBtn) {
            super.onBackPressed();

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchGalleryIntent();
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        } else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(editProfileAdapter!=null) {
            editProfileAdapter.loadMedicineList();
            editProfileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE_AVATAR:
                    if(data!=null) {
                        Uri selectedImage = data.getData();
                        try {
                            Bitmap aux = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                            editProfileAdapter.setAvatarPicture(aux);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE_AVATAR:
                    Bitmap aux = (Bitmap) data.getExtras().get("data");

                    editProfileAdapter.setAvatarPicture(aux);
                    break;
                default:
                    break;
            }
        }
    }
}
