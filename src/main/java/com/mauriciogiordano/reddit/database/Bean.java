package com.mauriciogiordano.reddit.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by mauricio on 10/27/14.
 */
public class Bean<T> {

    protected Class<T> clazz;

    public Bean(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    protected Dao<T, Integer> getDao(Context context)
    {
        Dao<T, Integer> dao = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(clazz);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dao;
    }

    protected boolean save(Context context, T object)
    {
        Dao<T, Integer> dao = getDao(context);

        Dao.CreateOrUpdateStatus status;

        try {
            status = dao.createOrUpdate(object);

            return status.isCreated() || status.isUpdated();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
