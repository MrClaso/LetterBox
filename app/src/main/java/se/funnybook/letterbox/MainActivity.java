package se.funnybook.letterbox;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    //Initialize attributes
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    ItemArrayAdapter itemArrayAdapter;
    // Initializing list view with the custom adapter
    ArrayList<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initializing list view with the custom adapter

        itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
        recyclerView = findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        // Populating list items
        byte[] bytes = new byte[] {-32, 97, -11, 73};
        itemList.add(new Item("Omvägen 12", bytes));
        itemList.add(new Item("Återvändsgränd 7", new byte[] {-116, 90, -126, 82}));
        itemList.add(new Item("Genvägen 5", new byte[] {2, -18, 111, -31, 112, -86, 112}));
        itemList.add(new Item("Bakgatan 9", new byte[] {2, -73, -44, -15, -80, -72, 0}));

        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null) {
            Toast.makeText(this, "NO NFC Capabilities", Toast.LENGTH_SHORT).show();
            finish();
        }
        //Create a PendingIntent object so the Android system can
        //populate it with the details of the tag when it is scanned.
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //On pause stop listening
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;

            byte[] payload = detectTagData(tag).getBytes();
        }
    }

    private String detectTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();

        int i = 0;
        while( i < itemList.size() && !Arrays.equals(itemList.get(i).getId(), id) ) {
            i++;
        }
        if(i == itemList.size()){
            Toast.makeText(this, "Brevlådan finns inte på slingan.",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, itemList.get(i).getName() + " slutförd!",Toast.LENGTH_LONG).show();
// Remove item from list
            itemList.remove(itemList.get(i));
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        }
        return sb.toString();
    }
}