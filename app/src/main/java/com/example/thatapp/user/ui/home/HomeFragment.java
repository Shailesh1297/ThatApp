package com.example.thatapp.user.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thatapp.R;
import com.example.thatapp.connection.Connection;
import com.example.thatapp.login.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    List<Product> productList;
    RecyclerView recyclerView;
    TextView collegeName;
    LinearLayout ll;
    String college;
    int collegId,userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        //strict mode
        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().build();
        StrictMode.setThreadPolicy(threadPolicy);
        recyclerView=(RecyclerView)view.findViewById(R.id.home_recycler_view);
        collegeName=(TextView)view.findViewById(R.id.home_college_name);
        ll=(LinearLayout)view.findViewById(R.id.home_filters);
        ll.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        userId=getUserId();
        getCollege(collegId);
        collegeName.setText(college);

        if(allProducts(userId))
        {
            RecyclerViewAdapter rva=new RecyclerViewAdapter(getActivity().getBaseContext(),productList);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getBaseContext(),3));
            recyclerView.setAdapter(rva);
        }



    }

    public boolean allProducts(int user_id)
    {
        StringBuilder stringBuilder=new StringBuilder();
        String line="";
        String page="all_products";
        try{
            HttpURLConnection conn= Connection.createConnection();
            //output
            OutputStream outputStream=conn.getOutputStream();
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream,"UTF-8");
            BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
            String dataEncode= URLEncoder.encode("page","UTF-8")+"="+URLEncoder.encode(page,"UTF-8")+
                    "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(user_id), "UTF-8");
            bufferedWriter.write(dataEncode);
            bufferedWriter.close();
            outputStreamWriter.close();
            outputStream.close();

            //input
            InputStream inputStream=conn.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuilder.append(line+"\n");
            }

            String dataDecode=stringBuilder.toString().trim();
            JSONObject jsonObject=new JSONObject(dataDecode);

            //  JSONObject jsonData=jsonObject.getJSONObject("flag");
            int flag=jsonObject.getInt("flag");
            if(flag==1)
            {
                JSONArray jsonArray=jsonObject.getJSONArray("0");
                int length=jsonArray.length();

                productList=new ArrayList<>();
                for(int i=0;i<length;i++)
                {
                    jsonObject=jsonArray.getJSONObject(i);
                    Product pdt=new Product();
                    pdt.setId(jsonObject.getInt("item_id"));
                    pdt.setTitle(jsonObject.getString("item_name"));
                    pdt.setPrice(jsonObject.getString("item_price"));
                    pdt.setCategory(jsonObject.getString("category"));
                    pdt.setDescription(jsonObject.getString("description"));
                    pdt.setThumbnail(jsonObject.getString("image"));
                    productList.add(pdt);
                }
                conn.disconnect();
                return true;

            }

            conn.disconnect();

        }catch (Exception e)
        {
            Log.d("All products",e.toString());
        }
        return false;
    }

    //get College Name

    private void getCollege(int college_id)
    {

        StringBuilder stringBuilder=new StringBuilder();
        String line="";
        String page="college";
        try{
            HttpURLConnection conn= Connection.createConnection();
            //output
            OutputStream outputStream=conn.getOutputStream();
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream,"UTF-8");
            BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
            String dataEncode= URLEncoder.encode("page","UTF-8")+"="+URLEncoder.encode(page,"UTF-8")+
                    "&" + URLEncoder.encode("college_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(college_id), "UTF-8");
            bufferedWriter.write(dataEncode);
            bufferedWriter.close();
            outputStreamWriter.close();
            outputStream.close();

            //input
            InputStream inputStream=conn.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuilder.append(line+"\n");
            }

            String dataDecode=stringBuilder.toString().trim();
            JSONArray jsonArray=new JSONArray(dataDecode);
            int length=jsonArray.length();
            JSONObject jsonObject=null;

            for(int i=0;i<length;i++)
            {
                jsonObject=jsonArray.getJSONObject(i);
                college=jsonObject.getString("college_name");
            }

            conn.disconnect();
        }catch (Exception e)
        {
            Log.d("User College",e.toString());
        }

    }

    private int getUserId()
    {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("USER_KEY",0);
        String userJson=sharedPreferences.getString("user",null);
        if(userJson!=null)
        {
            Gson gson=new Gson();
            User user=gson.fromJson(userJson,User.class);
            collegId=user.getCollegeid();
            return user.getUserid();
        }
        return 0;
    }
}
