package com.bitnews.bitnews.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bitnews.bitnews.R;

public class TutorialFragment extends Fragment {
    private int image_drawable_id; // todo
    private int title_string_id;
    private int description_string_id;

    public TutorialFragment(int fragmentPosition) {
        System.out.println(fragmentPosition);
        switch (fragmentPosition) {
            case 0:
                image_drawable_id = R.drawable.ic_launcher_background;
                title_string_id = R.string.tutorial1_title;
                description_string_id = R.string.tutorial1_description;
                break;
            case 1:
                image_drawable_id = R.drawable.ic_launcher_background;
                title_string_id = R.string.tutorial2_title;
                description_string_id = R.string.tutorial2_description;
                break;
            case 2:
                image_drawable_id = R.drawable.ic_launcher_background;
                title_string_id = R.string.tutorial3_title;
                description_string_id = R.string.tutorial3_description;
                break;
            case 3:
                image_drawable_id = R.drawable.ic_launcher_background;
                title_string_id = R.string.tutorial4_title;
                description_string_id = R.string.tutorial4_description;
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
        imageView.setImageResource(image_drawable_id);

        TextView title = view.findViewById(R.id.title);
        title.setText(title_string_id);

        TextView description = view.findViewById(R.id.description);
        description.setText(description_string_id);
    }
}
