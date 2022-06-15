package com.techsanelab.todo.util.communication;


import com.techsanelab.todo.util.IabResult;

public interface BillingSupportCommunication {
    void onBillingSupportResult(int response);
    void remoteExceptionHappened(IabResult result);
}
