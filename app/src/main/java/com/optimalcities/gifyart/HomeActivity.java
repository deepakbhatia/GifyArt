package com.optimalcities.gifyart;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Collections> feedsList;
    private RecyclerView mRecyclerView;
    private CollectionsAdapter adapter;

    private void setCollections()
    {
        feedsList = new ArrayList<Collections>();

        Collections collections_ww1 = new Collections();
        collections_ww1.setTitle("World War I 1914-1918");
        collections_ww1.setImageUrl("http://digicontent.snk.sk/content/photos/_Fotky__Ivan_Stodola/2760-1954_1.jpg");
        collections_ww1.setSearchUrl("http://www.europeana.eu/api/v2/search.json?wskey=rDqS8g83S&query=europeana_collectionName%3A9200323*&start=1&rows=96&profile=rich&reusability=open");

        new GetContentTask(this).execute(collections_ww1.getTitle(),collections_ww1.getSearchUrl());


        Collections collections_design = new Collections();
        collections_design.setTitle("Design");
        collections_design.setImageUrl("http://img.museumrotterdam.nl/700/9934_1.jpg");
        collections_design.setSearchUrl("http://www.europeana.eu/api/v2/search.json?wskey=rDqS8g83S&query=europeana_collectionName%3A2021609*&start=1&rows=96&profile=rich&reusability=open");
        new GetContentTask(this).execute(collections_design.getTitle(),collections_design.getSearchUrl());

        Collections collections_arts = new Collections();
        collections_arts.setTitle("Arts");
        collections_arts.setImageUrl("http://www.webumenia.sk/cedvuweb/image/SVK_SNG.P_985.jpeg?id=SVK:SNG.P_985");
        collections_arts.setSearchUrl("http://www.europeana.eu/api/v2/search.json?wskey=rDqS8g83S&query=europeana_collectionName%3A2023831_AG-EU_LinkedHeritage_Rybinsk&qf=painting&start=1&rows=96&profile=rich&reusability=open");
        new GetContentTask(this).execute(collections_arts.getTitle(),collections_arts.getSearchUrl());

        Collections collections_maps = new Collections();
        collections_maps.setTitle("Maps");
        collections_maps.setImageUrl("http://bvpb.mcu.es/i18n/catalogo_imagenes/imagen_id.cmd?idImagen=4140587");
        //collections_maps.setSearchUrl("http://www.europeana.eu/api/v2/search.json?wskey=rDqS8g83S&query=DATA_PROVIDER%3A\'Biblioteca+Virtual+del+Patrimonio+Bibliográfico\'&qf=europeana_collectionName%3A2022701d_Ag_ES_Hispana_esegen&qf=TYPE%3AIMAGE&start=1&rows=96&profile=rich&reusability=open");
        collections_maps.setSearchUrl("http://www.europeana.eu/api/v2/search.json?wskey=rDqS8g83S&query=DATA_PROVIDER%3A%22Catálogo+Colectivo+de+la+Red+de+Bibliotecas+de+los+Archivos+Estatales%22&qf=europeana_collectionName%3A2022701b_Ag_ES_Hispana_esegen1&qf=TYPE%3AIMAGE&start=1&rows=96&profile=rich&reusability=open");

        new GetContentTask(this).execute(collections_maps.getTitle(),collections_maps.getSearchUrl());

        Collections collections_fashion = new Collections();
        collections_fashion.setTitle("Fashion");
        collections_fashion.setImageUrl("http://repos.europeanafashion.eu/momu/images/LMO-jaargang-04-18381216_005.jpg");

        collections_fashion.setSearchUrl("http://www.europeana.eu/api/v2/search.json?wskey=rDqS8g83S&query=europeana_collectionName%3A2048211*&qf=RIGHTS%3Ahttp%3A%2F%2Fcreativecommons.org%2Flicenses%2Fby%2F*&start=1&rows=96&profile=rich&reusability=open");


        new GetContentTask(this).execute(collections_fashion.getTitle(),collections_fashion.getSearchUrl());

        feedsList.add(collections_arts);
        feedsList.add(collections_design);
        feedsList.add(collections_maps);
        feedsList.add(collections_ww1);
        feedsList.add(collections_fashion);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        setCollections();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toast.makeText(this,"Size Collections:"+feedsList.size(),Toast.LENGTH_LONG).show();
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.collections_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CollectionsAdapter(this, feedsList);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mRecyclerView.setHasFixedSize(true);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
