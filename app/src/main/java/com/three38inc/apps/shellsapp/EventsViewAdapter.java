package com.three38inc.apps.shellsapp;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * Created by jobith on 19/01/17.
 */

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList;
    private Context mContext;

    public EventsViewAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
        final CustomViewHolder viewHolder = new CustomViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext,"clicked="+ getPosition(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, EventDetailsActivity.class);
                i.putExtra("EVENTS", HomeActivity.eventList);
                i.putExtra("pos", viewHolder.getAdapterPosition());
                mContext.startActivity(i);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        //Render image using Picasso library
        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
            Glide.with(mContext)
                .load(feedItem.getThumbnail())
                .error(R.drawable.cheese_1)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.d("XYZ","Image Error");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d("XYZ","Image Loaded - "+model);
                        return false;
                    }
                })
                .placeholder(R.drawable.technics)
                .into(customViewHolder.imageView);
        }

        //Setting text view title
        customViewHolder.textViewNick.setText(feedItem.getNickName());
        customViewHolder.textViewName.setText(feedItem.getName());
        customViewHolder.textViewCaption.setText(feedItem.getCaption());
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textViewName;
        protected TextView textViewNick;
        protected TextView textViewCaption;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textViewNick = (TextView) view.findViewById(R.id.event_nickname);
            this.textViewName = (TextView) view.findViewById(R.id.event_name);
            this.textViewCaption = (TextView) view.findViewById(R.id.event_caption);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContext,"clicked="+ getPosition(), Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(this, EventDetailsActivity.class);
//                    i.putExtra("EVENTS", eventList);
//                    startActivity(i);
                    Snackbar snackbar = Snackbar
                            .make(view, "This is ", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
            });
        }
    }
}
