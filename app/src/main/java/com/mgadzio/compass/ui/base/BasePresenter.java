
package com.mgadzio.compass.ui.base;

public abstract class BasePresenter<T> {

    private T view = null;

    public void bindView(T view) {
        this.view = view;
    }

    protected T getView() {

        if (view == null) {
            throw new IllegalStateException("Initialize presenter first");
        }

        return view;
    }


    public abstract void onPause();

    public abstract void onResume();
}
