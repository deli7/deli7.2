package com.delivame.delivame.deliveryman.activities.common.Messaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.PublicMessage;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.SupportMessage;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.BUNDLE_PARAMS;

public class ViewSupportMessageActivity extends BaseActivity implements InitManager.InitManagerListener {

    private String messageId;
    @BindView(R.id.textViewFrom)
    TextView textViewFrom;
    @BindView(R.id.textViewDate)
    TextView textViewDate;
    @BindView(R.id.textViewMessageBody)
    TextView textViewMessageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_support_message);
        ButterKnife.bind(this);

        getExtras();

        new InitManager().init(ViewSupportMessageActivity.this);
    }

    boolean isPublic = false;

    private void getExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra(BUNDLE_PARAMS);
            if(bundle != null){
                isPublic = bundle.getBoolean("IS_PUBLIC", false);
                messageId = bundle.getString("MESSAGE_ID");
            }else {
                messageId = intent.getStringExtra(Constants.MESSAGE_ID);
            }
        }
    }

    public static void startMe(Context context, String id) {
        Intent intent = new Intent(context, ViewSupportMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.MESSAGE_ID, id);
        context.startActivity(intent);
    }

    private void initUI() {

        initUI(getString(R.string.ui_activity_title_message_details), true);

        loadMessage();

    }

    private void loadMessage() {
        if (!isPublic) {
            MyUtility.getUserSupportMessagesNodeRef().child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SupportMessage supportMessage = dataSnapshot.getValue(SupportMessage.class);
                    textViewFrom.setText(supportMessage.getSenderName());
                    textViewDate.setText(supportMessage.getDate());
                    textViewMessageBody.setText(supportMessage.getMessage());

                    if (!supportMessage.getRead()) {
                        supportMessage.setRead(true);
                        supportMessage.saveMessage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            MyUtility.getPublicMessagesRef().child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    PublicMessage publicMessage = dataSnapshot.getValue(PublicMessage.class);
                    textViewFrom.setText(publicMessage.getSubject());
                    textViewDate.setText(publicMessage.getId());
                    textViewMessageBody.setText(publicMessage.getBody());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void initUI(Settings settings, User currentUser) {
        this.settings = settings;
        this.currentUser = currentUser;
        initUI();
    }
}
