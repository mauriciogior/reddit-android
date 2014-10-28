package com.mauriciogiordano.reddit.network;

/**
 * Created by mauricio on 10/27/14.
 */
public class Endpoints
{
    public static class User
    {
        private static final String beginsWith = "api/";

        public static final String login = beginsWith + "login";
        public static final String me = beginsWith + "me.json";
    }
}
