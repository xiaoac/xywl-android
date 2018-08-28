package com.nandasoftits.xingyi.view;

import android.app.Dialog;
import android.content.Context;
import android.opengl.Visibility;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.nandasoftits.xingyi.R;

public class ImgSelectPicker implements View.OnClickListener {

    private Dialog mImgSelectDialog;

    private View mTakePhotosBtn;

    private View mSelectPhotosBtn;

    private View mCancelBtn;

    private Context mContext;

    private ImgSelectPickerCallback mImgSelectPickerCallback;

    private View mTackPhotoPanel;
    private View mPickPhotoPanel;

    public static final int TACK_PHOTO = 1 << 1;
    public static final int PICK_PHOTO = 1 << 2;

    private String mImgTarget;

    private String token;

    private String serviceUrl;

    public ImgSelectPicker(Context context) {
        mContext = context;

        initDialog();
        initView();
    }

    private void initView() {
        mTakePhotosBtn = mImgSelectDialog.findViewById(R.id.take_photos);
        mSelectPhotosBtn = mImgSelectDialog.findViewById(R.id.select_photos);
        mCancelBtn = mImgSelectDialog.findViewById(R.id.cancel_btn);

        mTackPhotoPanel = mImgSelectDialog.findViewById(R.id.take_photos_panel);
        mPickPhotoPanel = mImgSelectDialog.findViewById(R.id.select_photos_panel);

        mCancelBtn.setOnClickListener(this);
        mSelectPhotosBtn.setOnClickListener(this);
        mTakePhotosBtn.setOnClickListener(this);
    }

    private void initDialog() {
        if (mImgSelectDialog == null) {
            mImgSelectDialog = new Dialog(mContext, R.style.img_select_dialog);
            mImgSelectDialog.setCancelable(false);
            mImgSelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mImgSelectDialog.setContentView(R.layout.img_select_picker);
            Window window = mImgSelectDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    public void show(int model) {
        mTackPhotoPanel.setVisibility((model & TACK_PHOTO) != 0 ? View.VISIBLE : View.GONE);
        mPickPhotoPanel.setVisibility((model & PICK_PHOTO) != 0 ? View.VISIBLE : View.GONE);
        mImgSelectDialog.show();
    }

    private void cancel() {
        mImgSelectDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.cancel_btn:
                cancel();
                break;
            case R.id.take_photos:
                mImgSelectPickerCallback.doSelect(mImgTarget, TACK_PHOTO);
                cancel();
                break;
            case R.id.select_photos:
                mImgSelectPickerCallback.doSelect(mImgTarget, PICK_PHOTO);
                cancel();
                break;
            default:
        }
    }

    public void setImgTarget(String imgTarget) {
        mImgTarget = imgTarget;
    }

    public String getImgTarget() {
        return mImgTarget;
    }

    public void setImgSelectPickCallback(ImgSelectPickerCallback imgSelectPickCallback) {
        mImgSelectPickerCallback = imgSelectPickCallback;
    }

    public interface ImgSelectPickerCallback {
        public void doSelect(String imgTag, int select);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
