package org.ecaib.voicerecorder_emg.ui.main;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ecaib.voicerecorder_emg.R;

import java.io.File;
import java.io.IOException;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private ImageView logo;
    private FloatingActionButton record;
    private FloatingActionButton stop;
    private FloatingActionButton play;

    private String fileName;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        MediaRecorder mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
        mr.setOutputFile("./recording");
        mr.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        try {
            mr.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ActivityCompat.requestPermissions(getActivity(), permissions,
                REQUEST_RECORD_AUDIO_PERMISSION);

        logo = view.findViewById(R.id.logo);
        record = view.findViewById(R.id.record);
        stop = view.findViewById(R.id.stop);
        play = view.findViewById(R.id.play);


        String ruta = getContext().getExternalFilesDir(null).getAbsolutePath();
        fileName = ruta + "/audiorecord.3gp";

        record.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_recording));
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(fileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e("RECORDING",
                            "No es pot iniciar la gravació");
                }

                mRecorder.start();
            }
        });

        play.setOnClickListener(v -> {
            logo.setImageDrawable
                    (getResources().getDrawable(R.drawable.ic_playing));
            if (mRecorder == null && mPlayer == null) {
                mPlayer = new MediaPlayer();

                try {
                    mPlayer.setDataSource(fileName);
                    mPlayer.prepare();
                    mPlayer.start();

                    mPlayer.setOnCompletionListener(mediaPlayer -> {
                        stop.callOnClick();
                    });
                } catch (IOException e) {
                    Log.e("RECORDING", "No es pot iniciar la reproducció");
                }
            }
        });

        stop.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_logo));

            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            } else if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        });




        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(
                    getContext(),
                    "Permission needed",
                    Toast.LENGTH_LONG
            ).show();
            getActivity().finish();
        }
    }

}