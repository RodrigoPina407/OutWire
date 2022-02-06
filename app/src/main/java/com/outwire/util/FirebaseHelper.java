package com.outwire.util;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FirebaseHelper {


    public static FirebaseUser getFirebaseUser() {
        return getAuth().getCurrentUser();
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getUserReference() {
        return getUserCollection().document(getFirebaseUser().getUid());
    }

    public static CollectionReference getUserEventsCollection(){
        return getUserReference().collection("events");
    }

    private static FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    public static StorageReference getUserStorageReference() {
        return getFirebaseStorage().getReference().child(getFirebaseUser().getUid());
    }

    public static StorageReference getUserMediaReference() {
        return getUserStorageReference().child("media");
    }


    public static void deleteUserFromFirebase() {

        FirebaseHelper.getUserReference().delete();
        FirebaseHelper.getFirebaseUser().delete();
    }

}

