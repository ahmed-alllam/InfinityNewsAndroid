package com.infinitynews.infinitynews.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.infinitynews.infinitynews.R;

public class TutorialFragment extends Fragment {
    private int imageDrawableId;
    private int titleStringId;
    private int descriptionStringId;

    public TutorialFragment(int fragmentPosition) {
        switch (fragmentPosition) {
            case 0:
                imageDrawableId = R.drawable.ic_news;
                titleStringId = R.string.tutorial1_title;
                descriptionStringId = R.string.tutorial1_description;
                break;
            case 1:
                imageDrawableId = R.drawable.ic_up_to_date;
                titleStringId = R.string.tutorial2_title;
                descriptionStringId = R.string.tutorial2_description;
                break;
            case 2:
                imageDrawableId = R.drawable.ic_search_news;
                titleStringId = R.string.tutorial3_title;
                descriptionStringId = R.string.tutorial3_description;
                break;
            case 3:
                imageDrawableId = R.drawable.ic_notification_bell;
                titleStringId = R.string.tutorial4_title;
                descriptionStringId = R.string.tutorial4_description;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageResource(imageDrawableId);

        TextView title = view.findViewById(R.id.title);
        title.setText(titleStringId);

        TextView description = view.findViewById(R.id.description);
        description.setText(descriptionStringId);
    }
}
