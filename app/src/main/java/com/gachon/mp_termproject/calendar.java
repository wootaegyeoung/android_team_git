package com.gachon.mp_termproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class calendar extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Schedule> scheduleList;
    ScheduleAdapter scheduleAdapter;
    RecyclerView recyclerView;
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 스토리지

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        CalendarView cv = view.findViewById(R.id.calendarView);

        // RecyclerView와 어댑터 초기화
        scheduleList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.cb_recyclerView_comment);
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDay = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth); // 선택된 날짜를 디비에 저장된 형식과 같게 변경
                scheduleList.clear(); // 기존 데이터를 모두 제거

                db.collection("Contest_Writes")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<Schedule> newSchudleList = new ArrayList<>();
                                // 선택된 날짜와 같은 공모전 찾기
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String fieldValue = document.getString("시작날짜");
                                    // 선택된 날짜와 같은 시작 날짜를 가진 post의 글 제목과 종료 날짜 뽑아오기
                                    if (fieldValue != null && fieldValue.equals(selectedDay)) {
                                        String contest_title = document.getString("제목");
                                        String end_date = document.getString("종료날짜");
                                        // 2023-5-31 이런 형식이므로 [0]: 2023 / [1]: 5 / [2]: 31
                                        String[] start_date_l = selectedDay.split("-");
                                        String[] end_date_l = end_date.split("-");
                                        String contest_period = convertMonth(Integer.parseInt(start_date_l[1])) + " " + start_date_l[2] + " - " + convertMonth(Integer.parseInt(end_date_l[1])) + " " + end_date_l[2];// 예시: 14 May - 16 May


                                        String str_title = document.getString("제목");
                                        String str_content = document.getString("내용");
                                        String str_writer = document.getString("작성자");
                                        String str_image = document.getString("image");
                                        String str_id = document.getString("id");
                                        Map<String, Object> map_reward = (Map<String, Object>) document.getData().get("상금");

                                        if (str_image == null)
                                            return;

                                        // 이미지 불러오기
                                        StorageReference storageRef = storage.getReference();
                                        StorageReference imageRef = storageRef.child(str_image);

                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                // 이미지 다운로드 성공 시 처리할 작업
                                                Post connected_post = new Post(str_title, str_writer, str_content, map_reward, uri, null, str_id, getContext());//연결된 post 객체
                                                Schedule newSchedule = new Schedule(convertMonth(month + 1) , dayOfMonth, contest_title, contest_period, connected_post);
                                                newSchudleList.add(newSchedule);
                                                if (newSchudleList != null) {
                                                    scheduleList.clear(); // 기존 데이터를 모두 제거
                                                    scheduleList.addAll(newSchudleList); // 새로운 데이터로 업데이트
                                                    scheduleAdapter.notifyDataSetChanged(); // 어댑터에 변경 내용 반영
                                                    recyclerView.setAdapter(scheduleAdapter);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                // 이미지 다운로드 실패 시 처리할 작업
                                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            } else {
                            }
                        });
            }
        });

        // 리사이클러뷰 클릭이벤트
        scheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Schedule schedule) {
                Intent intent = new Intent(getContext(), clickBillboard.class);
                intent.putExtra("selectedPost", schedule.getConnected_post());
                startActivity(intent);
            }
        });

        return view;
    }

    // 날짜: 숫자를 문자열로 변환
    private String convertMonth(int n) {
        String month = "";
        switch (n) {
            case 1:
                month = "JAN";
                break;
            case 2:
                month = "FEB";
                break;
            case 3:
                month = "MAR";
                break;
            case 4:
                month = "APR";
                break;
            case 5:
                month = "MAY";
                break;
            case 6:
                month = "JUNE";
                break;
            case 7:
                month = "JULY";
                break;
            case 8:
                month = "AUG";
                break;
            case 9:
                month = "SEP";
                break;
            case 10:
                month = "OCT";
                break;
            case 11:
                month = "NOV";
                break;
            case 12:
                month = "DEC";
                break;
        }

        return month;
    }

}