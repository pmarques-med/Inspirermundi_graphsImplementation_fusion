package com.bloomidea.inspirers.listener;

/**
 * Created by michellobato on 04/04/17.
 */

public interface OkErrorPollListener {
    void ok(long pollId);
    void error();
}
