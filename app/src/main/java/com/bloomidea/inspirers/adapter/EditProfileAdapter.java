package com.bloomidea.inspirers.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.bloomidea.inspirers.EditProfileActivity;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyExpandableLayout;
import com.bloomidea.inspirers.listener.ArrayListListener;
import com.bloomidea.inspirers.listener.PictureLoadListener;
import com.bloomidea.inspirers.model.Country;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.utils.Utils;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static com.bloomidea.inspirers.application.AppController.getmInstance;

/**
 * Created by michellobato on 19/04/17.
 */

public class EditProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MY_INFO = 101;
    //private static final int TYPE_MY_MEDS = 102;
    private static final int TYPE_MY_LANGUAGES = 103;
    private static final int TYPE_MY_HOBBIES = 104;

    private EditProfileActivity context;
    private User activeUser;
    private ArrayList<UserMedicine> auxListMeds;

    private String nameSelected;
    private String userProvidedIdSelected;

    private InteractionListener listener;

    private Bitmap mCurrentPhotoAvatar = null;
    private int selectedCountryPos = -1;
    private String auxCountryName;
    private String auxCountryFlag;
    private String auxCountryIso;

    private SpacingItemDecoration spacingLanguagesItemDecoration;
    private StringProfileAdapter languagesItemsAdapter;
    private boolean[] selectionLanguage;
    private ArrayList<String> selectedLanguages;


    private SpacingItemDecoration spacingHobbiesItemDecoration;
    private StringProfileAdapter hobbiesItemsAdapter;
    private ArrayList<String> selectedHobbies;

    public EditProfileAdapter(EditProfileActivity context, InteractionListener listener) {
        this.context = context;
        this.activeUser = AppController.getmInstance().getActiveUser();

        this.nameSelected = activeUser.getUserName();

        this.userProvidedIdSelected = activeUser.getUserProvidedId();

        loadMedicineList();

        this.listener = listener;
        this.selectedLanguages = activeUser.getLanguagesAsArrayList();

        initLanguagesSelection();
        this.selectedHobbies = activeUser.getHobbiesAsArrayList();
    }

    public void loadMedicineList(){
        this.auxListMeds = AppController.getmInstance().getMedicineDataSource().getUserMedicines(activeUser.getId());
    }

    private void initLanguagesSelection() {
        CharSequence[] listLanguages = getmInstance().getListLanguages();
        selectionLanguage = new boolean[listLanguages.length];

        if (selectedLanguages == null) {
            return ;
        }

        for (int i = 0; i < listLanguages.length; i++) {
            selectionLanguage[i] = false;

            for(String aux : selectedLanguages){
                if (aux.equals(listLanguages[i])) {
                    selectionLanguage[i] = true;
                    break;
                }
            }
        }
    }

    private void loadLanguagesSelectionFromSelection() {
        this.selectedLanguages = new ArrayList<>();

        CharSequence[] listLanguages = getmInstance().getListLanguages();

        for(int i = 0; i< selectionLanguage.length;i++){
            if(selectionLanguage[i]){
                selectedLanguages.add(listLanguages[i].toString());
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_MY_INFO) {
            view = LayoutInflater.from(context).inflate(R.layout.item_edit_my_info, parent, false);
            viewHolder = new ViewHolderMyInfo(view);
        } /*else if(viewType == TYPE_MY_MEDS) {
            view = LayoutInflater.from(context).inflate(R.layout.item_edit_my_medicine, parent, false);
            viewHolder = new ViewHolderMyMeds(view);
        } */else if(viewType == TYPE_MY_LANGUAGES) {
            view = LayoutInflater.from(context).inflate(R.layout.item_edit_my_languages, parent, false);
            viewHolder = new ViewHolderMyLanguages(view);
        } else{
            //TYPE_MY_HOBBIES
            view = LayoutInflater.from(context).inflate(R.layout.item_edit_my_hobbies, parent, false);
            viewHolder = new ViewHolderMyHobbies(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderMyInfo){
            final ViewHolderMyInfo auxHolder = (ViewHolderMyInfo) holder;
            auxHolder.profile_imageView.setDrawingCacheEnabled(true);

            if(mCurrentPhotoAvatar==null) {
                Utils.loadImageView(context, auxHolder.profile_imageView, null, activeUser.getPictureUrl(), activeUser.getPicture(), R.drawable.default_avatar, null);
            }else{
                Utils.loadImageView(context, auxHolder.profile_imageView, null, "", mCurrentPhotoAvatar, R.drawable.default_avatar, new PictureLoadListener() {
                    @Override
                    public void onEndLoad(boolean ok) {
                        if(ok) {
                            mCurrentPhotoAvatar = auxHolder.profile_imageView.getDrawingCache();
                            mCurrentPhotoAvatar = mCurrentPhotoAvatar.copy(mCurrentPhotoAvatar.getConfig(), true);
                        }
                    }
                });
            }

            auxHolder.profile_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.avatarClick();
                }
            });

            auxHolder.user_name_editText.setText(nameSelected);
            auxHolder.user_name_editText.setTag(R.id.tag_viewholder, auxHolder);
            auxHolder.user_name_editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    nameSelected = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            auxHolder.user_id_editText.setText(userProvidedIdSelected);
            auxHolder.user_id_editText.setTag(R.id.tag_viewholder, auxHolder);
            auxHolder.user_id_editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    userProvidedIdSelected = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            if(selectedCountryPos == -1) {
                auxHolder.country_flag_imageView.setVisibility(View.VISIBLE);

                if (activeUser.getCountryName() != null && !activeUser.getCountryName().isEmpty()) {
                    Utils.loadImageView(context, auxHolder.country_flag_imageView, null, activeUser.getCountryFlagUrl(), activeUser.getCountryFlag(), R.drawable.no_photo, null);

                    auxHolder.country_name_textView.setText(activeUser.getCountryName());
                }else{
                    auxCountryName =  context.getString(R.string.default_ct_name);
                    auxCountryFlag = context.getString(R.string.default_ct_flag_url);
                    auxCountryIso = context.getString(R.string.default_ct_iso);

                    Utils.loadImageView(context, auxHolder.country_flag_imageView, null, auxCountryFlag, null, R.drawable.no_photo, null);

                    auxHolder.country_name_textView.setText(auxCountryName);
                }
            }else{
                auxHolder.country_flag_imageView.setVisibility(View.VISIBLE);

                Utils.loadImageView(context, auxHolder.country_flag_imageView, null, auxCountryFlag, null, R.drawable.no_photo, null);

                auxHolder.country_name_textView.setText(auxCountryName);
            }

            auxHolder.country_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.countryBoxClick(selectedCountryPos);
                }
            });

        }/*else if(holder instanceof ViewHolderMyMeds){
            ViewHolderMyMeds auxHolder = (ViewHolderMyMeds) holder;
            auxHolder.fixed_meds.removeAllViews();

            int i = 1;
            for(UserMedicine userMed : auxListMeds){
                View aux = LayoutInflater.from(context).inflate(R.layout.layout_edit_profile_medicine,null);

                if(i==1){
                    aux.findViewById(R.id.line).setVisibility(View.GONE);
                }

                ((ImageView) aux.findViewById(R.id.medicine_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(userMed.getMedicineType().getCode()));
                ((TextView) aux.findViewById(R.id.medicine_text_textView)).setText(userMed.getMedicineName());

                aux.findViewById(R.id.delete_medicine).setTag(R.id.tag_item_code,userMed);
                aux.findViewById(R.id.delete_medicine).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.medicineDelete(((UserMedicine) view.getTag(R.id.tag_item_code)));
                    }
                });

                aux.findViewById(R.id.edit_medicine).setTag(R.id.tag_item_code,userMed);
                aux.findViewById(R.id.edit_medicine).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.medicineEdit(((UserMedicine) view.getTag(R.id.tag_item_code)));
                    }
                });

                if(userMed instanceof UserSOSMedicine){
                    aux.findViewById(R.id.edit_medicine).setVisibility(View.GONE);
                }

                auxHolder.fixed_meds.addView(aux);

                i++;
            }
        }*/else if(holder instanceof ViewHolderMyLanguages){
            final ViewHolderMyLanguages auxHolder = (ViewHolderMyLanguages) holder;

            FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
            flowLayoutManager.setAutoMeasureEnabled(true);

            auxHolder.languages_recyclerview.setLayoutManager(flowLayoutManager);

            languagesItemsAdapter = new StringProfileAdapter(context, selectedLanguages, new StringProfileAdapter.OnRemoveListener() {
                @Override
                public void onItemRemoved(int position) {
                    selectedLanguages.remove(position);

                    if(selectedLanguages.isEmpty()){
                        notifyDataSetChanged();
                    }else{
                        languagesItemsAdapter.notifyItemRemoved(position);
                    }
                }
            });

            auxHolder.languages_recyclerview.setAdapter(languagesItemsAdapter);

            int itemSpacing = context.getResources().getDimensionPixelOffset(R.dimen.string_item_spacing);

            if(spacingLanguagesItemDecoration==null) {
                spacingLanguagesItemDecoration = new SpacingItemDecoration(itemSpacing, itemSpacing);
                auxHolder.languages_recyclerview.addItemDecoration(spacingLanguagesItemDecoration);
                auxHolder.languages_recyclerview.setHasFixedSize(true);
            }

            auxHolder.expandable_layout.setClosedPosition(context.getResources().getDimensionPixelOffset(R.dimen.string_item_h) + itemSpacing);

            if(selectedLanguages!= null && selectedLanguages.size() > 0) {
                auxHolder.no_languages.setVisibility(View.GONE);
            }else{
                auxHolder.no_languages.setVisibility(View.VISIBLE);
            }

            auxHolder.expandable_layout.collapse(false);

            auxHolder.expand.setTag(R.id.tag_viewholder,holder);
            auxHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ViewHolderMyLanguages) view.getTag(R.id.tag_viewholder)).expandable_layout.toggle();
                }
            });

            auxHolder.expand.setText(R.string.view_more_no_underline);
            auxHolder.expandable_layout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        auxHolder.expandable_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int heightEL = auxHolder.expandable_layout.getMeasuredHeight();
                        int heightRV = auxHolder.languages_recyclerview.getMeasuredHeight();

                        if(heightRV > heightEL){
                            auxHolder.expand.setVisibility(View.VISIBLE);
                        }else{
                            auxHolder.expand.setVisibility(View.GONE);
                        }
                    }
            });
            auxHolder.expandable_layout.setOnExpansionUpdateListener(new MyExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansionFraction) {
                    if(expansionFraction==0){
                        auxHolder.expand.setText(R.string.view_more_no_underline);
                    }else{
                        auxHolder.expand.setText(R.string.view_less_no_underline);
                    }
                }
            });

            auxHolder.languages_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initLanguagesSelection();
                    listener.languageBoxClick(selectionLanguage);
                }
            });
        }else if(holder instanceof ViewHolderMyHobbies){
            final ViewHolderMyHobbies auxHolder = (ViewHolderMyHobbies) holder;

            FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
            flowLayoutManager.setAutoMeasureEnabled(true);

            auxHolder.hobbies_recyclerview.setLayoutManager(flowLayoutManager);

            hobbiesItemsAdapter = new StringProfileAdapter(context, selectedHobbies, new StringProfileAdapter.OnRemoveListener() {
                @Override
                public void onItemRemoved(int position) {
                    selectedHobbies.remove(position);

                    if(selectedHobbies.isEmpty()){
                        notifyDataSetChanged();
                    }else{
                        hobbiesItemsAdapter.notifyItemRemoved(position);
                    }
                }
            });

            auxHolder.hobbies_recyclerview.setAdapter(hobbiesItemsAdapter);

            int itemSpacing = context.getResources().getDimensionPixelOffset(R.dimen.string_item_spacing);

            if(spacingHobbiesItemDecoration==null) {
                spacingHobbiesItemDecoration = new SpacingItemDecoration(itemSpacing, itemSpacing);
                auxHolder.hobbies_recyclerview.addItemDecoration(spacingHobbiesItemDecoration);
                auxHolder.hobbies_recyclerview.setHasFixedSize(true);
            }

            auxHolder.expandable_layout.setClosedPosition(context.getResources().getDimensionPixelOffset(R.dimen.string_item_h) + itemSpacing);

            if(selectedHobbies!= null && selectedHobbies.size() > 0) {
                auxHolder.no_hobbies.setVisibility(View.GONE);
            }else{
                auxHolder.no_hobbies.setVisibility(View.VISIBLE);
            }

            auxHolder.expandable_layout.collapse(false);

            auxHolder.expand.setTag(R.id.tag_viewholder,holder);
            auxHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ViewHolderMyHobbies) view.getTag(R.id.tag_viewholder)).expandable_layout.toggle();
                }
            });

            auxHolder.expand.setText(R.string.view_more_no_underline);
            auxHolder.expandable_layout.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            auxHolder.expandable_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            int heightEL = auxHolder.expandable_layout.getMeasuredHeight();
                            int heightRV = auxHolder.hobbies_recyclerview.getMeasuredHeight();

                            if(heightRV > heightEL){
                                auxHolder.expand.setVisibility(View.VISIBLE);
                            }else{
                                auxHolder.expand.setVisibility(View.GONE);
                            }
                        }
                    });
            auxHolder.expandable_layout.setOnExpansionUpdateListener(new MyExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansionFraction) {
                    if(expansionFraction==0){
                        auxHolder.expand.setText(R.string.view_more_no_underline);
                    }else{
                        auxHolder.expand.setText(R.string.view_less_no_underline);
                    }
                }
            });

            auxHolder.ok_btn.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolderMyHobbies auxH = (ViewHolderMyHobbies) view.getTag(R.id.tag_viewholder);

                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(auxH.hobbie_editText.getWindowToken(), 0);

                    String auxText = auxH.hobbie_editText.getText().toString();

                    if(auxText!=null && !auxText.isEmpty()){
                        selectedHobbies.add(auxText);
                        auxH.hobbie_editText.setText("");
                        notifyDataSetChanged();
                    }
                }
            });

            auxHolder.save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.saveBtnClick(nameSelected,
                            userProvidedIdSelected,
                            auxCountryName,
                            auxCountryFlag,
                            auxCountryIso,
                            mCurrentPhotoAvatar,
                            selectedLanguages,
                            selectedHobbies);
                }
            });
        }
    }

    public void setSelectedCountry(int posSelected){
        selectedCountryPos = posSelected;

        getmInstance().getCountryListAsArrayList(new ArrayListListener() {
            @Override
            public void onResponse(List response) {
                Country aux = (Country) response.get(selectedCountryPos);

                auxCountryName = aux.getName();
                auxCountryFlag = aux.getFlag();
                auxCountryIso = aux.getIso();

                notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void setAvatarPicture(Bitmap newAvatarPicture){
        mCurrentPhotoAvatar = newAvatarPicture;
        //((ImageView) findViewById(R.id.profile_imageView)).setImageBitmap(mCurrentPhotoAvatar);
        //mCurrentPhotoAvatar = findViewById(R.id.profile_imageView).getDrawingCache();
        //mCurrentPhotoAvatar = mCurrentPhotoAvatar.copy(mCurrentPhotoAvatar.getConfig(),true);

        notifyDataSetChanged();
    }

    private CharSequence[] getCountryNames(ArrayList<Country> list){
        CharSequence[] result = new CharSequence[list.size()];

        int pos = 0;

        for(Country country : list){
            result[pos] = country.getName();
            pos++;
        }

        return result;
    }

    public void setLanguageSelected(int position, boolean selection) {
        selectionLanguage[position] = selection;
    }

    public void languagesEndEditing() {
        loadLanguagesSelectionFromSelection();
    }

    public static class ViewHolderMyInfo extends RecyclerView.ViewHolder {
        public ImageView profile_imageView;
        public EditText user_name_editText;
        public EditText user_id_editText;
        public View country_box;
        public TextView country_name_textView;
        public ImageView country_flag_imageView;

        public ViewHolderMyInfo(View v) {
            super(v);

            profile_imageView = (ImageView) v.findViewById(R.id.profile_imageView);
            user_name_editText = (EditText) v.findViewById(R.id.user_name_editText);
            user_id_editText = (EditText) v.findViewById(R.id.user_id_edittext);
            country_box =  v.findViewById(R.id.country_box);

            country_name_textView = (TextView) v.findViewById(R.id.country_name_textView);
            country_flag_imageView = (ImageView) v.findViewById(R.id.country_flag_imageView);
        }
    }

    /*
    public static class ViewHolderMyMeds extends RecyclerView.ViewHolder {
        public LinearLayout fixed_meds;

        public ViewHolderMyMeds(View v) {
            super(v);

            fixed_meds = (LinearLayout) v.findViewById(R.id.fixed_meds);
        }
    }
*/
    public static class ViewHolderMyLanguages extends RecyclerView.ViewHolder {
        public MyExpandableLayout expandable_layout;
        public RecyclerView languages_recyclerview;
        public View languages_box;

        public TextView expand;
        public View no_languages;

        public ViewHolderMyLanguages(View v) {
            super(v);

            expandable_layout = (MyExpandableLayout) v.findViewById(R.id.expandable_layout);
            languages_recyclerview = (RecyclerView) v.findViewById(R.id.languages_recyclerview);
            languages_box = v.findViewById(R.id.languages_box);
            expand = (TextView) v.findViewById(R.id.expand);
            no_languages = v.findViewById(R.id.no_languages);
        }
    }

    public static class ViewHolderMyHobbies extends RecyclerView.ViewHolder {
        public MyExpandableLayout expandable_layout;
        public RecyclerView hobbies_recyclerview;
        public View no_hobbies;

        public TextView expand;
        public View ok_btn;
        public View save_btn;
        public EditText hobbie_editText;

        public ViewHolderMyHobbies(View v) {
            super(v);

            expandable_layout = (MyExpandableLayout) v.findViewById(R.id.expandable_layout);
            hobbies_recyclerview = (RecyclerView) v.findViewById(R.id.hobbies_recyclerview);
            no_hobbies = v.findViewById(R.id.no_hobbies);
            expand = (TextView) v.findViewById(R.id.expand);
            ok_btn = v.findViewById(R.id.ok_btn);
            save_btn = v.findViewById(R.id.save_btn);
            hobbie_editText = (EditText) v.findViewById(R.id.hobbie_editText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_MY_INFO;
        /*if(position == 1){
            type = TYPE_MY_MEDS;
        }else*/ if(position == 1){
            type = TYPE_MY_LANGUAGES;
        }else if(position == 2){
            type = TYPE_MY_HOBBIES;
        }

        return type;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public interface InteractionListener{
        void avatarClick();
        void countryBoxClick(int previousSelection);
        //void medicineDelete(UserMedicine medicineToDelete);
        //void medicineEdit(UserMedicine medicineToEdit);
        void languageBoxClick(boolean[] previousSelection);
        void saveBtnClick(String name,String userProvidedId,  String countryName, String countryFlagUrl,String countryIso, Bitmap newPicture, ArrayList<String> newLanguages, ArrayList<String> newHobbies);
    }
}
