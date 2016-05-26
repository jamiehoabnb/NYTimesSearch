package com.codepath.nytimessearch.ui.list;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    @BindView(R.id.spSortOrder)
    Spinner spSortOrder;

    @BindView(R.id.cbBusiness)
    CheckBox cbBusiness;

    @BindView(R.id.cbForeign)
    CheckBox cbForeign;

    @BindView(R.id.cbMovies)
    CheckBox cbMovies;

    @BindView(R.id.cbSports)
    CheckBox cbSports;

    @BindView(R.id.cbStyles)
    CheckBox cbStyles;

    @BindView(R.id.cbTravel)
    CheckBox cbTravel;

    public interface SettingsDialogListener {
        void onFinishDialog(Settings settings);
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(Settings settings) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SETTINGS, Parcels.wrap(settings));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            settings = (Settings) Parcels.unwrap(getArguments().getParcelable(ARG_SETTINGS));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btSave)
    public void save(View view) {
        Settings newSettings = new Settings();

        if (etBeginDate.getText().toString().length() > 0) {
            try {
                newSettings.setBeginDate(dateFormatter.parse(etBeginDate.getText().toString()));
            } catch (ParseException e) {
                Log.e("ERROR", "Error parsing date " + etBeginDate.getText().toString());
            }
        }

        if (etEndDate.getText().toString().length() > 0) {
            try {
                newSettings.setEndDate(dateFormatter.parse(etEndDate.getText().toString()));
            } catch (ParseException e) {
                Log.e("ERROR", "Error parsing date " + etEndDate.getText().toString());
            }
        }

        if (spSortOrder.getSelectedItemPosition() != 0) {
            newSettings.setSortOrder(Settings.SortOrder.valueOf(spSortOrder.getSelectedItem().toString().toLowerCase()));
        }

        List<Settings.NewsDesk> newsDesks = new ArrayList<>();
        if (cbBusiness.isChecked()) {
            newsDesks.add(Settings.NewsDesk.Business);
        }

        if (cbForeign.isChecked()) {
            newsDesks.add(Settings.NewsDesk.Foreign);
        }

        if (cbMovies.isChecked()) {
            newsDesks.add(Settings.NewsDesk.Movies);
        }

        if (cbSports.isChecked()) {
            newsDesks.add(Settings.NewsDesk.Sports);
        }

        if (cbStyles.isChecked()) {
            newsDesks.add(Settings.NewsDesk.Styles);
        }

        if (cbTravel.isChecked()) {
            newsDesks.add(Settings.NewsDesk.Travel);
        }

        newSettings.setNewsDesks(newsDesks);

        ((SettingsDialogListener) getActivity()).onFinishDialog(newSettings);
        dismiss();
    }

    @OnClick(R.id.btCancel)
    public void cancel(View view) {
        dismiss();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpDatePicker(etBeginDate, settings.getBeginDate());
        setUpDatePicker(etEndDate, settings.getBeginDate());

        if (settings.getSortOrder() != null) {
            spSortOrder.setSelection(settings.getSortOrder() == Settings.SortOrder.newest ? 1 : 2);
        }

        if (settings.getNewsDesks() != null && ! settings.getNewsDesks().isEmpty()) {
            for (Settings.NewsDesk newsDesk: settings.getNewsDesks()) {
                if (newsDesk == Settings.NewsDesk.Business) {
                    cbBusiness.setChecked(true);
                } else if (newsDesk == Settings.NewsDesk.Foreign) {
                    cbForeign.setChecked(true);
                } else if (newsDesk == Settings.NewsDesk.Movies) {
                    cbMovies.setChecked(true);
                } else if (newsDesk == Settings.NewsDesk.Sports) {
                    cbSports.setChecked(true);
                } else if (newsDesk == Settings.NewsDesk.Styles) {
                    cbStyles.setChecked(true);
                } else if (newsDesk == Settings.NewsDesk.Travel) {
                    cbTravel.setChecked(true);
                } else {
                    Log.e("ERROR", "Unhandled news desk type " + newsDesk);
                }
            }
        }
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
            etDate.setText(dateFormatter.format(newCalendar.getTime()));
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
    }
}
