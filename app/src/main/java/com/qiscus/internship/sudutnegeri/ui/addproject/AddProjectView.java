package com.qiscus.internship.sudutnegeri.ui.addproject;

/**
 * Created by Vizyan on 1/22/2018.
 */

interface AddProjectView {
    String getProjectName();

    String getLocation();

    String getTarget();

    String getInformation();

    String getPhoto();

    int getTargetFunds();

    void successPostProject();

    void failed(String s);
}
