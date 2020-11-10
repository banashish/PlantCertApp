package com.example.authenticatorapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewPage extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_page);

        fStore = FirebaseFirestore.getInstance();

        this.arrayAdapterListView();
    }

    private void arrayAdapterListView() {

        final List<String> pId = new ArrayList<>();
        final List<String> pName = new ArrayList<>();
        final List<String> pIssue = new ArrayList<>();
        final List<String> pDate = new ArrayList<>();

        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String strDate = dateFormat.format(date).toString();

        fStore.collection("issuedPlants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        pId.add(document.getId());
                        pName.add(document.getString("Name"));
                        pIssue.add(document.getString("issuedTo"));
                        Date dt=document.getDate("issueDate");
                        pDate.add(dateFormat.format(dt).toString());
                    }
                    ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();
                    int titleLen = pName.size();
                    for(int i =0; i < titleLen; i++) {
                        Map<String,Object> listItemMap = new HashMap<String,Object>();
                        listItemMap.put("Name", pName.get(i));
                        listItemMap.put("issuedTo", pIssue.get(i)+" "+pDate.get(i));
                        itemDataList.add(listItemMap);
                    }

                    SimpleAdapter simpleAdapter = new SimpleAdapter(ListViewPage.this, itemDataList, android.R.layout.simple_list_item_2,
                            new String[]{"Name", "issuedTo"}, new int[]{android.R.id.text1, android.R.id.text2});
                    listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(simpleAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                            Object clickItemObj = adapterView.getAdapter().getItem(index);
                            Toast.makeText(ListViewPage.this, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ListViewPage.this, UpdateInfo.class);
                            i.putExtra("item", pId.get(index));
                            startActivity(i);
                        }
                    });

                } else {
                    Log.d("tag", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}