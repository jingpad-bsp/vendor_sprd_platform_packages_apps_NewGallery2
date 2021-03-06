/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.filtershow.category;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.gallery3d.R;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.filters.FilterMirrorRepresentation;
import com.android.gallery3d.filtershow.filters.FilterRepresentation;
import com.android.gallery3d.filtershow.filters.FilterRotateRepresentation;
import com.android.gallery3d.filtershow.imageshow.MasterImage;
import com.android.gallery3d.filtershow.pipeline.ImagePreset;

import java.util.ArrayList;
import java.util.List;

public class CategoryPanel extends Fragment implements View.OnClickListener {

    public static final String FRAGMENT_TAG = "CategoryPanel";
    private static final String PARAMETER_TAG = "currentPanel";
    private static final String BACKGROUND_COLOR = "#4185F5";

    private int mCurrentAdapter = MainPanel.BORDERS;
    private CategoryAdapter mAdapter;
    private IconView mAddButton;

    private View mEffectView;
    private View mFilterView;

    private List<View> mEffectViewsList = new ArrayList<View>();

    public void setAdapter(int value) {
        mCurrentAdapter = value;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadAdapter(mCurrentAdapter);
    }

    public void loadAdapter(int adapter) {
        FilterShowActivity activity = (FilterShowActivity) getActivity();
        switch (adapter) {
            case MainPanel.LOOKS: {
                mAdapter = activity.getCategoryLooksAdapter();
                if (mAdapter != null) {
                    mAdapter.initializeSelection(MainPanel.LOOKS);
                }
                activity.updateCategories();
                break;
            }
            case MainPanel.BORDERS: {
                mAdapter = activity.getCategoryBordersAdapter();
                if (mAdapter != null) {
                    mAdapter.initializeSelection(MainPanel.BORDERS);
                }
                activity.updateCategories();
                break;
            }
            case MainPanel.GEOMETRY: {
                mAdapter = activity.getCategoryGeometryAdapter();
                if (mAdapter != null) {
                    mAdapter.initializeSelection(MainPanel.GEOMETRY);
                }
                break;
            }
            case MainPanel.FILTERS: {
                mAdapter = activity.getCategoryFiltersAdapter();
                if (mAdapter != null) {
                    mAdapter.initializeSelection(MainPanel.FILTERS);
                }
                break;
            }
            case MainPanel.VERSIONS: {
                mAdapter = activity.getCategoryVersionsAdapter();
                if (mAdapter != null) {
                    mAdapter.initializeSelection(MainPanel.VERSIONS);
                }
                break;
            }
        }
        updateAddButtonVisibility();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(PARAMETER_TAG, mCurrentAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout main = (LinearLayout) inflater.inflate(
                R.layout.filtershow_category_panel_new, container,
                false);

        mEffectView = main.findViewById(R.id.filtershow_effect_items);
        mFilterView = main.findViewById(R.id.filtershow_items);
        Log.d(FRAGMENT_TAG, "onCreateView mEffectView=" + mEffectView + ", mFilterView=" + mFilterView, new Throwable());
        int selectedPanel = mCurrentAdapter;
        if (savedInstanceState != null) {
            selectedPanel = savedInstanceState.getInt(PARAMETER_TAG);
            loadAdapter(selectedPanel);
            mCurrentAdapter = selectedPanel;
        }

        if (selectedPanel == MainPanel.FILTERS) {
            mEffectView.setVisibility(View.VISIBLE);
            mFilterView.setVisibility(View.GONE);
            setEffectClickListeners(main);
        } else {
            mEffectView.setVisibility(View.GONE);
            mFilterView.setVisibility(View.VISIBLE);
        }

        View panelView = main.findViewById(R.id.listItems);
        if (panelView instanceof CategoryTrack) {
            CategoryTrack panel = (CategoryTrack) panelView;
            if (mAdapter != null) {
                mAdapter.setOrientation(CategoryView.HORIZONTAL);
                panel.setAdapter(mAdapter);
                mAdapter.setContainer(panel);
            }
        } else if (mAdapter != null) {
            ListView panel = main.findViewById(R.id.listItems);
            panel.setAdapter(mAdapter);
            mAdapter.setContainer(panel);
        }

        mAddButton = main.findViewById(R.id.addButton);
        if (mAddButton != null) {
            mAddButton.setOnClickListener(this);
            updateAddButtonVisibility();
        }
        return main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                FilterShowActivity activity = (FilterShowActivity) getActivity();
                activity.addCurrentVersion();
                break;
        }
    }

    public void updateAddButtonVisibility() {
        // SPRD:bug 540329 CategoryAdapter maybe null
        if (mAddButton == null || mAdapter == null) {
            return;
        }
        FilterShowActivity activity = (FilterShowActivity) getActivity();
        if (activity.isShowingImageStatePanel() && mAdapter.showAddButton()) {
            mAddButton.setVisibility(View.VISIBLE);
            if (mAdapter != null) {
                mAddButton.setText(mAdapter.getAddButtonText());
            }
        } else {
            mAddButton.setVisibility(View.GONE);
        }
    }

    private void clearEffectViewsColor() {
        for (View view : mEffectViewsList) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setEffectClickListeners(View main) {
        mEffectViewsList.clear();
        View exposure = main.findViewById(R.id.filtershow_effect_exposure);
        mEffectViewsList.add(exposure);
        exposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onClickEffectWithAction(mAdapter.getItem(1));//??????
                clearEffectViewsColor();
                arg0.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
            }
        });

        View sharpen = main.findViewById(R.id.filtershow_effect_sharpen);
        mEffectViewsList.add(sharpen);
        sharpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onClickEffectWithAction(mAdapter.getItem(8));//??????
                clearEffectViewsColor();
                arg0.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
            }
        });

        View contrast = main.findViewById(R.id.filtershow_effect_contrast);
        mEffectViewsList.add(contrast);
        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onClickEffectWithAction(mAdapter.getItem(4));//?????????
                clearEffectViewsColor();
                arg0.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
            }
        });

        View vignetter = main.findViewById(R.id.filtershow_effect_vignetter);
        mEffectViewsList.add(vignetter);
        vignetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onClickEffectWithAction(mAdapter.getItem(2));//??????
                clearEffectViewsColor();
                arg0.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
            }
        });

        View saturation = main.findViewById(R.id.filtershow_effect_saturation);
        mEffectViewsList.add(saturation);
        saturation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onClickEffectWithAction(mAdapter.getItem(11));//?????????
                clearEffectViewsColor();
                arg0.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
            }
        });
    }

    private void onClickEffectWithAction(Action action) {
        if (MasterImage.getImage().getPreset() != null) {
            FilterShowActivity activity = (FilterShowActivity) getActivity();
            FilterRepresentation actionRep = action.getRepresentation();
            if (actionRep instanceof FilterRotateRepresentation
                    || actionRep instanceof FilterMirrorRepresentation) {
                ImagePreset copy = new ImagePreset(MasterImage.getImage().getPreset());
                FilterRepresentation rep = copy.getRepresentation(actionRep);
                if (rep != null) {
                    activity.showRepresentation(rep);
                } else {
                    actionRep.resetRepresentation();
                    activity.showRepresentation(actionRep);
                }
            } else {
                activity.showRepresentation(actionRep);
            }
        }
    }
}
