package com.example.userasus.http_uceva.datos;

import android.provider.BaseColumns;

/**
 * Created by UserAsus on 11/10/2016.
 */
public class ContractFeed {


    public static abstract class FeedEnrty implements BaseColumns {

        public   static final  String TABLE_NAME="feed";

        public static final String FEED_ID="feed_id";

        public static final String FEED="feed";
    }


}
