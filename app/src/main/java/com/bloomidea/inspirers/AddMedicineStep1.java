package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.InhalerAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.VerticalHorizontalSpaceItemDecoration;
import com.bloomidea.inspirers.model.Inhaler;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.InhalerTypeAux;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import org.medida.inhalerdetection.InhalerDetectionActivity;

import java.util.ArrayList;

import static com.bloomidea.inspirers.utils.MedicineTypeAux.needsShowName;


public class AddMedicineStep1 extends Fragment {
    private static final String ARG_IS_SOS = "ARG_IS_SOS";
    private static final String ARG_MEDICINE_TO_EDIT = "ARG_MEDICINE_TO_EDIT";

    private boolean isSOS;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private LinearLayout medicineType1;
    private LinearLayout medicineType2;
    private LinearLayout medicineType3;

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewMore;

    private InhalerAdapter adapter;
    private InhalerAdapter adapterMore;

    private ArrayList<Inhaler> inhalersList;
    private ArrayList<Inhaler> moreinhalersList;

    private boolean showMain = true;
    private boolean showMore = false;

    private UserMedicine medicineToEdit = null;
    private Inhaler inhalerAux;

    private AlertDialog dialog;

    public AddMedicineStep1() {
        // Required empty public constructor
    }

    public static AddMedicineStep1 newInstance(boolean isSOS, UserMedicine medicineToEdit) {
        AddMedicineStep1 fragment = new AddMedicineStep1();

        Bundle args = new Bundle();

        args.putBoolean(ARG_IS_SOS, isSOS);
        args.putSerializable(ARG_MEDICINE_TO_EDIT,medicineToEdit);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isSOS = getArguments().getBoolean(ARG_IS_SOS);
            medicineToEdit = (UserMedicine) getArguments().getSerializable(ARG_MEDICINE_TO_EDIT);
        }
    }

    private int selectedTypePos;
    private View flutiformDosageBoxLayout;
    private EditText flutiformDosageEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_add_medicine_step1, container, false);

            if(isSOS){
                ((ImageView) rootView.findViewById(R.id.imageView8)).setImageResource(R.drawable.add_med_sos_first);
            }

            selectedTypePos = -1;

            flutiformDosageBoxLayout = rootView.findViewById(R.id.medicine_dosage_box);
            flutiformDosageEditText = (EditText) rootView.findViewById(R.id.medicine_dosage_editText);

            inhalersList = InhalerTypeAux.getMainInhalerType();
            moreinhalersList = InhalerTypeAux.getMoreInhalerType();

            adapter = new InhalerAdapter(getActivity(), inhalersList, false, new InhalerAdapter.InhalerAdapterListener() {
                @Override
                public void onClickInhaler(Inhaler inhaler) {

                    for (Inhaler in : moreinhalersList){
                        in.setSelected(false);
                        adapterMore.notifyDataSetChanged();
                    }

                    for (Inhaler in : inhalersList){
                        if (in.isSelected() && !inhaler.equals(in)){
                            in.setSelected(false);
                        }
                    }

                    inhalerAux = inhaler;
                    setSelectedMedicineTypeName(selectedTypePos);
                    inhaler.setSelected(!inhaler.isSelected());
                    changeListInterface();

                }
            });


            adapterMore = new InhalerAdapter(getActivity(), moreinhalersList, true, new InhalerAdapter.InhalerAdapterListener() {
                @Override
                public void onClickInhaler(Inhaler inhaler) {

                    for (Inhaler in : inhalersList){
                        in.setSelected(false);
                        adapter.notifyDataSetChanged();
                    }

                    for (Inhaler in : moreinhalersList){
                        if (in.isSelected() && !inhaler.equals(in)){
                            in.setSelected(false);
                        }
                    }

                    /* PEDRO if (inhaler.getType().equals(InhalerDetectionActivity.InhalerType.Unknown)){
                        getActivity().findViewById(R.id.overlay_other_med_name).setVisibility(View.VISIBLE);
                        inhaler.setSelected(true);
                    }else{
                        inhaler.setSelected(!inhaler.isSelected());
                    }*/

                    inhalerAux = inhaler;
                    setSelectedMedicineTypeName(selectedTypePos);

                    changeListInterface();
                }
            });


            recyclerView = rootView.findViewById(R.id.mainInhalerList);
            recyclerViewMore = rootView.findViewById(R.id.moreInhalerList);

            VerticalHorizontalSpaceItemDecoration dividerItemDecoration = new VerticalHorizontalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin_div_8),false);
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerViewMore.addItemDecoration(dividerItemDecoration);

            recyclerView.setAdapter(adapter);
            recyclerViewMore.setAdapter(adapterMore);

            medicineType1 = rootView.findViewById(R.id.medicine_type_1);
            medicineType2 = rootView.findViewById(R.id.medicine_type_2);
            medicineType3 = rootView.findViewById(R.id.medicine_type_3);

            setSelectedMedicineTypeName(0);

            medicineType1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedMedicineTypeName(0);
                }
            });

            medicineType2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedMedicineTypeName(1);
                }
            });

            medicineType3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedMedicineTypeName(2);
                }
            });

            getActivity().findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().findViewById(R.id.overlay_other_med_name).setVisibility(View.GONE);
                    String medicineName = ((EditText) getActivity().findViewById(R.id.overlay_medicine_name_editText)).getText().toString();
                    proccedNextStep(medicineName);

                }
            });

            getActivity().findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().findViewById(R.id.overlay_other_med_name).setVisibility(View.GONE);
                }
            });

            getActivity().findViewById(R.id.overlay_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().findViewById(R.id.overlay_other_med_name).setVisibility(View.GONE);
                }
            });


            rootView.findViewById(R.id.dropBoxName).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AutoCompleteTextView autoT = (AutoCompleteTextView) rootView.findViewById(R.id.medicine_name_editText);
                    autoT.showDropDown();
                }
            });

            rootView.findViewById(R.id.medicine_type_box).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

                    adb.setTitle(R.string.medicine_type_title);
                    adb.setSingleChoiceItems(MedicineTypeAux.getMedicineTypesNames(getActivity()), selectedTypePos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setSelectedMedicineTypeName(i);
                            dialogInterface.dismiss();
                        }
                    });

                    adb.setNegativeButton(getString(R.string.cancel), null);

                    adb.show();
                }
            });

            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String medicineName = ((EditText) rootView.findViewById(R.id.medicine_name_editText)).getText().toString();

                    proccedNextStep(medicineName);


                }
            });

            rootView.findViewById(R.id.inhalerType).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (showMain){
                        rootView.findViewById(R.id.mainInhalerList).setVisibility(View.GONE);
                        rootView.findViewById(R.id.inhalerTypeArrow).setRotation(180);
                        rootView.findViewById(R.id.moreTypeArrow).setRotation(180);
                    }else{
                        rootView.findViewById(R.id.mainInhalerList).setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.moreInhalerList).setVisibility(View.GONE);
                        rootView.findViewById(R.id.inhalerTypeArrow).setRotation(0);
                        rootView.findViewById(R.id.moreTypeArrow).setRotation(180);
                        showMore = false;
                    }
                    showMain = !showMain;
                }
            });

            rootView.findViewById(R.id.viewMore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showMore){
                        rootView.findViewById(R.id.moreInhalerList).setVisibility(View.GONE);
                        rootView.findViewById(R.id.moreTypeArrow).setRotation(180);
                    }else{
                        rootView.findViewById(R.id.moreInhalerList).setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.mainInhalerList).setVisibility(View.GONE);
                        rootView.findViewById(R.id.inhalerTypeArrow).setRotation(180);
                        rootView.findViewById(R.id.moreTypeArrow).setRotation(0);
                        showMain = false;
                    }
                    showMore = !showMore;
                }
            });

            if(medicineToEdit != null){
                loadMedicineInfo();
            }
        }

        return rootView;
    }



    private void proccedNextStep(final String medName){
        final MedicineType auxMedicineType = selectedTypePos != -1 ? MedicineTypeAux.getMedicineTypes(getActivity()).get(selectedTypePos) : null;

        String dosage = flutiformDosageEditText.getText().toString();
        final String barCode = ((EditText) rootView.findViewById(R.id.medicine_codebar)).getText().toString();

        if (checkInfo(auxMedicineType, medName, dosage, barCode)) {
            final int dosageInt = 0;

                        /*try {
                            dosageInt = Integer.parseInt(dosage);
                        } catch (NumberFormatException e) {
                            Log.d("Exception", e.toString());
                        }*/

            if (auxMedicineType.getCode().equals(MedicineTypeAux.TYPE1_CODE) && medicineToEdit == null) {
                ArrayList<UserMedicine> myMeds = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicinesAux(AppController.getmInstance().getActiveUser().getId());


                for (final UserMedicine med : myMeds) {
                    if (med.getMedicineName().equals(medName)){
                        // ASK TO EDIT OR CREATE A NEW
                        androidx.appcompat.app.AlertDialog.Builder dialog = new 	androidx.appcompat.app.AlertDialog.Builder(getActivity());
                        dialog.setMessage(getResources().getString(R.string.medicine_exists));
                        dialog.setPositiveButton(getResources().getString(R.string.edit_medicine), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                medicineToEdit = med;
                                ((AddMedicineActivity) getActivity()).editMedicineStep1((UserNormalMedicine) med);
                                mListener.step1End(auxMedicineType, medName, dosageInt, barCode);
                            }
                        });
                        dialog.setNeutralButton(getString(R.string.create_new_medicine), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                medicineToEdit = null;
                                ((AddMedicineActivity) getActivity()).editMedicineStep1(null);
                                mListener.step1End(auxMedicineType, medName, dosageInt, barCode);
                            }
                        });
                        dialog.show();
                        return;
                    }
                }
                mListener.step1End(auxMedicineType, medName, dosageInt, barCode);
            } else {
                mListener.step1End(auxMedicineType, medName, dosageInt, barCode);
            }

        }
    }

    private void changeListInterface(){
        adapterMore.setContainsSelection(containsSelected(moreinhalersList) || containsSelected(inhalersList));
        adapter.setContainsSelection(containsSelected(moreinhalersList) || containsSelected(inhalersList));

        if (containsSelected(moreinhalersList) || containsSelected(inhalersList)){
            rootView.findViewById(R.id.medicine_box_name).setVisibility(View.VISIBLE);
        }else{
            rootView.findViewById(R.id.medicine_box_name).setVisibility(View.GONE);
        }
    }

    private void loadMedicineInfo(){
        selectedTypePos = -1;
        CharSequence[] medicineTypesNames = MedicineTypeAux.getMedicineTypesNames(getActivity());

        for(int i=0 ; i< medicineTypesNames.length; i++){
            if(medicineTypesNames[i].toString().equals(medicineToEdit.getMedicineType().getName())){
                selectedTypePos = i;
                break;
            }
        }

        ((EditText) rootView.findViewById(R.id.medicine_name_editText)).setText(medicineToEdit.getMedicineName());

        if(medicineToEdit.getInhalers()!=null && !medicineToEdit.getInhalers().isEmpty()){

            InhalerDetectionActivity.InhalerType type = MedicineTypeAux.getListMedicineNameRecognitionCodeToTake().get(medicineToEdit.getMedicineName());

            for (Inhaler in : inhalersList){
                if (in.getType().equals(type)){
                    in.setSelected(true);
                }
                adapter.notifyDataSetChanged();
            }

            for (Inhaler in : moreinhalersList){
                if (in.getType().equals(type)){
                    in.setSelected(true);
                }
                adapterMore.notifyDataSetChanged();
            }

            changeListInterface();
            //MedicineInhaler inhaler = medicineToEdit.getInhalers().get(0);

            //rootView.findViewById(R.id.medicine_dosage_box).setVisibility(View.VISIBLE);
            //rootView.findViewById(R.id.medicine_codebar_box).setVisibility(View.VISIBLE);

            //((EditText) rootView.findViewById(R.id.medicine_dosage_editText)).setText(""+inhaler.getDosage());
            //((EditText) rootView.findViewById(R.id.medicine_codebar)).setText(inhaler.getBarcode());
        }

        setSelectedMedicineTypeName(selectedTypePos);
    }

    private boolean checkInfo(MedicineType auxMedicineType, String medicineName, String dosage, String barCode) {
        boolean ok = false;

        if(auxMedicineType==null){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }else if(medicineName == null || medicineName.isEmpty()){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }else {
            /*if(MedicineTypeAux.needsDosesAndBarcode(medicineName,auxMedicineType)){
                if(dosage==null || dosage.isEmpty()){
                    Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
                }*//*else if(barCode==null && barCode.isEmpty()){
                    Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
                }*//*else{
                    ok=true;
                }
            }else{
                ok = true;
            }*/
            ok = true;
        }

        return ok;
    }

    private void setSelectedMedicineTypeName(int posSelected){
        selectedTypePos = posSelected;
        MedicineType aux = MedicineTypeAux.getMedicineTypes(getActivity()).get(selectedTypePos);

        ((TextView) rootView.findViewById(R.id.medicine_type_textView)).setText(aux.getName());

        if(aux.getCode().equals(MedicineTypeAux.TYPE1_CODE)){
            ((LinearLayout) rootView.findViewById(R.id.dropBoxName)).setClickable(true);
            rootView.findViewById(R.id.dropBoxArrow).setVisibility(View.VISIBLE);
            final AutoCompleteTextView autoT = (AutoCompleteTextView) rootView.findViewById(R.id.medicine_name_editText);
            autoT.setVisibility(View.INVISIBLE);
            autoT.setEnabled(false);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, MedicineTypeAux.getMedicinesNeedInlNames(inhalerAux));
            autoT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    verifyMedicineInl();
                    autoT.setVisibility(View.VISIBLE);
                }
            });

            autoT.setAdapter(adapter);

            autoT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        verifyMedicineInl();
                        autoT.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else if(aux.getCode().equals(MedicineTypeAux.TYPE2_CODE)){
            ((LinearLayout) rootView.findViewById(R.id.dropBoxName)).setClickable(false);
            rootView.findViewById(R.id.dropBoxArrow).setVisibility(View.GONE);
            final AutoCompleteTextView autoT = (AutoCompleteTextView) rootView.findViewById(R.id.medicine_name_editText);
            autoT.setVisibility(View.VISIBLE);
            autoT.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, MedicineTypeAux.getMedicinesNeedInlNamesPills());
            autoT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    verifyMedicineInl();
                }
            });

            autoT.setAdapter(adapter);

            autoT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        verifyMedicineInl();
                    }
                }
            });
        }else if(aux.getCode().equals(MedicineTypeAux.TYPE3_CODE)){
            ((LinearLayout) rootView.findViewById(R.id.dropBoxName)).setClickable(false);
            rootView.findViewById(R.id.dropBoxArrow).setVisibility(View.GONE);
            final AutoCompleteTextView autoT = (AutoCompleteTextView) rootView.findViewById(R.id.medicine_name_editText);
            autoT.setVisibility(View.VISIBLE);
            autoT.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, MedicineTypeAux.getMedicinesNeedInlNamesOthers());
            autoT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    verifyMedicineInl();
                }
            });

            autoT.setAdapter(adapter);

            autoT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        verifyMedicineInl();
                    }
                }
            });
        }else{
            rootView.findViewById(R.id.dropBoxArrow).setVisibility(View.GONE);
            AutoCompleteTextView autoT = (AutoCompleteTextView) rootView.findViewById(R.id.medicine_name_editText);
            autoT.setVisibility(View.VISIBLE);
            autoT.setEnabled(true);
            autoT.setAdapter(null);
        }

        verifyMedicineInl();
        changeInterfaceType();
    }

    private void changeInterfaceType(){
        if (selectedTypePos == 0){
            medicineType1.setBackgroundResource(R.drawable.type_medicine_selected);
            medicineType2.setBackgroundResource(R.drawable.type_medicine_unselected);
            medicineType3.setBackgroundResource(R.drawable.type_medicine_unselected);

            rootView.findViewById(R.id.medicine_image_1).setBackgroundResource(R.drawable.asset16);
            rootView.findViewById(R.id.medicine_image_2).setBackgroundResource(R.drawable.asset14);
            rootView.findViewById(R.id.medicine_image_3).setBackgroundResource(R.drawable.asset15);
        }else if (selectedTypePos == 1){
            medicineType1.setBackgroundResource(R.drawable.type_medicine_unselected);
            medicineType2.setBackgroundResource(R.drawable.type_medicine_selected);
            medicineType3.setBackgroundResource(R.drawable.type_medicine_unselected);

            rootView.findViewById(R.id.medicine_image_1).setBackgroundResource(R.drawable.asset13);
            rootView.findViewById(R.id.medicine_image_2).setBackgroundResource(R.drawable.asset17);
            rootView.findViewById(R.id.medicine_image_3).setBackgroundResource(R.drawable.asset15);
        }else{
            medicineType1.setBackgroundResource(R.drawable.type_medicine_unselected);
            medicineType2.setBackgroundResource(R.drawable.type_medicine_unselected);
            medicineType3.setBackgroundResource(R.drawable.type_medicine_selected);

            rootView.findViewById(R.id.medicine_image_1).setBackgroundResource(R.drawable.asset13);
            rootView.findViewById(R.id.medicine_image_2).setBackgroundResource(R.drawable.asset14);
            rootView.findViewById(R.id.medicine_image_3).setBackgroundResource(R.drawable.asset18);
        }
    }

    private void verifyMedicineInl(){
        MedicineType auxMedicineType = selectedTypePos!=-1?MedicineTypeAux.getMedicineTypes(getActivity()).get(selectedTypePos):null;

        String medicineName = ((EditText) rootView.findViewById(R.id.medicine_name_editText)).getText().toString();

        if(needsShowName(auxMedicineType)){
            rootView.findViewById(R.id.medicine_box_name).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.viewInhalers).setVisibility(View.GONE);
        }else{
            rootView.findViewById(R.id.medicine_box_name).setVisibility(View.GONE);
            rootView.findViewById(R.id.viewInhalers).setVisibility(View.VISIBLE);
        }

        if (!medicineName.equals("")){
            rootView.findViewById(R.id.medicine_box_name).setVisibility(View.VISIBLE);
        }


        /*if(needsDosesAndBarcode(medicineName,auxMedicineType)){
            if(flutiformDosageBoxLayout.getVisibility()!=View.VISIBLE) {
                rootView.findViewById(R.id.medicine_dosage_box).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.medicine_codebar_box).setVisibility(View.VISIBLE);

                int pos[] = new int[2];

                rootView.findViewById(R.id.medicine_name_editText).getLocationOnScreen(pos);

                Utils.showFlutiformDialog(getActivity(), getActivity().getFragmentManager(), flutiformDosageEditText.getWidth(), pos[1], new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        flutiformDosageEditText.requestFocus();
                    }
                });
            }else{
                flutiformDosageEditText.requestFocus();
            }
        }else{
            rootView.findViewById(R.id.medicine_dosage_box).setVisibility(View.GONE);
            rootView.findViewById(R.id.medicine_codebar_box).setVisibility(View.INVISIBLE);
        }*/
    }

    public boolean containsSelected(ArrayList<Inhaler> arrayInhalers){
        boolean contains = false;
        for (Inhaler in : arrayInhalers){
            if (in.isSelected()){
                contains = true;
            }
        }
        return contains;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void step1End(MedicineType medicineType, String medicineName, int dosage, String barCode);
    }
}
