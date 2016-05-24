package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends DialogFragment {
    private static final String ARG_SETTINGS = "settings";

    private Settings settings;

    private View view;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    @BindView(R.id.etBeginDate)
    EditText etBeginDate;

    @BindView(R.id.etEndDate)
    EditText etEndDate;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(Settings settings) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SETTINGS, settings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            settings = (Settings) getArguments().getSerializable(ARG_SETTINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpDatePicker(etBeginDate, settings.getBeginDate());
        setUpDatePicker(etEndDate, settings.getBeginDate());
    }

    @OnClick(R.id.btSave)
    public void save(View view) {
        Toast.makeText(view.getContext(), "Save it.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Set up a date picker.
     *
     * @param etDate The EditText to set up a date picker for.
     * @param initDate The date to initialize the date picker with.
     */
    private void setUpDatePicker(final EditText etDate, final Date initDate) {
        etDate.setInputType(InputType.TYPE_NULL);
        etDate.requestFocus();

        Calendar newCalendar = Calendar.getInstance();

        if (initDate != null) {
            newCalendar.setTime(initDate);
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        etDate.setText(dateFormatter.format(newCalendar.getTime()));
    }
}
