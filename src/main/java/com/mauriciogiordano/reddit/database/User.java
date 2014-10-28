package com.mauriciogiordano.reddit.database;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.mauriciogiordano.reddit.network.Delegate;
import com.mauriciogiordano.reddit.network.Endpoints;
import com.mauriciogiordano.reddit.network.HttpClientHelper;
import com.mauriciogiordano.reddit.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Bean representation of the t2 (Account).
 */
public class User extends Bean
{
    //    GET /api/me.json
    //    {
    //        "kind":"t2",
    //        "data":{
    //                "has_mail":true,
    //                "name":"mauriciogior",
    //                "is_friend":false,
    //                "created":1408996244,
    //                "gold_creddits":0,
    //                "modhash":"9pogebx1mg961c7c5382ec2056322c8f46946c1f33462951d7",
    //                "created_utc":1408992644,
    //                "link_karma":1,
    //                "comment_karma":6,
    //                "over_18":false,
    //                "is_gold":false,
    //                "is_mod":false,
    //                "gold_expiration":null,
    //                "has_verified_email":true,
    //                "id":"i1nd1",
    //                "has_mod_mail":false
    //        }
    //    }

    public static final String KIND = "t2";

    @DatabaseField(generatedId = false, id = true)
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private boolean hasMail;
    @DatabaseField
    private boolean hasVerifiedEmail;
    @DatabaseField
    private boolean hasModMail;
    @DatabaseField
    private boolean over18;

    @DatabaseField
    private boolean isFriend;
    @DatabaseField
    private boolean isGold;
    @DatabaseField
    private boolean isMod;
    @DatabaseField
    private String goldExpiration;
    @DatabaseField
    private int goldCreddits;

    @DatabaseField
    private String commentKarma;
    @DatabaseField
    private String linkKarma;
    @DatabaseField
    private String modhash;

    @DatabaseField
    private long created;
    @DatabaseField
    private long createdUtc;

    public User()
    {
        super(User.class);
    }

    public User(JSONObject object)
    {
        super(User.class);

        id = object.optString("id", "");
        name = object.optString("name", "");
        hasMail = object.optBoolean("has_mail", false);
        hasVerifiedEmail = object.optBoolean("has_verified_email", false);
        hasModMail = object.optBoolean("has_mod_mail", false);
        over18 = object.optBoolean("over_18", false);
        isFriend = object.optBoolean("is_friend", false);
        isGold = object.optBoolean("is_gold", false);
        isMod = object.optBoolean("is_mod", false);
        goldExpiration = object.optString("gold_expiration", "");
        goldCreddits = object.optInt("gold_creddits", 0);
        commentKarma = object.optString("comment_karma", "");
        linkKarma = object.optString("link_karma", "");
        modhash = object.optString("modhash", "");
        created = object.optLong("created", 0);
        createdUtc = object.optLong("created_utc", 0);
    }

    /**
     * Creates or updates the current object.
     * @param context The current context of the application.
     * @return true if success, false if fail.
     */
    public boolean save(Context context)
    {
        return save(context, this);
    }

    /**
     * Sign in the user with specified parameters.
     * @param username The username.
     * @param password The password.
     * @param onLoginListener The callback.
     */
    public static void signIn(String username, String password, final Context context, final OnLoginListener onLoginListener)
    {
        HttpClientHelper client = new HttpClientHelper(Endpoints.User.login, context);

        client.addParamForPost("api_type", "json");
        client.addParamForPost("user", username);
        client.addParamForPost("passwd", password);
        client.addParamForPost("rem", String.valueOf(true));

        Network.newRequest(client, Network.POST, Network.WITHOUT_COOKIE, new Delegate()
        {
            @Override
            public void requestResults(Network.Status status)
            {
                User user = null;
                boolean err = false;

                if(status.hasInternet)
                {
                    if(status.response.getStatusLine().getStatusCode() == 200
                    || status.response.getStatusLine().getStatusCode() == 409)
                    {
                        try
                        {
                            JSONObject values = (JSONObject) ((JSONObject) status.result.get("json")).get("data");
                            String cookie = values.getString("cookie");

                            status.httpClientHelper.setCookie(cookie, context);

                            getMe(context, onLoginListener);
                        }
                        catch (JSONException e)
                        {
                            err = true;
                            onLoginListener.onLogin(user, status, err);
                        }
                    }
                    else
                    {
                        err = true;
                        onLoginListener.onLogin(user, status, err);
                    }
                }
                else
                {
                    onLoginListener.onLogin(user, status, err);
                }
            }
        });
    }

    /**
     * Retrieves the current user in session.
     * @param onLoginListener The callback.
     */
    public static void getMe(final Context context, final OnLoginListener onLoginListener)
    {
        HttpClientHelper client = new HttpClientHelper(Endpoints.User.me, context);

        Network.newRequest(client, Network.GET, Network.WITH_COOKIE, new Delegate()
        {
            @Override
            public void requestResults(Network.Status status)
            {
                User user = null;
                boolean err = false;

                if(status.hasInternet)
                {
                    if(status.response.getStatusLine().getStatusCode() == 200)
                    {
                        try
                        {
                            user = new User(status.result.getJSONObject("data"));
                        }
                        catch (JSONException e)
                        {
                            err = true;
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        err = true;
                    }
                }

                onLoginListener.onLogin(user, status, err);
            }
        });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasMail() {
        return hasMail;
    }

    public void setHasMail(boolean hasMail) {
        this.hasMail = hasMail;
    }

    public boolean isHasVerifiedEmail() {
        return hasVerifiedEmail;
    }

    public void setHasVerifiedEmail(boolean hasVerifiedEmail) {
        this.hasVerifiedEmail = hasVerifiedEmail;
    }

    public boolean isHasModMail() {
        return hasModMail;
    }

    public void setHasModMail(boolean hasModMail) {
        this.hasModMail = hasModMail;
    }

    public boolean isOver18() {
        return over18;
    }

    public void setOver18(boolean over18) {
        this.over18 = over18;
    }

    public boolean isGold() {
        return isGold;
    }

    public void setGold(boolean isGold) {
        this.isGold = isGold;
    }

    public boolean isMod() {
        return isMod;
    }

    public void setMod(boolean isMod) {
        this.isMod = isMod;
    }

    public String getGoldExpiration() {
        return goldExpiration;
    }

    public void setGoldExpiration(String goldExpiration) {
        this.goldExpiration = goldExpiration;
    }

    public int getGoldCreddits() {
        return goldCreddits;
    }

    public void setGoldCreddits(int goldCreddits) {
        this.goldCreddits = goldCreddits;
    }

    public String getCommentKarma() {
        return commentKarma;
    }

    public void setCommentKarma(String commentKarma) {
        this.commentKarma = commentKarma;
    }

    public String getLinkKarma() {
        return linkKarma;
    }

    public void setLinkKarma(String linkKarma) {
        this.linkKarma = linkKarma;
    }

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public static abstract class OnLoginListener
    {
        public abstract void onLogin(User user, Network.Status status, boolean err);
    }
}
