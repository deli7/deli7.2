package com.delivame.delivame.deliveryman.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.IntroWizardActivity;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.SharedPrefHelper;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class UserSettingsFragment extends Fragment {

    private static final int TONE_PICKER = 201;
    @BindView(R.id.switchNotifications)
    Switch switchNotifications;
    @BindView(R.id.buttonLogout)
    Button buttonLogout;
    Unbinder unbinder;
    @BindView(R.id.buttonSelect)
    Button buttonSelect;
    @BindView(R.id.textViewRingtone)
    TextView textViewRingtone;
    @BindView(R.id.linearLayoutSwitchNotifications)
    LinearLayout linearLayoutSwitchNotifications;

    private OnFragmentInteractionListener mListener;

    public UserSettingsFragment() {
        // Required empty public constructor
    }


    public static UserSettingsFragment newInstance() {
        return new UserSettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        unbinder = ButterKnife.bind(this, view);


        Ringtone ringtone;
        Uri uri = SharedPrefHelper.getPrefUri(getContext(), Constants.PREF_SETTING_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getContext(), uri);
        if (ringtone != null) {
            textViewRingtone.setText(ringtone.getTitle(getContext()));
        }

        boolean newOrdersNotifications = SharedPrefHelper.getPrefBoolean(getContext(),
                Constants.NEW_ORDERS_NOTIFICATIONS, true);

        switchNotifications.setChecked(newOrdersNotifications);

        switchNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> SharedPrefHelper.writePrefBoolean(getContext(),
                        Constants.NEW_ORDERS_NOTIFICATIONS, isChecked));

        if (!mListener.getCurrentUser().isDeliveryMan()) {
            linearLayoutSwitchNotifications.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.buttonLogout)
    public void onClick() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    FirebaseAuth.getInstance().signOut();
                    IntroWizardActivity.startMe(getContext());
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),
                R.style.AppCompatAlertDialogStyle));
        builder.setMessage(getString(R.string.ui_dialog_are_you_sure_exit))
                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener)
                .show();
    }

    @OnClick(R.id.buttonSelect)
    public void onButtonSelectClick() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);

        Uri uri = SharedPrefHelper.getPrefUri(getContext(), Constants.PREF_SETTING_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);

        startActivityForResult(intent, TONE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TONE_PICKER) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    SharedPrefHelper.writePrefUri(getContext(), Constants.PREF_SETTING_RINGTONE, uri);

                    Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
                    if (ringtone != null) {
                        textViewRingtone.setText(ringtone.getTitle(getContext()));

                        // NotificationsHelper.showNotification(getContext(), "Title", "Snippet", R.drawable.neworder, DeliveryManHomeActivity.class);
                    }
                }

            }
        }
    }


    public interface OnFragmentInteractionListener {
        User getCurrentUser();
    }
}