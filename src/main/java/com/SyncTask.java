package com;

/**
 * Created by softy on 7/5/17.
 */


public interface SyncTask
{
    void syncVoid(Object result);
    void onPostSync(Object result);
    void post();
}