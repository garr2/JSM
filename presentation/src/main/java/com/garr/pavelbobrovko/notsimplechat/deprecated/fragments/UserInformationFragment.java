package com.garr.pavelbobrovko.notsimplechat.deprecated.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.garr.pavelbobrovko.notsimplechat.deprecated.AuthenticationModule;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.ImageOrientationChecker;
import com.garr.pavelbobrovko.notsimplechat.deprecated.adapters.ListAdapter;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInformationFragment extends Fragment implements View.OnClickListener,DataUpdator{


    private ImageView ivAvatar;
    private EditText etName, etAbout;
    private TextView tvNameError;
    private TextView tvNoFriends;
    private Button btnSave, btnCancel;
    private ImageButton ibGallery, ibCamera;


    private FirebaseUser mFirebaseUser;
    private Context mCtx;
    private View v = null;

    private StorageReference mStorageReference;
    private CoordinatingService coordinatingService;
    private User user;
    private MainActivity mActivity;

    private RecyclerView rvFriendList;

    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private long userDisplayID;
    private long displayedID= -2;
    //private long currentID ;
    private Long [] friendList;
    private String imageTile;
    private boolean isPhotoChange=false;
    private boolean isAccountDataCreate = false;
    private boolean isFragmentStart;
    private Uri photoUrlLocal;
   // private Uri photoUrl;
    private File directory;
    private boolean isETAboutFocused = false;
    private boolean isETNameFocused = false;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUES_CODE = 2;
    private final String KEY_IS_ET_ABOUT_FOCUSED = "isETAboutFocusedKey";
    private final String KEY_IS_ET_NAME_FOCUSED = "isETNameFocusedKey";
    private final String USER_KEY = "user_key";

    public UserInformationFragment(){
        this.setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity){
        Log.d(ConstantInterface.LOG_TAG,"OnAttach");
        getObjects(activity);
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context){
        getObjects(context);
        super.onAttach(context);
    }

    @Override
    public void onStop (){
        Log.d(ConstantInterface.LOG_TAG,"UserInformationFragment.onStop");
        isFragmentStart = false;

        isETNameFocused = etName.isFocused();
        isETAboutFocused = etAbout.isFocused();
        clearFocus();
        super.onStop();
    }

    @Override
    public void onStart (){
        Log.d(ConstantInterface.LOG_TAG,"UserInformationFragment.onStart");
        super.onStart();
        isFragmentStart = true;
        if (photoUrlLocal==null) {

        }

        if (isETAboutFocused)etAbout.setFocusable(isETAboutFocused);
    }

    private void getObjects(Context context){
        mCtx=context;
        mActivity=(MainActivity) mCtx;

        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if (Build.VERSION.SDK_INT>=23)
            mActivity.checkPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceSaved){

        v = inflater.inflate(R.layout.edit_user_information_fragment,parent,false);


        //about=mFirebaseUser.getDisplayName();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        this.setRetainInstance(true);

        if (savedInstanceState!=null){
            Log.d(ConstantInterface.LOG_TAG,"savedInstanceState is not null");
            isETNameFocused = savedInstanceState.getBoolean(KEY_IS_ET_NAME_FOCUSED);
            isETAboutFocused = savedInstanceState.getBoolean(KEY_IS_ET_ABOUT_FOCUSED);
            setIdToDisplay(savedInstanceState.getLong(ConstantInterface.BUNDLE_KEY));
            user = savedInstanceState.getParcelable(USER_KEY);
        }else setIdToDisplay(getArguments().getLong(ConstantInterface.BUNDLE_KEY));

        rvFriendList = v.findViewById(R.id.rvFriendList);

        ivAvatar=v.findViewById(R.id.ivEditAvatar);
        etName=v.findViewById(R.id.etEditName);
        etAbout=v.findViewById(R.id.etAboutUser);
        tvNameError=v.findViewById(R.id.tvNameError);
        tvNoFriends = v.findViewById(R.id.tvNoFriends);

        btnSave= v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnCancel=v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        ibGallery=v.findViewById(R.id.ibGallery);
        ibGallery.setOnClickListener(this);
        ibCamera=v.findViewById(R.id.ibCamera);
        ibCamera.setOnClickListener(this);

        if (coordinatingService ==null) coordinatingService =mActivity.getBindedService();
        getDisplayId();
        friendList = coordinatingService.getUserFriends();
        displayUserInfo();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_ET_NAME_FOCUSED,isETNameFocused);
        outState.putBoolean(KEY_IS_ET_ABOUT_FOCUSED, isETAboutFocused);
        outState.putLong(ConstantInterface.BUNDLE_KEY,displayedID);
        outState.putParcelable(USER_KEY,user);
    }

    private void clearFocus(){
        etName.clearFocus();
        etAbout.clearFocus();
    }

    private void displayUserInfo(){
        Log.d(ConstantInterface.LOG_TAG,"displayyUserInfo id = " + displayedID + " displayId = " + userDisplayID);
        if (userDisplayID == -1)getDisplayId();
        if (-1 == displayedID || displayedID == userDisplayID){
            setButtonsVisibility(View.VISIBLE);
            setFieldsEditable(true);
        }else {
            setButtonsVisibility(View.GONE);
            setFieldsEditable(false);
        }

        if (user == null){
            getUserInfo(displayedID, new OnDBReadCompleteListener<User>() {
                @Override
                public void onComplete(User type) {
                    user = type;
                    setDisplayUserInfo();
                }

                @Override
                public void onFailtrue(Exception e) {

                }
            });
        }else setDisplayUserInfo();

        if (friendList!= null && friendList.length>0) {
            tvNoFriends.setVisibility(View.GONE);
            setAdapter();
        } else {
            tvNoFriends.setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter(){
        mLayoutManager = new LinearLayoutManager(mCtx);
        rvFriendList.setLayoutManager(mLayoutManager);
        listAdapter=new ListAdapter(friendList,mActivity,ConstantInterface.USER_LIST_TYPE);
        rvFriendList.setAdapter(listAdapter);
    }

    private void setIdToDisplay(long _id){
        Log.d(ConstantInterface.LOG_TAG,"UserInformationFragment.setIDToDisplay = " + _id +
                " isFragmentStart = " + isFragmentStart);

       displayedID = _id;
       //if (isFragmentStart)displayUserInfo();
    }

    private void getDisplayId(){
        if (coordinatingService ==null) coordinatingService =mActivity.getBindedService();
        coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
            @Override
            public void onComplete(Long type) {
                if (type!=null) {
                    Log.d(ConstantInterface.LOG_TAG,"getDisplayedId displayId = " + userDisplayID);
                    userDisplayID = type;
                } else {
                    getDisplayId();
                }
                /**/
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });
    }

    private void getUserInfo(Long _id, OnDBReadCompleteListener<User> listener){
        coordinatingService.getUserInformation(_id, listener);

    }

    private void setDisplayUserInfo(){
        if (user!=null && user.getDisplayName()!=null) {
            isAccountDataCreate =true;
        }

        if (user.getAvatarURL()!=null){
            //coordinatingService.setImage(photoUrl.toString(),ivAvatar);
            Glide.with(mCtx)
                    .load(user.getAvatarURL())
                    .apply(new RequestOptions().error(R.drawable.no_image).circleCrop().centerInside())
                    .into(ivAvatar);
        }

        //Log.d(ConstantInterface.LOG_TAG,"Display name is: " + displayName + " ID: "
        //+ userDisplayID + "\n photo URL is: " + photoUrl);

        if (user.getDisplayName()!=null)etName.setText(user.getDisplayName());

        if (user.getAbout()!=null)etAbout.setText(user.getAbout());
    }

    private void setButtonsVisibility(int visibility){
        if (visibility == View.VISIBLE) {
            btnCancel.setText("Cancel");
            btnSave.setText("Save");
        } else {
            btnCancel.setText("Собщение");
            btnSave.setText("В друзья");
        }
        ibCamera.setVisibility(visibility);
        ibGallery.setVisibility(visibility);
    }

    private void setFieldsEditable(boolean editable){
        etAbout.setFocusable(editable);
        etAbout.setClickable(editable);
        etAbout.setFocusableInTouchMode(editable);

        etName.setFocusable(editable);
        etName.setClickable(editable);
        etName.setFocusableInTouchMode(editable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                if (!TextUtils.isEmpty(etName.getText().toString())
                        && !TextUtils.equals(etName.getText().toString(),"null")){

                    if (isPhotoChange){
                        Log.d(ConstantInterface.LOG_TAG,"Photo change");

                        coordinatingService.uploadAvatarAndChangeUser(isAccountDataCreate,photoUrlLocal,imageTile, userDisplayID,
                                etName.getText().toString(),etAbout.getText().toString()
                                ,mFirebaseUser.getPhotoUrl());

                        btnCancel.setClickable(false);
                        btnSave.setClickable(false);
                    }else {
                        Log.d(ConstantInterface.LOG_TAG,"Photo no change");

                        coordinatingService.changeUserInformation(isAccountDataCreate, userDisplayID,etName.getText().toString()
                                ,etAbout.getText().toString(), Uri.parse(user.getAvatarURL()));

                    }

                       /*if (commandListener.getAccountCreated()&&commandListener.getLoginIn()){
                           commandListener.setResultAndFinish(RESULT_OK);
                       }else {
                           commandListener.setResultAndFinish(RESULT_CANCELED);
                       }*/
                }else if (TextUtils.isEmpty(etName.getText().toString())){
                    tvNameError.setText(getString(R.string.empty_field));
                    tvNameError.setVisibility(View.VISIBLE);
                }else if (TextUtils.equals(etName.getText().toString(),"null")){
                    tvNameError.setText(getString(R.string.illegal_name));
                    tvNameError.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnCancel:
                if (coordinatingService.getDisplayName()==null){
                    AuthenticationModule.getInstance().signOut();
                    mActivity.finish();
                }else mActivity.setCurrentFragment();
                break;
            case R.id.ibGallery:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                break;
            case R.id.ibCamera:
                Log.d(ConstantInterface.LOG_TAG,"btnCamera");
                openCamera();
                break;
        }

    }

    public void getUserInformation(String _eMail,@Nullable final OnDBReadCompleteListener listener){
        if(user==null)user=new User();
        Log.d(ConstantInterface.LOG_TAG,"getUserInformation");
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference();
        chatReference.child(ConstantInterface.USER_INFORMATION).child(_eMail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user=dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Log.d(ConstantInterface.LOG_TAG,user.getAbout() + user.getDisplayName() + user.getAvatarURL());
                        }else {
                            Log.d(ConstantInterface.LOG_TAG,"user is null");
                        }

                        if (listener!=null){
                            if (user!=null) {
                                listener.onComplete(user);
                            }else listener.onFailtrue(new Exception("Data no found"));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener!=null)listener.onFailtrue(databaseError.toException());
                        Log.d(ConstantInterface.LOG_TAG,"Database ERROR " + databaseError);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode== AppCompatActivity.RESULT_OK) {
            Log.d(ConstantInterface.LOG_TAG,"resultCode " + requestCode);
            if (requestCode == GALLERY_REQUEST_CODE) {
                photoUrlLocal = intent.getData();

            } else if (requestCode == CAMERA_REQUES_CODE) {
                Log.d(ConstantInterface.LOG_TAG,"Camera is return, Uri: " + photoUrlLocal );
                ImageOrientationChecker ioc=new ImageOrientationChecker();
                ioc.checkImage(photoUrlLocal,mCtx);
            }
            ivAvatar.setImageURI(photoUrlLocal);
            isPhotoChange=true;
        } else {
            Log.d(ConstantInterface.LOG_TAG, "Activity is canceled");
            isPhotoChange = false;
        }
    }

    private void openCamera(){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(mCtx.getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(ConstantInterface.LOG_TAG,"ERROR " + ex);
                // Error occurred while creating the File
            }

            if (photoFile!=null){
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    photoUrlLocal = FileProvider.getUriForFile(mCtx,
                            mCtx.getApplicationContext().getPackageName() + ".provider",
                            photoFile);
                }else {
                    photoUrlLocal=Uri.fromFile(photoFile);
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUrlLocal);
                //cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION)

                startActivityForResult(cameraIntent, CAMERA_REQUES_CODE);
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageTile = "photo_" + timeStamp;
        File storageDir = mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image= File.createTempFile(
                imageTile,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //image= new File(storageDir + "/" + imageTile + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        photoUrlLocal=Uri.fromFile(image);
        return image;
    }

    private void deleteUser() {
        mFirebaseUser.delete().addOnCompleteListener(getActivity(),
                new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(ConstantInterface.LOG_TAG, "User account deleted.");
                    }
                });
    }

    private void setUserData(){

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getDisplayName())
                .setPhotoUri(Uri.parse(user.getAvatarURL()))
                .build();

        mFirebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mCtx,"User profile updated.",Toast.LENGTH_SHORT).show();
                            Log.d(ConstantInterface.LOG_TAG, "User profile updated.");
                            //commandListener.setResultAndFinish(RESULT_OK);
                        }
                    }
                });
    }

    protected void loadToFirebase(){
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mStorageReference=mStorageReference.child(ConstantInterface.AVATAR_IMAGE_FOLDER + imageTile + ".jpg");
        mStorageReference.putFile(photoUrlLocal).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                user.setAvatarURL(String.valueOf(taskSnapshot.getUploadSessionUri()));
                Log.d(ConstantInterface.LOG_TAG,"Image loaded URL is: " + user.getAvatarURL());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mCtx,"Image update failed",Toast.LENGTH_SHORT).show();
                Log.d(ConstantInterface.LOG_TAG,"Image update failed  " + e);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //setUserData();
            }
        });
    }

    @Override
    public void updateAndDisplayNewData(@Nullable long arg) {
    }
}

