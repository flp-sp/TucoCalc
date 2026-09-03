package com.tucocalc;

public class BillManager {
    private static BillManager instance;
    private BillContext context;

    private BillManager() {
    }

    public static BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    public void setContext(BillContext context) {
        this.context = context;
    }

    public BillContext getContext() {
        return context;
    }

    public void reset() {
        context = null;
    }
}
