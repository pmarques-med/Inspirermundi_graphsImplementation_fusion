package com.bloomidea.inspirers.listener;

/**
 * Created by michellobato on 21/04/17.
 */

public interface InternalBroadcastListener {
    void userMessage(String uid, String msg, String messageNotif);
    void medicineToTake();
    void removedGodfather(String messageNotif);
    void requestGodchild(String messageNotif);
    void acceptedGodchild(String messageNotif);
    void buzzRecived(String messageNotif);
}
