package com.mauriciogiordano.reddit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mauriciogiordano.reddit.database.User;
import com.mauriciogiordano.reddit.network.Network;


public class RedditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit);

        User.signIn("mauriciogior", "6uwgqj9w", getApplicationContext(), new User.OnLoginListener()
        {
            @Override
            public void onLogin(User user, Network.Status status, boolean err)
            {
                if(!err)
                {
                    Log.d("MODHASH", user.getModhash());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reddit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
