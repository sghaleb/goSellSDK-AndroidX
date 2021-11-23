package company.tap.gosellapi.internal.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;

import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

import company.tap.gosellapi.R;
import company.tap.gosellapi.internal.Constants;
import company.tap.gosellapi.internal.activities.BaseActivity;
import company.tap.gosellapi.internal.activities.GoSellPaymentActivity;
import company.tap.gosellapi.internal.api.models.SavedCard;
import company.tap.gosellapi.internal.data_managers.PaymentDataManager;
import company.tap.gosellapi.internal.utils.PaymentsUtil;
import company.tap.gosellapi.internal.viewholders.GroupViewHolder;
import company.tap.gosellapi.open.data_manager.PaymentDataSource;
import company.tap.gosellapi.open.enums.TransactionMode;

/**
 * The type Recent payments recycler view adapter.
 */
public class RecentPaymentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * The interface Recent payments recycler view adapter listener.
     */
    public interface RecentPaymentsRecyclerViewAdapterListener {
        /**
         * Recent payment item clicked.
         *
         * @param position the position
         */
        void recentPaymentItemClicked(int position);

        /**
         * delete saved card
         * @param cardId
         */
        void deleteCard(@NonNull String cardId);
    }

    /**
     * Interface to be implemented by {@link company.tap.gosellapi.internal.viewholders.GroupViewHolder}
     * in order to change title from Cancel to Edit in case user stopped cards shaking
     */

    public interface RecentPaymentsRecyclerViewAdapterShakingListener {
        void changeGroupActionTitle();
    }

    private ArrayList<SavedCard> datasource;
    private RecyclerView parent;
    private int focusedPosition = Constants.NO_FOCUS;
    private RecentPaymentsRecyclerViewAdapterListener listener;
    private GroupViewHolder groupViewHolderListener;
    private static final int TYPE_GOOGLE_PAY = 0;
    private static final int TYPE_SAVED_CARD = 1;
    private View view;
    private int totalItemCount = 0;
    private ArrayList<Object> modifiedDatasource = new ArrayList<Object>();

    /**
     * Instantiates a new Recent payments recycler view adapter.
     *
     * @param datasource the datasource
     * @param listener   the listener
     */
    public RecentPaymentsRecyclerViewAdapter(ArrayList<SavedCard> datasource, RecentPaymentsRecyclerViewAdapterListener listener) {
        this.datasource= datasource;
        modifiedDatasource.add(TYPE_GOOGLE_PAY,"GooglePay");
        modifiedDatasource.addAll(TYPE_SAVED_CARD,datasource);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("viewType value"+viewType);
        System.out.println("totalItemCount value"+modifiedDatasource);
        switch(viewType) {
            case TYPE_SAVED_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gosellapi_viewholder_recent_payments, parent, false);
                return new RecentPaymentsViewHolder(view);
            case TYPE_GOOGLE_PAY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gosellapi_viewholder_gpay, parent, false);
                return new GooglePaymentViewHolder(view);

            default:view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gosellapi_viewholder_gpay, parent, false);
                return new GooglePaymentViewHolder(view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        System.out.println("position inside"+position);
        if(position< modifiedDatasource.size()-1){
            return TYPE_SAVED_CARD;
        }else return TYPE_GOOGLE_PAY;
       // return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        System.out.println("holder Type"+holder.getItemViewType());
        System.out.println("position Type"+position);
        switch (holder.getItemViewType()) {
            case 0:
                GooglePaymentViewHolder googlePaymentViewHolder = (GooglePaymentViewHolder) holder;
                googlePaymentViewHolder.bind(position,holder);
                break;
            case 1:
                RecentPaymentsViewHolder recentPaymentViewHolder = (RecentPaymentsViewHolder)holder;
                SavedCard card = datasource.get(position);
                recentPaymentViewHolder.bind(position, card);

                break;
        }

        //holder.setFocused(position == focusedPosition);
    }

    @Override
    public int getItemCount() {
//        totalItemCount = datasource.size()+1;
        return modifiedDatasource.size();
    }


    public void shakeAllCards(GroupViewHolder groupViewHolderListener) {
//        Log.d("RecePaymentRecycleAdapt","shake all cards...");
        this.groupViewHolderListener = groupViewHolderListener;
        for (int childCount = this.parent.getChildCount(), i = 0; i < childCount; ++i) {
            final RecentPaymentsViewHolder holder = (RecentPaymentsViewHolder) this.parent.getChildViewHolder(this.parent.getChildAt(i));
//            System.out.println("RecePaymentRecycleAdapt : "+holder.card.getId());
            holder.setFocused(false);
            holder.showDelete();
            Animation animation =
                    AnimationUtils.loadAnimation(this.parent.getContext(), R.anim.shake);
//            holder.cardView.startAnimation(animation);
//            holder.deleteImageView.startAnimation(animation);
            holder.shakingArea.startAnimation(animation);
        }
    }

    public void stopShakingAllCards() {
//        Log.d("RecePaymentRecycleAdapt","stop shake all cards...");
        if(this.parent.getChildCount()>0){
            for (int childCount = this.parent.getChildCount(), i = 0; i < childCount; ++i) {
                final RecentPaymentsViewHolder holder = (RecentPaymentsViewHolder) this.parent.getChildViewHolder(this.parent.getChildAt(i));
                holder.hideDelete();
                holder.shakingArea.setAnimation(null);
            }
            if(this.groupViewHolderListener!=null)groupViewHolderListener.changeGroupActionTitle();
        }

    }

    public void checkShakingStatus(int position){
        RecentPaymentsViewHolder  holder = (RecentPaymentsViewHolder) parent.findViewHolderForAdapterPosition(position);
        if(holder==null)return;
        if(holder.getShakinStatusFlag())
        {
            if(this.groupViewHolderListener!=null)groupViewHolderListener.changeGroupActionTitle();
            stopShakingAllCards();
        }
    }

    /**
     * Sets focused.
     *
     * @param focused the focused
     */
    public void setFocused(boolean focused) {
        setFocused(focused ? focusedPosition : Constants.NO_FOCUS);
    }

    /**
     * Clear focus.
     */
    public void clearFocus()
    {
//        System.out.println("afterText changed ... adapter.clear "+focusedPosition);
        RecentPaymentsViewHolder oldHolder;
        parent.clearFocus();
        if (focusedPosition != Constants.NO_FOCUS) {
            oldHolder = (RecentPaymentsViewHolder) parent.findViewHolderForAdapterPosition(focusedPosition);
            if (oldHolder != null) {
                oldHolder.setFocused(false);
            }
        }
        listener.recentPaymentItemClicked(Constants.NO_FOCUS);
    }

    private void setFocused(int position) {
//        if (focusedPosition != Constants.NO_FOCUS) {
//            oldHolder = (RecentPaymentsViewHolder) parent.findViewHolderForAdapterPosition(focusedPosition);
//            if (oldHolder != null) {
//                oldHolder.setFocused(false);
//            }
//        }
        focusedPosition = position;
//        RecentPaymentsViewHolder newHolder = (RecentPaymentsViewHolder) parent.findViewHolderForAdapterPosition(position);
//        if (newHolder != null) {
//            newHolder.setFocused(true);
//        }
        RecentPaymentsViewHolder newHolder = (RecentPaymentsViewHolder) parent.findViewHolderForAdapterPosition(position);

        for (int childCount = this.parent.getChildCount(), i = 0; i < childCount; ++i) {

            final RecentPaymentsViewHolder holder = (RecentPaymentsViewHolder) this.parent.getChildViewHolder(this.parent.getChildAt(i));

            if(holder!=null && holder == newHolder && holder.getFocusStatus()){
                continue;
            }
            holder.setFocused(false);
        }


        if(newHolder!=null)
            newHolder.setFocused(true);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parent = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        parent = null;
    }

    /**
     * The type Recent payments view holder.
     */
    public class RecentPaymentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnLongClickListener{
        private int position;
        private SavedCard card;
        private ImageView itemCheckmark;
        private ImageView logoImageView;
        private RelativeLayout recentPaymentsCardViewLayout;
        private MaterialCardView cardView;
        private ImageView deleteImageView;
        private RelativeLayout shakingArea;

        private boolean alreadyFocusedFlag;
        private boolean shakingStatusFlag;

        private RecentPaymentsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }



        private void bind(int position, SavedCard card) {
            this.position = position;
            this.card = card;

            recentPaymentsCardViewLayout = itemView.findViewById(R.id.recentPaymentsCardViewLayout);
            shakingArea = itemView.findViewById(R.id.shakingArea);

            shakingArea.setOnLongClickListener(
                    v -> {
//                        System.out.println("LONG CLICK   ......");

                        return false;
                    }
            );

            String cardNumber = String.format(itemView.getResources().getString(R.string.textview_placeholder_last_four_digits), card.getLastFour());
            TextView cardLastDigits = itemView.findViewById(R.id.cardLastDigits);
            cardLastDigits.setText(cardNumber);
//            cardView = itemView.findViewById(R.id.parent_card);
            cardView = itemView.findViewById(R.id.cardView);
            itemCheckmark = itemView.findViewById(R.id.itemCheckmark);
            logoImageView = itemView.findViewById(R.id.logoImageView);
            deleteImageView= itemView.findViewById(R.id.deleteImageView);

            deleteImageView.setOnClickListener(v->{
                showDeleteConfirmationDialog(v,position);
            });
            Glide.with(itemView.getContext()).load(card.getImage()).into(logoImageView);
        }


        private void showDeleteConfirmationDialog(View v, int clickedPosition){
//            System.out.println("position ... "+clickedPosition);
            SpannableStringBuilder str = new SpannableStringBuilder(" xxxx");
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    0, str.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(v.getContext().getString(R.string.delete_saved_card_msg));
            stringBuilder.append(str);
            stringBuilder.append(datasource.get(clickedPosition).getLastFour()+
                    "?");

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseActivity.getCurrent());
            dialogBuilder.setTitle(v.getContext().getString(R.string.delete_saved_card_title));
            dialogBuilder.setMessage(stringBuilder.toString());
            dialogBuilder.setCancelable(false);

            dialogBuilder.setPositiveButton(v.getContext().getString(R.string.delete_saved_card_button_Delete),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(groupViewHolderListener!=null)groupViewHolderListener.changeGroupActionTitle();
                            listener.deleteCard(datasource.get(clickedPosition).getId());
                        }
                    });
            dialogBuilder.setNegativeButton(v.getContext().getString(R.string.delete_saved_card_button_Cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(groupViewHolderListener!=null)groupViewHolderListener.changeGroupActionTitle();
                            stopShakingAllCards();
                        }
                    });

            dialogBuilder.show();
        }

        @Override
        public void onClick(View v) {

            if(PaymentDataManager.getInstance().getPaymentOptionsRequest().getTransactionMode()== TransactionMode.SAVE_CARD)return;

            checkShakingStatus(position);

            RecentPaymentsRecyclerViewAdapter.this.setFocused(position);
            listener.recentPaymentItemClicked(position);
        }



        public void showDelete(){
            this.deleteImageView.setVisibility(View.VISIBLE);
            this.shakingStatusFlag=true;
        }

        public void hideDelete(){
            this.deleteImageView.setVisibility(View.INVISIBLE);
            this.shakingStatusFlag=false;
        }

        private void setFocused(boolean focused) {

            if(focused && this.alreadyFocusedFlag) return;

            if(focused){
                itemCheckmark.setVisibility(View.VISIBLE);
                cardView.setStrokeWidth(1);
                cardView.setStrokeColor(itemView.getResources().getColor(R.color.vibrant_green));
                this.alreadyFocusedFlag=true;

            }else {
                itemCheckmark.setVisibility(View.INVISIBLE);
                cardView.setStrokeWidth(0);
                this.alreadyFocusedFlag=false;
            }

            recentPaymentsCardViewLayout.setSelected(focused);
        }

        @Override
        public boolean onLongClick(View v) {
            // display edit button
            return false;
        }


        public boolean getFocusStatus(){
            return this.alreadyFocusedFlag;
        }

        public boolean getShakinStatusFlag(){
            return this.shakingStatusFlag;
        }



    }
    public class GooglePaymentViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout google_pay_button_layout;
        // A client for interacting with the Google Pay API.
        private PaymentsClient paymentsClient;
        // Arbitrarily-picked constant integer you define to track a request for payment data activity.
        private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
        private GooglePaymentViewHolder(View itemView){
            super(itemView);
            paymentsClient = PaymentsUtil.createPaymentsClient(  GoSellPaymentActivity.getCurrent());
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void bind(int position, RecyclerView.ViewHolder holder) {
            google_pay_button_layout = itemView.findViewById(R.id.google_pay_button_layout);

            google_pay_button_layout.setOnClickListener(v -> {
                requestPayment(v,holder);
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void requestPayment(View view, RecyclerView.ViewHolder holder) {




            // The price provided to the API should include taxes and shipping.
            // This price is not displayed to the user.
            double garmentPrice = PaymentDataSource.getInstance().getAmount().doubleValue();
            long garmentPriceCents = Math.round(garmentPrice * PaymentsUtil.CENTS_IN_A_UNIT.longValue());
            long priceCents = garmentPriceCents ;

            Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents);
            if (!paymentDataRequestJson.isPresent()) {
                return;
            }

            PaymentDataRequest request =
                    PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
            // AutoResolveHelper to wait for the user interacting with it. Once completed,
            // onActivityResult will be called with the result.
            if (request != null) {
                 AutoResolveHelper.resolveTask(
                         paymentsClient.loadPaymentData(request),
                       GoSellPaymentActivity.getCurrent(), LOAD_PAYMENT_DATA_REQUEST_CODE);
             }

        }

    }

}
