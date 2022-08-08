package com.george.unsplash.localdata.preferences;

import com.george.unsplash.network.models.user.Me;

public interface PreferencesBehaviour {

    void saveToken(String token);

    void saveTokenType(String tokenType);

    void saveScope(String scope);

    void saveMe(Me me);

    void saveProfileImage(String image);

    String getToken();

    String getTokenType();

    String getScope();

    Me getMe();

    String getProfileImage();

    void logout();

}
