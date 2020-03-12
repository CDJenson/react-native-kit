package com.coship.rnkit.update.hotupdate;


/**
 *  author: zoujunda
 *  date: 2019/6/19 18:16
 *	version: 1.0
 *  description: hot update upgrade strategy
 */
public enum HotUpdateInstallMode {

    IMMEDIATE(0), //No need to restart the program, effective immediately
    ON_NEXT_LAUNCH(1);//Effective after restarting the program

    private final int value;

    HotUpdateInstallMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static HotUpdateInstallMode getHotUpdateInstallMode(int value) {
        for (HotUpdateInstallMode type : HotUpdateInstallMode.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return HotUpdateInstallMode.ON_NEXT_LAUNCH;
    }
}
