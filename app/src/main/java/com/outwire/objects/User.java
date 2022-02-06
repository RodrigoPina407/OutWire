package com.outwire.objects;


public class User{

    private String displayName = null;
    private String userName = null;
    private String userEmail = null;
    private String userPhoneNumber = null;
    private String userGender = null;
    private String userLanguage = null;
    private String photoUri = null;
    private boolean isWithEmailLogin = false;
    private boolean isWithGoogleLogin = false;
    private boolean isWithFacebookLogin = false;

    private long timeOfRegistration = 0;

    public User() {}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public boolean isWithEmailLogin() {
        return isWithEmailLogin;
    }

    public void setWithEmailLogin(boolean withEmailLogin) {
        isWithEmailLogin = withEmailLogin;
    }

    public boolean isWithGoogleLogin() {
        return isWithGoogleLogin;
    }

    public void setWithGoogleLogin(boolean withGoogleLogin) {
        isWithGoogleLogin = withGoogleLogin;
    }

    public boolean isWithFacebookLogin() {
        return isWithFacebookLogin;
    }

    public void setWithFacebookLogin(boolean withFacebookLogin) {
        isWithFacebookLogin = withFacebookLogin;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public long getTimeOfRegistration() {
        return timeOfRegistration;
    }

    public void setTimeOfRegistration(long timeOfRegistration) {
        this.timeOfRegistration = timeOfRegistration;
    }

}
