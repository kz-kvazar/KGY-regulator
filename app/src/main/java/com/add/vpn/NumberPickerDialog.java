package com.add.vpn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class NumberPickerDialog extends DialogFragment {

    private static final int MIN_VALUE = 1100;
    private static final int MAX_VALUE = 1560;
    private static final int STEP = 20;
    private OnNumberSetListener listener;

     @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(MIN_VALUE / STEP);
        numberPicker.setMaxValue(MAX_VALUE / STEP);
        numberPicker.setDisplayedValues(getDisplayedValues());
        numberPicker.setWrapSelectorWheel(true);
         EditText editText = new EditText(getContext());
         editText.setInputType(InputType.TYPE_CLASS_NUMBER);
         editText.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(4)});

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.set_max_power)
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {
                    int selectedValue = 1520;
                    try {
                        selectedValue = (Integer.parseInt(editText.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireActivity(), R.string.max_power_Error, Toast.LENGTH_LONG).show();
                    }

                    if (selectedValue>1560) selectedValue = 1560;
                    if (selectedValue<900) selectedValue = 900;

                    Toast.makeText(requireActivity(),(requireActivity().getString(R.string.max_power, String.valueOf(selectedValue))), Toast.LENGTH_SHORT).show();
                   // menuItem.setTitle(getText(R.string.max_power) + " " + selectedValue + "кВт");
                    if (listener != null) {
                        listener.onNumberSet(selectedValue);
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();
    }

    private String[] getDisplayedValues() {
        int count = (MAX_VALUE - MIN_VALUE) / STEP + 1;
        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = String.valueOf(MIN_VALUE + (i * STEP));
        }
        return values;
    }

    public void setOnNumberSetListener(OnNumberSetListener listener) {
        this.listener = listener;
    }

    public interface OnNumberSetListener {
        void onNumberSet(int value);
    }
}
