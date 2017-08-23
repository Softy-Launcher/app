package com;

import android.os.AsyncTask;

/**
 * Created by softy on 7/5/17.
 */

public class Sync implements SyncTask {

    private SyncTask mSync;

    void setSyncTask(SyncTask mTask)
    {
        this.mSync = mTask;
    }

    @Override
    public void syncVoid(Object result) {

    }

    @Override
    public void onPostSync(Object result) {

    }

    @Override
    public void post() {
        AsyncTask mTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mSync.syncVoid(objects);
                return objects;
            }

            @Override
            public void onPostExecute(Object result)
            {
                mSync.onPostSync(result);
            }
        };
        mTask.execute();
    }
}
