package com.example.testapp;


import android.Manifest;
import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.konka.media.PCMRecorder;
import com.konka.sdk.SeeulinkSDK;
import com.konka.sdk.bean.AgentUser;
import com.konka.sdk.bean.MediaInfo;
import com.konka.sdk.callback.MeetingEvent;
import com.konka.sdk.weight.RTCView;
import com.konka.utils.Log;
import com.konka.webrtc.CameraUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView resultTv;
    private Button startMeeting, finishMeeting, openCamera, closeCamera, renderVideo, pushAudio, regiesterUser, createMeeting, enableAudio, disableAudio;
    private ViewGroup showLayout;
    private List<AgentUser> users;
    private PCMRecorder pcmRecorder;
    private PopupWindow userWindows;

    //==============test=====================
    private String userKey = "11113";
    private String userId;
    private String roomCode;
    private com.example.seeulinkdemo.UserAdapter userAdapter;
    private ListView userView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        SeeulinkSDK.getInstance().init(getApplication());
        SeeulinkSDK.getInstance().regiestMeetingEvent(new DemoMeetingEvent());

        showLayout = findViewById(R.id.show_layout);
        resultTv = findViewById(R.id.result);
        regiesterUser = findViewById(R.id.regiester_user);

        startMeeting = findViewById(R.id.start_meeting);
        createMeeting = findViewById(R.id.create_meeting);
        finishMeeting = findViewById(R.id.finish_meeting);
        openCamera = findViewById(R.id.open_camera);
        closeCamera = findViewById(R.id.close_camera);
        renderVideo = findViewById(R.id.render_video);
        pushAudio = findViewById(R.id.push_audio);
        enableAudio = findViewById(R.id.enable_audio);
        disableAudio = findViewById(R.id.disable_audio);


        resultTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        regiesterUser.setOnClickListener(this);
        createMeeting.setOnClickListener(this);
        startMeeting.setOnClickListener(this);
        finishMeeting.setOnClickListener(this);
        openCamera.setOnClickListener(this);
        closeCamera.setOnClickListener(this);
        renderVideo.setOnClickListener(this);
        pushAudio.setOnClickListener(this);
        enableAudio.setOnClickListener(this);
        disableAudio.setOnClickListener(this);

        userAdapter = new com.example.seeulinkdemo.UserAdapter(users, this);
        userView = new ListView(this);
        userView.setAdapter(userAdapter);
        userWindows = new PopupWindow(this);
        userWindows.setContentView(userView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SeeulinkSDK.getInstance().release();
    }

    public void onResult(String result) {
        runOnUiThread(() -> resultTv.append(getTime() + result + "\n"));

    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
        String sim = dateFormat.format(date);
        return sim;
    }

    private boolean isCapturingAudio = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regiester_user:
                SeeulinkSDK.getInstance().regiesterUser(userKey);
                break;
            case R.id.create_meeting:
                SeeulinkSDK.getInstance().createMeeting(userId);
                break;
            case R.id.start_meeting:
                SeeulinkSDK.getInstance().startMeeting(userId, roomCode);
                break;
            case R.id.finish_meeting:
                SeeulinkSDK.getInstance().closeMeeting();

                break;
            case R.id.open_camera:
                String json = SeeulinkSDK.getInstance().queryMediaEnable(userId);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.has("video")) {
                        boolean isEnable = jsonObject.getBoolean("video");
                        if (!isEnable) {
                            String[] cameras = CameraUtils.getCameraNames(getApplicationContext());
                            if (cameras.length > 0) {
                                SeeulinkSDK.getInstance().openLocalVideo(cameras[0]);
                            }
                        } else {
                            onResult("用户:" + userId + "视频已打开");
                        }
                    } else {
                        onResult("用户无音视频");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.close_camera:

                String json1 = SeeulinkSDK.getInstance().queryMediaEnable(userId);
                try {
                    JSONObject jsonObject = new JSONObject(json1);
                    if (jsonObject.has("video")) {
                        boolean isEnable = jsonObject.getBoolean("video");
                        if (isEnable) {
                            SeeulinkSDK.getInstance().closeLocalVideo();
                            showLayout.removeAllViews();
                        } else {
                            onResult("用户:" + userId + "视频已关闭");
                        }
                    } else {
                        onResult("用户无音视频");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
            case R.id.render_video:
                showUsers(v, 0);
                break;
            case R.id.push_audio:
                if (isCapturingAudio) {
                    SeeulinkSDK.getInstance().closeLocalAudio();
                } else {
                    SeeulinkSDK.getInstance().openLocalAudio();
                }
                break;
            case R.id.enable_audio:
                showUsers(v, 1);
                break;
            case R.id.disable_audio:
                showUsers(v, 2);
                break;
        }
    }


    private void showUsers(View view, int type) {
        if (userWindows != null && userWindows.isShowing()) {
            userWindows.dismiss();
        } else {

            userView.setOnItemClickListener((parent, view1, position, id) -> {
                switch (type) {
                    case 0:
                        String json1 = SeeulinkSDK.getInstance().queryMediaEnable(users.get(position).userId);
                        try {
                            JSONObject jsonObject = new JSONObject(json1);
                            if (jsonObject.has("video")) {
                                boolean isEnable = jsonObject.getBoolean("video");
                                if (isEnable) {
                                    renderVideo(users.get(position).userId);
                                } else {
                                    onResult("用户:" + userId + "视频已关闭");
                                }
                            } else {
                                onResult("用户无音视频");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 1:
                        String json2 = SeeulinkSDK.getInstance().queryMediaEnable(users.get(position).userId);
                        try {
                            JSONObject jsonObject = new JSONObject(json2);
                            if (jsonObject.has("audio")) {
                                boolean isEnable = jsonObject.getBoolean("audio");
                                if (!isEnable) {
                                    SeeulinkSDK.getInstance().enableRemoteAudio(users.get(position).userId, true);
                                } else {
                                    onResult("用户:" + userId + "音频已打开");
                                }
                            } else {
                                onResult("用户无音视频");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 2:
                        String json3 = SeeulinkSDK.getInstance().queryMediaEnable(users.get(position).userId);
                        Log.d("::::::::data:" + json3);
                        try {
                            JSONObject jsonObject = new JSONObject(json3);
                            if (jsonObject.has("audio")) {
                                boolean isEnable = jsonObject.getBoolean("audio");
                                if (isEnable) {
                                    SeeulinkSDK.getInstance().enableRemoteAudio(users.get(position).userId, false);
                                } else {
                                    onResult("用户:" + userId + "音频已关闭");
                                }
                            } else {
                                onResult("用户无音视频");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                userWindows.dismiss();
            });
            userAdapter.setUsers(users);

            userWindows.showAsDropDown(view, 0, (int) (-1.5 * view.getMeasuredHeight()));
        }
    }

    public void renderVideo(String userId) {
        RTCView rtcView = new RTCView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int) getResources().getDimension(R.dimen.video_width), (int) getResources().getDimension(R.dimen.video_height));
        rtcView.setLayoutParams(layoutParams);
        showLayout.addView(rtcView);
        SeeulinkSDK.getInstance().renderVideo(userId, rtcView);

    }


    private class DemoMeetingEvent implements MeetingEvent {

        @Override
        public void onRegiestUser(int code, String info) {
            switch (code) {
                case SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(info);
                        userId = jsonObject.getString("userId");
                        onResult("注册用户成功:" + info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    onResult("注册用户失败.code:" + code + ",info:" + info);
                    break;
            }
        }

        @Override
        public void onCreateMeeting(int code, String info) {
            switch (code) {
                case SUCCESS:
                    onResult("创建会议成功:" + info);
                    roomCode = info;
                    MediaInfo mediaInfo = new MediaInfo();
                    mediaInfo.sampleRate = 48000;
                    mediaInfo.channelsConfig = AudioFormat.CHANNEL_IN_MONO;
                    SeeulinkSDK.getInstance().configMediaInfo(mediaInfo);
                    break;
                default:
                    onResult("创建会议失败.code:" + code + ",info:" + info);
                    break;
            }
        }

        @Override
        public void onStartMeeting(List<AgentUser> users) {
            onResult("开始会议");
            MainActivity.this.users = users;
        }

        @Override
        public void onCloseMeeting() {
            onResult("结束会议");
            runOnUiThread(() -> showLayout.removeAllViews());
        }

        @Override
        public void onMeetingEvent(int code, String info) {
            switch (code) {
                case MEETING_NO_INFO:
                    onResult("未获取到房间信息");
                    break;
                case MEETING_NO_CONNECTING:
                    onResult("未加入会议");
                    break;
                case MEETING_CONNECTING:
                    onResult("会议正在进行");
                    break;
                case MEETING_NO_VIDEO:
                    onResult("用户无视频:" + info);
                    break;
                case MEETING_NO_AUDIO:
                    onResult("用户无音频:" + info);
                    break;
                case MEETING_NO_USER:
                    onResult("找不到用户");
                    break;
            }
        }

        @Override
        public void onDisconnect() {
            onResult("连接已断开");
            runOnUiThread(() -> showLayout.removeAllViews());
        }

        @Override
        public void onNewConn(AgentUser agentUser) {
            onResult("新用户连接" + agentUser.userId);
        }

        @Override
        public void onConnLeft(AgentUser agentUser) {
            onResult("用户断开连接" + agentUser.userId);
        }

        @Override
        public void onSignalStart() {
            onResult("媒体通道开启");
            if (pcmRecorder == null) {
                pcmRecorder = new PCMRecorder();
                pcmRecorder.setRecordCallBack((data, size, sampleRate, channels) -> {
                    SeeulinkSDK.getInstance().pushExternalAudioData(data, size);
                });
            }
//            SeeulinkSDK.getInstance().closeAudio();
            pcmRecorder.start();
        }

        @Override
        public void onMediaChange(String userId, int mediaType, boolean enable) {
            switch (mediaType) {
                case MEDIA_TYPE_AUDIO:
                    if (enable) {
                        onResult("用户" + userId + "的音频打开");
                    } else {
                        onResult("用户" + userId + "的音频关闭");
                    }
                    if (userId.equals(MainActivity.this.userId)) {
                        isCapturingAudio = enable;
                        runOnUiThread(() -> pushAudio.setText((enable ? "关闭音频" : "打开音频")));
                    }
                    break;
                case MEDIA_TYPE_VIDEO:
                    if (enable) {
                        onResult("用户" + userId + "的视频打开");
                    } else {
                        onResult("用户" + userId + "的视频关闭");
                    }
                    break;
            }
        }

        @TargetApi(23)
        @Override
        public void onCameraInfo(int code, String des) {
            switch (code) {
                case CAMERA_NO_PERMISSION:
                    onResult("应用无相机权限");
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    break;
                case CAMERA_ERROR_:
                    onResult("相机异常");
                    break;
                case CAMERA_OPEN:
                    onResult("相机打开");
                    break;
                case CAMERA_CLOSE:
                    onResult("相机关闭");
                    break;
            }

        }

    }
}
