package com.example.pruebasenal;

import android.app.Activity;
import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new BlueToothFragment())
                    .commit();
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BlueToothFragment extends ListFragment {

        private static final String TAG = BlueToothFragment.class.getName();

        private BluetoothAdapter bTAdapter ;

        private ArrayAdapter<Dispositivo> adapter;
        private ArrayList<Dispositivo> dispositivos;
        private ArrayList<Dispositivo> bacons;


        public BlueToothFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            // habilita el multicheck
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            bTAdapter = BluetoothAdapter.getDefaultAdapter();


            // Si empiezo una búsqueda de dispositivos bluetooth, estoy interesado
            getActivity().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));


            // lista vacia de dispositivos
            dispositivos = new ArrayList<>();

            // lista de bacons
            bacons = new ArrayList<>();

            bacons.add(new Dispositivo("ZTE TARA 3G",-51,40.416775400000000000,-3.703790199999957600));
            bacons.add(new Dispositivo("BACON 2",-52,40.416775400000000000,-3.703790199999957600));
            bacons.add(new Dispositivo("BACON 3",-53,40.416775400000000000,-3.703790199999957600));


            // adaptador que hace de controlador para una lista de tipo check
            adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_checked,dispositivos);

            setListAdapter(adapter);

            setHasOptionsMenu(true);


            return rootView;
        }



        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_main, menu);

        }

        // Opciones del menu
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            //noinspection SimplifiableIfStatement
            int id = item.getItemId();
            if (id == R.id.buscar) {
                    // buscando dispositivos bluetooth
                    bTAdapter.startDiscovery();
                return true;
            }
            if (id == R.id.triangular) {

                 triangular();

                return true;
            }
            return true;
        }

        private void triangular() {
            double latitud  = Trilateration.getLatitude(bacons.get(0),bacons.get(1),bacons.get(2));
            double longitud =  Trilateration.getLongitude(bacons.get(0), bacons.get(1), bacons.get(2));

            Log.d(TAG,"latitud="+latitud+",longitud="+longitud);

            //latitud = 40.416775400000000000;
            //longitud = -3.703790199999957600;

            Uri uri = Uri.parse("geo:0,0?q="+latitud+","+longitud+"(bacon)");

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);



        }



        /**
         * Evento que se ejecuta al hacer clic en un elemento de la lista.
         * @param l
         * @param v
         * @param position
         * @param id
         */
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);


        }

        private final BroadcastReceiver receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                    double rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);


                        for(Dispositivo b:bacons){
                            if( name.equals(b.getNombre())){
                                b.setRssi(rssi);
                                adapter.add( new Dispositivo (name,rssi));
                            }

                    }

                    Log.d(TAG,name+" "+rssi);

                }

            }
        };


    }
}
