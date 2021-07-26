package org.medida.inhalerdetection;

public interface DetectionActivity {
    long getPatientId();
    long getActivityTimeStamp();
    void AdvanceToPostDetectionActivity(String imageName);
}
