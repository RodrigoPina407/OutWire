package com.outwire.util;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public abstract class FirebaseStorageHelper {

    private Uri downloadUri;
    private static InputStream mStream;


    public static String getFilenameFromUri(String s){

        File f = new File(s);
        return f.getName();
    }

    public static InputStream getStream() {
        return mStream;
    }

    public static void setStream(Context context, Uri uri) {
        mStream = null;
        try {
            if(uri != null)
                mStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void executeOnSuccess() {
    }

    public void executeOnFailure(){}

    public void uploadFile(@NotNull Context context, InputStream stream, String filename, String errorMsg) {

        UploadTask uploadTask = null;

        if (stream != null) {

            StorageReference ref = FirebaseHelper.getUserMediaReference().child(filename);


            uploadTask = ref.putStream(stream);

            uploadTask.addOnFailureListener(exception -> {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                executeOnFailure();

            }).addOnSuccessListener(taskSnapshot -> {

                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUri = uri;
                    executeOnSuccess();
                });

            });
        }
        else
            executeOnFailure();
    }

    public Uri getDownloadUri() {
        return downloadUri;
    }
}
