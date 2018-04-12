package com.adamapps.aiteo.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.aiteo.NewPost;
import com.adamapps.aiteo.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;
import com.mypopsy.widget.FloatingSearchView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    FloatingSearchView floatingSearchView;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        floatingSearchView = v.findViewById(R.id.search);
        floatingActionButton = v.findViewById(R.id.new_post_button);
        recyclerView = v.findViewById(R.id.home_post_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new HomeAdapter());
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            float height;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                float height = motionEvent.getY();
                if(action==MotionEvent.ACTION_DOWN){
                    this.height = height;
                }else if(action == MotionEvent.ACTION_UP){
                    if(this.height <height) {
                        floatingSearchView.setVisibility(View.VISIBLE);
                        //Toast.makeText(getActivity(), "Scrolling Up", Toast.LENGTH_SHORT).show();
                    }else if(this.height>height){
                        floatingSearchView.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), "Scrolling Down", Toast.LENGTH_SHORT).show();

                    }

                }
                return false;

            }

        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewPost.class));
            }
        });

        return v;
    }

    public class HomeAdapter extends RecyclerView.Adapter<HomeHolder>{
        String[] dummy = {"adam","john","paul","peter","tom",
                "adam","john","paul","peter","tom",
                "adam","john","paul","peter","tom",
                "adam","john","paul","peter","tom"};

        @NonNull
        @Override
        public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v=  LayoutInflater.from(getActivity()).inflate(R.layout.single_home_item,parent,false);
            return new HomeHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
            holder.post_like_count.setText(dummy[position]);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
    public class HomeHolder extends RecyclerView.ViewHolder{
        TextView post_like_count;
        View mView;

        public HomeHolder(View itemView) {
            super(itemView);
            mView = itemView;
            post_like_count = itemView.findViewById(R.id.post_like_count);
        }
    }

}
