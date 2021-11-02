package com.siemens.datalayer.iot.util;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

// get access_token use OAuth2.0
public class OltuJavaClient {

    // get access_token use OAuth2.0 CLIENT_CREDENTIALS
    public static String getAccessTokenUseClientCredentials(String accessTokenUrl,String clientId,String clientSecret)
    {
        String accessToken = null;
        OAuthClient client = new OAuthClient(new URLConnectionClient());

        try {
            OAuthClientRequest request =
                    OAuthClientRequest.tokenLocation(accessTokenUrl)
                    .setGrantType(GrantType.CLIENT_CREDENTIALS)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .buildBodyMessage();

            accessToken = client.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class).getAccessToken();

        } catch (OAuthSystemException | OAuthProblemException e) {
            e.printStackTrace();
        }

        return accessToken;
    }
}