package com.juliendelrio.kitkat4taug.subjects;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juliendelrio.kitkat4taug.R;

public class SubjectfFragmentNewSMSFramework extends AbstractSubjectfFragment {

	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		listView = (ListView) inflater.inflate(R.layout.fragment_newsms_root, container, false);
		View header = inflater.inflate(R.layout.fragment_newsms_header, listView, false);
		listView.addHeaderView(header);
		Uri uriSms = Uri.parse("content://sms/inbox");
		Cursor c = inflater.getContext().getContentResolver().query(uriSms, null, null, null, null);
		listView.setAdapter(new CustomAdapter(inflater.getContext(), c,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		return listView;
	}

	private class CustomAdapter extends CursorAdapter {

		public CustomAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		public CustomAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Holder holder = (Holder) view.getTag();
			setContent(cursor, holder);
		}

		@Override
		protected void onContentChanged() {
			super.onContentChanged();
			getCursor().requery();
			notifyDataSetChanged();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Init view
			View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2,
					parent, false);
			Holder holder = new Holder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			holder.text2 = (TextView) view.findViewById(android.R.id.text2);
			view.setTag(holder);

			// Add content
			setContent(cursor, holder);
			return view;
		}

		private void setContent(Cursor cursor, Holder holder) {
			// Message
			int bodyIndex = cursor.getColumnIndex("body");
			String body = cursor.getString(bodyIndex);
			holder.text2.setText(body);

			// Contact
			String personName = cursor.getString(cursor.getColumnIndex("address"));
			Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
					personName);

			Cursor curPerson = mContext.getContentResolver().query(personUri,
					new String[] { PhoneLookup._ID, PhoneLookup.DISPLAY_NAME }, null, null, null);

			if (curPerson.moveToFirst()) {
				int nameIndex = curPerson.getColumnIndex(PhoneLookup.DISPLAY_NAME);
				personName = curPerson.getString(nameIndex);
			}
			curPerson.close();
			holder.text1.setText(personName);
		}

		private class Holder {
			TextView text1;
			TextView text2;
		}
	}
}
