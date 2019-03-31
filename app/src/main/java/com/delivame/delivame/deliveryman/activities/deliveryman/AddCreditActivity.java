package com.delivame.delivame.deliveryman.activities.deliveryman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.CreditRequest;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCreditActivity extends BaseActivity implements InitManager.InitManagerListener {

    @BindView(R.id.vodafone)
    ImageView vodafone;
    @BindView(R.id.instructions)
    TextView instructions;
    @BindView(R.id.instructions2)
    TextView instructions2;
    @BindView(R.id.textInputLayoutFullname)
    TextInputLayout textInputLayoutFullname;
    @BindView(R.id.textInputLayoutPhone)
    TextInputLayout textInputLayoutPhone;
    @BindView(R.id.textInputLayoutCredit)
    TextInputLayout textInputLayoutCredit;
    @BindView(R.id.buttonSubmit)
    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit);
        ButterKnife.bind(this);

        new InitManager().init(AddCreditActivity.this);
    }

    @Override
    public void initUI(Settings settings, User currentUser) {
        this.settings = settings;
        this.currentUser = currentUser;

        initUI();
    }

    private void initUI() {
        initUI(getString(R.string.ui_activity_title_add_credit), true);

        instructions2.setText(settings.getChargeInstructions());
    }

    public static void startMe(Context context) {
        Intent intent = new Intent(context, AddCreditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @OnClick(R.id.buttonSubmit)
    public void onViewClicked() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        CreditRequest creditRequest = new CreditRequest();
                        creditRequest.setDate(DateTimeUtil.getCurrentDateTime());
                        creditRequest.setCredit(Double.parseDouble(textInputLayoutCredit.getEditText().getText().toString()));
                        creditRequest.setFullName(textInputLayoutFullname.getEditText().getText().toString());
                        creditRequest.setPhoneNumber(textInputLayoutPhone.getEditText().getText().toString());
                        creditRequest.setRequestType(CreditRequest.CREDIT_REQ_TYPE_ADD_CREDIT);
                        creditRequest.setRequestStatus(CreditRequest.CREDIT_REQ_STATUS_NEW);

                        MyUtility.getAddCreditRequestsRef().child(creditRequest.getDate()).setValue(creditRequest);
                        Toast.makeText(AddCreditActivity.this, getString(R.string.ui_notification_add_credit_request_added), Toast.LENGTH_SHORT).show();

                        finish();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddCreditActivity.this, R.style.AppCompatAlertDialogStyle));
        builder.setMessage(getString(R.string.ui_dialog_are_you_sure_add_credit_request)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();




    }
}
