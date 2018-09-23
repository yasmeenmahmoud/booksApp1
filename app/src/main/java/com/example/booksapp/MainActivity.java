package com.example.booksapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<BookClass> booklist;
    ListView itemList;
    BookAdapter listadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemList = (ListView) findViewById(R.id.listview);
        booklist = new ArrayList<>();

        EditText search;
        search = (EditText) findViewById(R.id.edt_search);
        beginjsonparsing();
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// make Toast when click
                Toast.makeText(getApplicationContext(), "Position " + position, Toast.LENGTH_LONG).show();
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listadapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void beginjsonparsing() {
        try {

            JSONObject reader = new JSONObject(loadjsonfromassest());
            JSONArray jarray = reader.getJSONArray("rootnode");
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject obj = jarray.getJSONObject(i);

                    String title = obj.getString("title");
                    String auther = obj.getString("auther");
//
                    booklist.add(new BookClass(title, auther));


                    listadapter = new BookAdapter(this, booklist);
                    itemList.setAdapter(listadapter);
                    itemList.setTextFilterEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String loadjsonfromassest() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }


    private class BookAdapter extends ArrayAdapter<BookClass> {
        private ArrayList<BookClass> oldbooklist;
        private ArrayList<BookClass> newbookList;
        private BookFilter filter;


        public BookAdapter(Context context, ArrayList<BookClass> newbookList) {
            super(context, 0, newbookList);
            this.newbookList = new ArrayList<BookClass>();
            this.newbookList.addAll(newbookList);
            this.oldbooklist = new ArrayList<BookClass>();
            this.oldbooklist.addAll(newbookList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if the existing view is being reused, otherwise inflate the view
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.listview, parent, false);
            }

            // Get the current position of Book
            BookClass currentbook = getItem(position);

            // Find the TextView in the list_item.xml (mapping)
            TextView titleBookTextView = (TextView) listItemView.findViewById(R.id.txttitle);
            TextView authorBookTextView = (TextView) listItemView.findViewById(R.id.author);
            titleBookTextView.setText(currentbook.getMtitle());
            authorBookTextView.setText(currentbook.getMauther());
            return listItemView;
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new BookFilter();
            }
            return filter;
        }

        private class BookFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<BookClass> filteredItems = new ArrayList<BookClass>();

                    for (int i = 0, l = oldbooklist.size(); i < l; i++) {
                        BookClass BOOKS = oldbooklist.get(i);
                        if (BOOKS.toString().toLowerCase().contains(constraint))
                            filteredItems.add(BOOKS);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                } else {
                    synchronized (this) {
                        result.values = oldbooklist;
                        result.count = oldbooklist.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                newbookList = (ArrayList<BookClass>) results.values;
                notifyDataSetChanged();
                clear();
                for (int i = 0, l = newbookList.size(); i < l; i++)
                    add(newbookList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }

}

class BookClass {

    private String title;
    private String auther;

    public BookClass(String tittle, String autther) {
        title = tittle;
        auther = autther;
    }


    public String getMtitle() {
        return title;
    }

    public String getMauther() {
        return auther;
    }


}




