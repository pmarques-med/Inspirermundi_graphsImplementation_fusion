package com.bloomidea.inspirers.model;

/**
 * Created by michellobato on 17/11/2017.
 */

public class UserMedicineAuxSync {
    private UserMedicine userMedicine;
    private boolean deleted;

    public UserMedicineAuxSync(UserMedicine userMedicine, boolean deleted) {
        this.userMedicine = userMedicine;
        this.deleted = deleted;
    }

    public UserMedicine getUserMedicine() {
        return userMedicine;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
