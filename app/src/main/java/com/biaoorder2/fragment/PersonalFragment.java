package com.biaoorder2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.biaoorder2.R;
import com.biaoorder2.activity.LoginActivity;
import com.biaoorder2.ui.ReToast;

public class PersonalFragment extends Fragment {
    private Button exit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        exit = view.findViewById(R.id.btn_exit);
        exit.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            ReToast.show(requireActivity(),"已退出登录...");
            requireActivity().finish();
        });
    }
}
