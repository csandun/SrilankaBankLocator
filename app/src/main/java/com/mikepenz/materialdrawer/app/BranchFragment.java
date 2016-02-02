package com.mikepenz.materialdrawer.app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BranchFragment extends Fragment {


    public BranchFragment() {
        // Required empty public constructor
    }

    View v;
    EditText etSearchBranch;
    String brancherNames [] ;

    ListView listView;
    DBHelper helper;
    String[] branchNames;
    BranchAdapter adapter;
    //ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_branck, container, false);
        etSearchBranch = (EditText) v.findViewById(R.id.etSearchBranch);


        helper = new DBHelper(getActivity());


        etSearchBranch.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = etSearchBranch.getRight()
                            - etSearchBranch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // clicked on clear icon
                        etSearchBranch.setText("");
                        return true;
                    }
                }
                return false;
            }
        });


        //get brancher list

        List<Branch> allBranches = helper.getAllBranches();
        branchNames = new String[allBranches.size()];
        int i =0;
        for (Branch branch:allBranches) {
            branchNames[i] = branch.getBranchName();
            i++;
        }

        listView =(ListView) v.findViewById(R.id.listView);





        //adapter = new ArrayAdapter<String>(getActivity(),R.layout.single_row,R.id.tvBranchName,branchNames);
        //adapter = new BranchAdapter(getActivity(),branchNames,allBranches);
        //Log.d("Search", adapter.getItem(0));
        //listView.setAdapter(adapter);
/*

        etSearchBranch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
*/


        adapter = new BranchAdapter(getActivity(),allBranches);
        listView.setAdapter(adapter);


        etSearchBranch.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if(cs.toString().equals("")){
                    Log.i("filter", "empty String");
                    adapter.getFilter().filter("");
                }else {
                    adapter.getFilter().filter(cs.toString());
                    Log.i("filter", cs.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return  v;
    }




}


class BranchAdapter extends BaseAdapter implements Filterable{
    Context context;
    List<Branch> branchList;
    List<Branch> allBranchList;
    BranchFilter branchFilter;


    public BranchAdapter(Context c ,List<Branch> branchList) {
        this.context = c;
        this.branchList = branchList;
        this.allBranchList = branchList;
    }

    @Override
    public int getCount() {
        return branchList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.single_row, null);

        TextView branchCode = (TextView) row.findViewById(R.id.tvBranchCode);
        TextView address = (TextView) row.findViewById(R.id.tvAddress);
        TextView tp = (TextView) row.findViewById(R.id.tvTP);
        TextView branchName = (TextView) row.findViewById(R.id.tvBranchName);
        ImageView logo = (ImageView)row.findViewById(R.id.imageViewLogo);

        Branch branch = branchList.get(position);

        //Log.d("Adapter",branch.getAddress());

        //branchCode.setText("0" + String.valueOf(branch.getBranchCode()) + " - ");
        branchName.setText(branch.getBranchName());
        address.setText(branch.getAddress());
        tp.setText(branch.getTel());
        logo.setImageResource(R.drawable.boc48x48);

        return row;
    }

    @Override
    public Filter getFilter() {
        branchFilter = new BranchFilter();

        return branchFilter;
    }

    private class BranchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Create a FilterResults object
            FilterResults results = new FilterResults();

            // If the constraint (search string/pattern) is null
            // or its length is 0, i.e., its empty then
            // we just set the `values` property to the
            // original contacts list which contains all of them
            if (constraint == null || constraint.length() == 0) {
                results.values = allBranchList;
                results.count = allBranchList.size();


            }
            else {
                // Some search copnstraint has been passed
                // so let's filter accordingly
                ArrayList<Branch> filtedBranches = new ArrayList<Branch>();

                // We'll go through all the contacts and see
                // if they contain the supplied string
                for (Branch b : allBranchList) {
                    if (b.getBranchName().toUpperCase().contains( constraint.toString().toUpperCase() )) {
                        // if `contains` == true then add it
                        // to our filtered list
                        filtedBranches.add(b);
                    }
                }

                // Finally set the filtered values and size/count
                results.values = filtedBranches;
                results.count = filtedBranches.size();
            }

            // Return our FilterResults object
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            branchList = (ArrayList<Branch>) results.values;
            notifyDataSetChanged();
        }
    }























/*
    Context context;
    List<Branch> branchesList;


    public BranchAdapter(Context context, String [] branchNames,List<Branch> branchesList) {
        super(context, R.layout.single_row,R.id.tvBranchName,branchesList);
        this.context = context;
        this.branchesList = branchesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.single_row, null);

        TextView branchCode = (TextView) row.findViewById(R.id.tvBranchCode);
        TextView address = (TextView) row.findViewById(R.id.tvAddress);
        TextView tp = (TextView) row.findViewById(R.id.tvTP);
        TextView branchName = (TextView) row.findViewById(R.id.tvBranchName);
        ImageView logo = (ImageView)row.findViewById(R.id.imageViewLogo);

        Branch branch = branchesList.get(position);

        //Log.d("Adapter",branch.getAddress());

        //branchCode.setText("0" + String.valueOf(branch.getBranchCode()) + " - ");
        branchName.setText(branch.getBranchName());
        address.setText(branch.getAddress());
        tp.setText(branch.getTel());
        logo.setImageResource(R.drawable.boc48x48);

        return row;
    }*/




}
