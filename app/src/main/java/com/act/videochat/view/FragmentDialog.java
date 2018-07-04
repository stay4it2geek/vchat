package com.act.videochat.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.videochat.R;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentDialog extends DialogFragment {

    /**
     * 显示的标题
     */
    private TextView titleTv;

    /**
     * 显示的消息
     */
    private TextView messageTv;

    /**
     * 确认和取消按钮
     */
    private Button negtiveBn, positiveBn;

    /**
     * 按钮之间的分割线
     */
    private View columnLineView;

    /**
     * 内容数据
     */
    private String message;
    private String title;
    private String positive, negtive;

    /**
     * 底部是否只有一个按钮
     */
    private boolean isSingle = false;

    private Dialog dialog;


    private void initView(View rootView) {
        Bundle args = getArguments();
        if (args == null)
            return;
        title = args.getString("title");
        positive = args.getString("positive");
        negtive = args.getString("negtive");
        message = args.getString("message");
        isSingle = args.getBoolean("isSingle");
        negtiveBn = (Button) rootView.findViewById(R.id.negtive);
        positiveBn = (Button) rootView.findViewById(R.id.positive);
        titleTv = (TextView) rootView.findViewById(R.id.title);
        messageTv = (TextView) rootView.findViewById(R.id.message);
        columnLineView = rootView.findViewById(R.id.column_line);
    }

    public static final FragmentDialog newInstance(boolean needShowDelete, String title, String message, String positive, String negtive, String imageUrl, String creatTime, boolean isSingle, OnClickBottomListener onClickBottomListener) {
        FragmentDialog fragment = new FragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        bundle.putString("positive", positive);
        bundle.putString("negtive", negtive);
        bundle.putBoolean("isSingle", isSingle);
        fragment.onClickBottomListener = onClickBottomListener;
        fragment.setArguments(bundle);
        return fragment;
    }


    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick(dialog);
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negtiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegtiveClick(dialog);
                }
            }
        });

    }

    /**
     * 初始化界面控件的显示数据
     */
    private void refreshView() {
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        } else {
            titleTv.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(message)) {
            messageTv.setText(message);
        }


        if (!TextUtils.isEmpty(positive)) {
            positiveBn.setText(positive);
        } else {
            positiveBn.setText("确定");
        }
        if (!TextUtils.isEmpty(negtive)) {
            negtiveBn.setText(negtive);
        } else {
            negtiveBn.setText("取消");
        }

        if (isSingle) {
            columnLineView.setVisibility(View.GONE);
            negtiveBn.setVisibility(View.GONE);
        } else {
            negtiveBn.setVisibility(View.VISIBLE);
            columnLineView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;


    public interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        public void onPositiveClick(Dialog dialog);

        /**
         * 点击取消按钮事件
         */
        public void onNegtiveClick(Dialog dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.common_dialog_layout, null, false);
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        initDialogStyle(rootView);
        initView(rootView);
        initEvent();
        return dialog;

    }

    private void initDialogStyle(View view) {
        dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 紧贴底部
        window.setAttributes(lp);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

}
