package org.hamm.h1kemaps.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapScaleView;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKPolyline;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.versioning.SKMapUpdateListener;
import com.skobbler.ngx.versioning.SKVersioningManager;

import org.hamm.h1kemaps.app.R;
import org.hamm.h1kemaps.app.application.H1keApplication;
import org.hamm.h1kemaps.app.model.NavigationDrawerItem;
import org.hamm.h1kemaps.app.selecthotodemo.TPZSActivity;
import org.hamm.h1kemaps.app.util.NavigationDrawerListAdapter;
import org.hamm.h1kemaps.app.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Konstantin Hamm on 25.02.15. The Classes and Activities in this Project were
 * developed with the help of the Skobbler Maps SDK and its Support sites.
 * http://developer.skobbler.de/docs/android/2.4.0/index.html
 * and :
 * http://developer.skobbler.de/getting-started/android#sec000_
 * The code from the Skobbler support sites is Open Source therefore
 * this code is Open Source also.
 * <p>
 * Description: This is the Main Activity of the app. It holds the SKMapSurfaceView which is in charge
 * of constructing a map surface view and displaying a map.
 *
 * @seehttp://developer.skobbler.de/docs/android/2.3.0/com/skobbler/ngx/map/SKMapSurfaceView.html SKMapSurfaceListener is listening for map surface events like zoom, pan etc.
 * @seehttp://developer.skobbler.de/docs/android/2.3.0/com/skobbler/ngx/map/SKMapSurfaceListener.html SKRouteListener is listening for Routing events .
 * @seehttp://developer.skobbler.de/docs/android/2.3.0/com/skobbler/ngx/routing/SKRouteListener.html SKNavigationListener listens for callbacks related to navigation from the ngx library
 * @seehttp://developer.skobbler.de/docs/android/2.3.0/com/skobbler/ngx/navigation/SKNavigationListener.html SKCurrentPositionListener is used for receiving current position updates
 * @seehttp://developer.skobbler.de/docs/android/2.3.0/com/skobbler/ngx/positioner/SKCurrentPositionListener.html SensorEventListener listens for sensor events like onOrientationChanged().
 * @seehttp://developer.android.com/reference/android/hardware/SensorEventListener.html SKMapUpdateListener listens for updates on the map data
 * @seehttp://developer.skobbler.de/docs/android/2.3.0/com/skobbler/ngx/versioning/SKMapUpdateListener.html AdapterView.OnItemClickListener a simple onClick() Listener for the Navigation Drawer Menu
 * @seehttp://developer.android.com/reference/android/view/View.OnClickListener.html
 */
public class MapActivity extends ActionBarActivity implements SKMapSurfaceListener, SKRouteListener, SKNavigationListener,
        SKCurrentPositionListener, SensorEventListener, SKMapUpdateListener, AdapterView.OnItemClickListener {

    /**
     * SKMapSurfaceView is the the Map view. This is the Main component of the whole Application
     */
    private SKMapSurfaceView mMapView;

    /**
     * This is where the current position is stored
     */
    private SKPosition mCurrentPosition;

    /**
     * The H1keApplication is for saving the overall state of the app
     */
    private H1keApplication mApp;

    /**
     * SKCurrentPositionProvider provides current position updates using Android location services
     */
    private SKCurrentPositionProvider mCurrentPositionProvider;

    /**
     * A Hash Map for Holding the Annotation created by the user
     */
    private TreeMap<Integer, SKAnnotation> mAnnotationList;

    /**
     * Counter for changing the map style
     */
    private int mMapStyleCounter = 0;

    /**
     * Drawer Layout for navigation throughout the App
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Char Sequences that Holds the Title for the ActionBar and Drawer
     */
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    /**
     * List within the Navigation Drawer
     */
    private ListView mDrawerList;

    /**
     * Used to toggle the Drawer either open or closed
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * Navigation Menu titles
     */
    private String[] mNavMenuTitles;

    /**
     * Navigation Menu Icons
     */
    private TypedArray mNavMenuIcons;

    /**
     * List of Navigation Menu items
     */
    private ArrayList<NavigationDrawerItem> mNavDrawerItems;

    /**
     * Navigation Drawer List adapter for listening on click events of the Drawer
     */
    private NavigationDrawerListAdapter mAdapter;

    /**
     * Holder for the Map view
     */
    private SKMapViewHolder mapViewGroup;

    private ImageButton mDingwei;

    /**
     * Counter for the Annotations.
     */
    private int mAnnotationCounter = 0;

    /**
     * Tells if heading is currently active and if the navigation is in progress
     */
    private boolean mHeadingOn;//, mNavigationInProgress;

    /**
     * Coordinate that holds the navigation destination.
     */
    SKCoordinate mNavDestination;

    /**
     * Keep Screen on / off
     */
    boolean mKeepScreenOn = false;

    /**
     * The onCreate() method builds the activity.
     * Long Story short: In this Method all the necessary initialisations are made and some settings
     * are applied to the map view.
     * <p>
     * First an application object is retrieved via mApp = (H1keApplication) getApplication();.
     * Is that done, the previous app state is loaded from the json file (see class H1keApplication
     * for more detail).
     * After that the Skobbler Maps API is initialized with
     * Utils.initAppLibrary(this, mApp.ismOfflineMaps(), style). Next a SKCurrentPositionProvider
     * is created and then position updates are requested. Then the NavigationDrawer is initialized and
     * the layout get's inflated. After that there are some settings applied to the map( see method
     * applySettingsOnMapView() for more information ). Annotations which could be saved previously
     * are loaded. In the last step every variable from the savedInstanceState is loaded.
     *
     * @param savedInstanceState = this is a bundle where things like the state of the app is saved
     *                           in the event of an interruption ( phone call, user pushing home button etc.)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (H1keApplication) getApplication();

        mApp.retrievePreviousAppState();

        mApp.setmNavigationInProgress(false);

        mNavDestination = new SKCoordinate();

        String[] styleArray = mApp.getMapStylesArray();

        String style = styleArray[mApp.getChosenMapStyle()];

        Utils.initAppLibrary(this, mApp.ismOfflineMaps(), style);

        mCurrentPositionProvider = new SKCurrentPositionProvider(this);

        mCurrentPositionProvider.setCurrentPositionListener(this);

        requestPositionUpdates();
        //主页面的地图
        setContentView(R.layout.activity_map);

        initDrawerView(savedInstanceState);

        mapViewGroup = (SKMapViewHolder) findViewById(R.id.view_group_map);

        mDingwei = (ImageButton) findViewById(R.id.dingwei);

        //定位按钮
        mDingwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view.getId() == R.id.dingwei) {
                    if (mCurrentPosition != null) {

                        mMapView.centerMapOnCurrentPositionSmooth(17, 500);
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, R.string.unable_to_find_location, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            }

        });

        mMapView = mapViewGroup.getMapSurfaceView();


        mMapView.setMapSurfaceListener(this);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_popup, null);

        applySettingsOnMapView();

        SKVersioningManager.getInstance().setMapUpdateListener(this);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener(this));

        mAnnotationList = mApp.getAnnotationList();

        initScaleView();

        if (savedInstanceState != null) {
            mApp.setmNavigationInProgress(savedInstanceState.getBoolean("mNavigationInProgress"));
            mAnnotationCounter = savedInstanceState.getInt("mAnnotationCounter");
            mNavDestination.setLatitude(savedInstanceState.getDouble("destinationLat"));
            mNavDestination.setLongitude(savedInstanceState.getDouble("destinationLon"));
            mMapStyleCounter = savedInstanceState.getInt("mMapStyleCounter");
            mKeepScreenOn = savedInstanceState.getBoolean("mKeepScreenOn");
        }

        if (mKeepScreenOn) {
            switchKeepScreenOnOffQuiet(true);
        }


        if (mCurrentPosition != null) {

            mMapView.centerMapOnCurrentPositionSmooth(17, 500);
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(MapActivity.this, R.string.unable_to_find_location, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


    /**
     * Customize the map view
     * Sets rotation, zooming, panning and other settings.
     * Also here is where the compass is positioned on the screen and set to
     * visible if the user has toggled it.
     */
    /**
     * 自定义地图视图
     * 设置旋转，缩放，平移和其他设置。
     * 此处还有指南针位于屏幕上并设置为的位置
     * 如果用户切换它，则可见。
     */
    private void applySettingsOnMapView() {
        mMapView.getMapSettings().setMapRotationEnabled(true);
        mMapView.getMapSettings().setMapZoomingEnabled(true);
        mMapView.getMapSettings().setMapPanningEnabled(true);
        mMapView.getMapSettings().setZoomWithAnchorEnabled(true);
        mMapView.getMapSettings().setInertiaRotatingEnabled(true);
        mMapView.getMapSettings().setInertiaZoomingEnabled(true);
        mMapView.getMapSettings().setInertiaPanningEnabled(true);
        mMapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.NONE);
        if (mApp.isCompassToggle()) {
            mMapView.getMapSettings().setCompassPosition(new SKScreenPoint(10, 50));
            mMapView.getMapSettings().setCompassShown(true);
        }
    }

    /**
     * This enables the little Scale view in the lower right corner.
     * 右下角启用小比例视图。
     */
    private void initScaleView() {

        // display the scale view on the map
        mapViewGroup.setScaleViewEnabled(true);

        // set the scale view’s position
        mapViewGroup.setScaleViewPosition(0, 80, RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_BOTTOM);

        // get the map scale object from the map holder object that contains it
        SKMapScaleView scaleView = mapViewGroup.getScaleView();

        // set one of the color used to render the scale
        scaleView.setLighterColor(Color.argb(255, 255, 200, 200));

        // disable fade out animation on the scale view
        scaleView.setFadeOutEnabled(false);

        // set the distance units displayed to miles and feet
        scaleView.setDistanceUnit(SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS);
    }

    /**
     * Enables/disables heading mode.
     * The heading mode points to the direction in which the user is looking and displays that with a
     * blue cone.
     *
     * @param enabled sets the heading enabled or disabled
     */
    /**
     * 启用/禁用标题模式。
     * 标题模式指向用户正在查看的方向并使用a显示
     * 蓝色锥形。
     * enabled设置标题已启用或已禁用
     **/
    private void setHeading(boolean enabled) {
        if (enabled) {
            mHeadingOn = true;
            mMapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.POSITION_PLUS_HEADING);
            startOrientationSensor();
        } else {
            mHeadingOn = false;
            mMapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.NONE);
            stopOrientationSensor();
        }
    }

    /**
     * This method initializes the Navigation Drawer of the app.
     * 此方法初始化应用程序的导航抽屉
     *
     * @param savedInstanceState
     */
    private void initDrawerView(Bundle savedInstanceState) {

        // Getting Resources
        mTitle = mDrawerTitle = getTitle();

        mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        mNavMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mNavDrawerItems = new ArrayList<NavigationDrawerItem>();

        // adding navigation Drawer titles and icons to the Navigation Drawer
        for (int i = 0; i < mNavMenuTitles.length; i++)
            mNavDrawerItems.add(new NavigationDrawerItem(mNavMenuTitles[i], mNavMenuIcons.getResourceId(i, -1)));

        mNavMenuIcons.recycle();

        mAdapter = new NavigationDrawerListAdapter(getApplicationContext(), mNavDrawerItems);

        mDrawerList.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initActionBarDrawerToggle();
    }

    private void initActionBarDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if navigation drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);


    }


    /**
     * This method sets the title of the app.
     * 此方法设置应用程序的标题。
     *
     * @param title = title of the app
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    //主页面的地图
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_activity, menu);
        return true;
    }

    /**
     * This starts a new About activity.
     * 关于
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar mApp icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_about:
                // About
                startActivity(new Intent(MapActivity.this, AboutActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Activates the orientation sensor andr registers a listener for orientation changes.
     */
    private void startOrientationSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Deactivates the orientation sensor.
     * <p>
     * 关闭方向传感器。
     */
    private void stopOrientationSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    /**
     * Every time the app is going to be paused and resumed by the user afterwards, the onResume()
     * method is called. Main purpose of this method is that the map view is resumed and, if the app was in
     * heading on mode the orientation sensors are started again.
     * Additionally the position updates are requested and things like follower mode is set
     * (if the app was in navigation mode) or annotations are redrawn (if there where any) before
     * onPause().
     * / **
     * *每次应用程序暂停后由用户暂停，onResume（）
     * *方法被调用。此方法的主要目的是恢复地图视图，如果应用程序在，则恢复
     * *朝向模式，再次启动方向传感器。
     * *此外，还要求更新位置，并设置跟随者模式等内容
     * *（如果应用程序处于导航模式）或之前重新绘制注释（如果有的话）
     * * onPause（）。
     * * /
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mHeadingOn) {
            startOrientationSensor();
        }

        requestPositionUpdates();

        if (mApp.ismNavigationInProgress()) {
            mMapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.POSITION);
        }

        if (!mAnnotationList.isEmpty()) {
            redrawAnnotations();
        }
    }

    /**
     * This method requests position updates. This can be accomplished by GPS and GSM.
     * Please see the Utils class for more details.
     * *此方法请求位置更新。这可以通过GPS和GSM来完成。
     */
    private void requestPositionUpdates() {

        if (Utils.hasGps(this)) {
            // If the Device has a GPS connection request position updates
            // with gps.
            mCurrentPositionProvider.requestLocationUpdates(true, true, true);

        } else if (Utils.hasInternet(this) && !mApp.ismOfflineMaps()) {
            // If the App is in online mode and has a gsm connection
            // request position updates with gsm
            mCurrentPositionProvider.requestLocationUpdates(false, true, true);
        }
    }

    /**
     * onPause() stops the orientation sensors, location updates and the map view is paused manually
     * all that is required to save battery power of the device.
     * / **
     * * onPause（）停止方向传感器，位置更新和手动暂停地图视图
     * *所有这些都是节省设备电池电量所需的。
     * * /
     */

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mHeadingOn) {
            stopOrientationSensor();
        }
        mCurrentPositionProvider.stopLocationUpdates();

        switchKeepScreenOnOffQuiet(false);
    }

    /**
     * onDestroy is called if the Android Operating System decides it can be destroyed to
     * clear some memory.
     * Important is that the location updates are stopped, the App state is saved for later use
     * and the map view gets wiped out by SKMaps.getInstance.destroySKMaps().
     * / **
     * 如果Android操作系统决定将其销毁，则调用onDestroy
     * *清除一些记忆。
     * *重要的是停止位置更新，保存应用程序状态以供以后使用
     * *并且地图视图被SKMaps.getInstance.destroySKMaps（）消灭。
     * * /
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentPositionProvider.stopLocationUpdates();
        mApp.saveCurrentAppState();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SKMaps.getInstance().destroySKMaps();
    }

    /**
     * When the Map surface of the map view is created the onSurfaceCreated() method is called.
     * If the annotation list is not empty when this method is called - the annotations are redrawn.
     * / **
     * *创建地图视图的地图表面时，将调用onSurfaceCreated（）方法。
     * *如果在调用此方法时注释列表不为空 - 则重新绘制注释。
     */

    @Override
    public void onSurfaceCreated() {

        if (!mAnnotationList.isEmpty()) {
            redrawAnnotations();
        }
    }

    /**
     * onSaveInstanceState(Bundle) saves the state of the app temporarily.
     * All of the states that are needed to resume the app correctly are saved here and loaded
     * every time the app has to be rebuild completely via onCreate.
     *
     * @param outState = The bundle where it is all saved.
     *                 / **
     *                 * onSaveInstanceState（Bundle）暂时保存应用程序的状态。
     *                 *正确恢复应用程序所需的所有状态都保存在此处并加载
     *                 *每次必须通过onCreate完全重建应用程序。
     *                 *
     *                 * @param outState =全部保存的包。
     *                 * /
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mNavigationInProgress", mApp.ismNavigationInProgress());
        outState.putInt("mAnnotationCounter", mAnnotationCounter);
        outState.putDouble("destinationLat", mNavDestination.getLatitude());
        outState.putDouble("destinationLon", mNavDestination.getLongitude());
        outState.putInt("mMapStyleCounter", mMapStyleCounter);
        outState.putBoolean("mKeepScreenOn", mKeepScreenOn);
    }

    /**
     * Restores the instance state which was saved before.
     *
     * @param savedInstanceState / **
     *                           *恢复之前保存的实例状态。
     *                           *
     *                           * @param savedInstanceState
     *                           * /
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Every time when there's a position update it must be reported to the map view and
     * set as the current position.
     *
     * @param currentPosition / **
     *                        *每当有位置更新时，必须将其报告给地图视图和
     *                        *设为当前位置。
     *                        *
     *                        * @param currentPosition
     *                        * /
     */
    @Override
    public void onCurrentPositionUpdate(SKPosition currentPosition) {
        this.mCurrentPosition = currentPosition;
        mMapView.reportNewGPSPosition(this.mCurrentPosition);
        mMapView.setPositionAsCurrent(new SKCoordinate(mCurrentPosition.getLongitude(), mCurrentPosition.getLatitude()), 15, false);
    }

    /**
     * When the map view is panned the heading function is disabled.
     * / **
     * *平移地图视图时，标题功能被禁用。
     * * /
     */
    @Override
    public void onActionPan() {
        if (mHeadingOn) {
            setHeading(false);
        }
    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onScreenOrientationChanged() {
    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {

    }

    /**
     * If the map is rotated the heading function is disabled.
     */
    @Override
    public void onRotateMap() {
        if (mHeadingOn) {
            setHeading(false);
        }
    }

    /**
     * 每次用户长时间点击地图视图时，都会调用此方法。
     * 在长按之后，标记（注释）将添加到地图视图中。
     *
     * @param skScreenPoint = the screen point where the user performed a long tap.
     */
    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {
        if (!mApp.ismNavigationInProgress()) {
            SKCoordinate annotationCoord = mMapView.pointToCoordinate(skScreenPoint);
            mNavDestination = annotationCoord;
            SKAnnotation annotation = new SKAnnotation();

            annotation.setUniqueID(mAnnotationCounter++);
            annotation.setLocation(annotationCoord);
            annotation.setMininumZoomLevel(5);
            annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
            addAnnotationToMapView(annotation);
        }
    }

    /**
     * This method adds an annotation first to a tree map, and then to the map view.
     * after that a polyline redraw is performed.
     *
     * @param annotation = the new annotation that is going to be added.
     *                   / **
     *                   *此方法首先将注释添加到树图，然后添加到地图视图。
     *                   *之后执行折线重绘。
     *                   *
     *                   * @param annotation =将要添加的新注释。
     *                   * /
     */
    private void addAnnotationToMapView(SKAnnotation annotation) {
        mAnnotationList.put(annotation.getUniqueID(), annotation);

        mMapView.addAnnotation(mAnnotationList.get(annotation.getUniqueID()), SKAnimationSettings.ANIMATION_PIN_DROP);

        redrawPolyline();
    }

    /**
     * This function iterates over the tree map which holds all annotations and adds them to the
     * map view.
     * / **
     * *此函数迭代树形图，其中包含所有注释并将它们添加到
     * *地图视图。
     * * /
     */
    private void redrawAnnotations() {
        if (mAnnotationList != null && !mAnnotationList.isEmpty()) {
            for (SKAnnotation annotationItem : mAnnotationList.values()) {
                mMapView.addAnnotation(annotationItem, SKAnimationSettings.ANIMATION_PIN_DROP);
            }
        }
    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {

    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {

    }

    /**
     * This method is called when the user is tapping on an existing annotation.
     * After the tap that particular annotation gets deleted.
     * @param skAnnotation = Annotation that the user tapped on.
     */
    /**
     * 当用户点击现有注释时调用此方法。
     * <p>
     * 在点击后，特定注释被删除。
     *
     * @param =用户点击的注释annotation
     */
    @Override
    public void onAnnotationSelected(final SKAnnotation skAnnotation) {
        if (mAnnotationList.containsKey(skAnnotation.getUniqueID())) {


            final String[] items = {"行车导航", "添加记事", "历史足迹", "取消标记"};
            AlertDialog.Builder listDialog =
                    new AlertDialog.Builder(MapActivity.this);

            listDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // which 下标从0开始
                    // ...To-do
                    Toast.makeText(MapActivity.this,
                            "你点击了" + items[which],

                            Toast.LENGTH_SHORT).show();
                    if (items[which] == items[0]) {
                       // startActivity(new Intent(getApplicationContext(), TPZSActivity.class));
                        startActivity(new Intent(getApplicationContext(), daohangActivity.class));
                    } else if (items[which] == items[1]) {
                        startActivity(new Intent(getApplicationContext(), fatieActivity.class));
                    } else if (items[which] == items[2]) {

                        startActivity(new Intent(getApplicationContext(), lishizujiActivity.class));
                    } else if (items[which] == items[3]) {

                        mMapView.deleteAnnotation(skAnnotation.getUniqueID());   //  单击标点去除
                        mAnnotationList.remove(skAnnotation.getUniqueID());
                    }
                }
            });
            listDialog.show();


            redrawPolyline();
        }
    }

    /**
     * This function draws a polyline on the map view along all annotations.
     * 用户标点，自动绘制折线
     */
    private void redrawPolyline() {

        mMapView.clearAllOverlays();

        if (mAnnotationList.size() > 1) {

            // get a polyline object
            SKPolyline polyline = new SKPolyline();

            List<SKCoordinate> coordinates = hashMapToAnnotationList(mAnnotationList);

            polyline.setNodes(coordinates);
            // set polyline color
            polyline.setColor(new float[]{0f, 0f, 1f, 1f});
            // set properties for the outline
            polyline.setOutlineColor(new float[]{0f, 0f, 1f, 1f});
            polyline.setOutlineSize(3);
            polyline.setOutlineDottedPixelsSolid(3);
            polyline.setOutlineDottedPixelsSkip(3);

            mMapView.addPolyline(polyline);
        }
    }

    /**
     * This method takes a (automatically) ordered tree map and returns a Linked list in the same
     * order.
     *
     * @param treeMap = automatically ordered map filled with annotations.
     * @return a linked list filled with all the annotations from the tree map in the same order.
     * / **
     * *此方法采用（自动）有序树形图并返回相同的链接列表
     * *订单。
     * *
     * * @param treeMap =自动排序的地图填充注释。
     * * @return一个链表，用同一顺序填充树图中的所有注释。
     * * /
     */
    private LinkedList<SKCoordinate> hashMapToAnnotationList(TreeMap<Integer, SKAnnotation> treeMap) {
        LinkedList<SKCoordinate> coordinates = new LinkedList<SKCoordinate>();

        for (SKAnnotation an : treeMap.values()) {
            coordinates.add(an.getLocation());
        }
        return coordinates;
    }


    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onObjectSelected(int i) {

    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onOffportRequestCompleted(int i) {

    }

    @Override
    public void onNewVersionDetected(int i) {

    }

    @Override
    public void onMapVersionSet(int i) {

    }

    @Override
    public void onVersionFileDownloadTimeout() {

    }

    @Override
    public void onNoNewVersionDetected() {

    }

    /**
     * This method gets called when a destination is reached and the navigation is stopped.
     */
    @Override
    public void onDestinationReached() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MapActivity.this, R.string.label_dest_reached, Toast.LENGTH_SHORT).show();
                // stop navigation if reaching destination
                stopNavigation();
            }
        });
    }

    @Override
    public void onSignalNewAdvice(String[] strings, boolean b) {

    }

    @Override
    public void onSpeedExceeded(String[] strings, boolean b) {

    }

    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState) {

    }

    @Override
    public void onReRoutingStarted() {

    }

    @Override
    public void onFreeDriveUpdated(String s, String s2, int i, double v, double v2) {

    }

    @Override
    public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState) {

    }

    @Override
    public void onTunnelEvent(boolean b) {

    }

    @Override
    public void onRouteCalculationCompleted(int i, int i2, int i3, boolean b, int i4) {

    }

    @Override
    public void onAllRoutesCompleted() {

    }

    @Override
    public void onServerLikeRouteCalculationCompleted(int i) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

    }

    /**
     * Called when sensor values have changed.
     *
     * @param event = sensor event
     *              / **
     *              *当传感器值发生变化时调用。
     *              *
     *              * @param事件=传感器事件
     *              * /
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * / **
     * *当已注册传感器的精度发生变化时调用。
     * * /
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * This method calculates a route to a specific coordinate.
     * Caution this gets saved in SKRouteManager.getInstance().calculateRoute(route)
     *
     * @param coordinate = the destination coordinate.
     *                   / **
     *                   *此方法计算到特定坐标的路线。
     *                   *注意这会保存在SKRouteManager.getInstance（）中.calculateRoute（route）
     *                   *
     *                   * @param coordinate =目标坐标。
     *                   * /
     */
    private void calculateRouteToCoord(final SKCoordinate coordinate) {
        if (coordinate != null) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.foot_navigation)
                    .setMessage(R.string.start_navigation_label)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            launchRouteCalculation(coordinate);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            stopNavigation();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_map)
                    .show();
        }
    }

    /**
     * Launches the route calculation to a destination coordinate.
     *
     * @param coordinate = destination coordinate.
     *                   *将路线计算发射到目的地坐标。
     *      *
     *      * @param coordinate =目标坐标。
     *      * /
     */
    private void launchRouteCalculation(SKCoordinate coordinate) {
        // get a route settings object and populate it with the desired properties
        if (coordinate != null) {
            SKRouteSettings route = new SKRouteSettings();
            // set start and destination points
            if (mCurrentPosition != null) {
                route.setStartCoordinate(new SKCoordinate(mCurrentPosition.getLongitude(), mCurrentPosition.getLatitude()));
                route.setDestinationCoordinate(coordinate);
                // set the number of routes to be calculated
                route.setNoOfRoutes(1);
                // set the route mode
                route.setRouteMode(SKRouteSettings.SKROUTE_PEDESTRIAN);
                // set whether the route should be shown on the map after it's computed
                route.setRouteExposed(true);
                // set the route listener to be notified of route calculation
                // events

                SKRouteManager.getInstance().setRouteListener(this);
                // pass the route to the calculation routine
                SKRouteManager.getInstance().calculateRoute(route);
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, R.string.unable_to_find_location, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * Launches the calculation of two alternative routes
     *
     * @param coordinate = destination coordinate
     *                   / **
     *      *启动两条替代路线的计算
     *      *
     *      * @param coordinate =目标坐标
     *      * /
     */
    private void launchAlternativeRouteCalculation(SKCoordinate coordinate) {

        if (coordinate != null) {
            SKRouteSettings route = new SKRouteSettings();
            if (mCurrentPosition != null) {
                route.setStartCoordinate(new SKCoordinate(mCurrentPosition.getLongitude(), mCurrentPosition.getLatitude()));
                route.setDestinationCoordinate(coordinate);
                // selecting the total number of routes, with 2 alternatives
                route.setNoOfRoutes(3);
                route.setRouteMode(SKRouteSettings.SKROUTE_PEDESTRIAN);
                route.setRouteExposed(true);
                SKRouteManager.getInstance().setRouteListener(this);
                SKRouteManager.getInstance().calculateRoute(route);
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, R.string.unable_to_find_location, Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }
    }

    /**
     * Launches the navigation on the current route
     * (at this point the route should be saved in the
     * SKRouteManager.getInstance().calculateRoute(route))
     * / **
     *      *启动当前路线的导航
     *      *（此时路线应保存在
     *      * SKRouteManager.getInstance（）。calculateRoute（route））
     *      * /
     */
    private void launchNavigation() {

        mApp.setmNavigationInProgress(true);

        // get navigation settings object
        SKNavigationSettings navigationSettings = new SKNavigationSettings();

        // set the desired navigation settings
        navigationSettings.setNavigationType(SKNavigationSettings.NAVIGATION_TYPE_REAL);
        navigationSettings.setPositionerVerticalAlignment(-0.25f);
        navigationSettings.setShowRealGPSPositions(false);

        // get the navigation manager object
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mMapView);

        // set listener for navigation events
        navigationManager.setNavigationListener(this);

        // start navigating using the settings
        navigationManager.startNavigation(navigationSettings);
    }

    /**
     * The user is able to keep the screen on or use the default behaviour.
     *
     * @param on = screen on / off
     *           / **
     *      *用户可以保持屏幕开启或使用默认行为。
     *      *
     *      * @param on =屏幕开/关
     *      * /
     */
    private void switchKeepScreenOnOffVerbose(boolean on) {
        if (on) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    Toast.makeText(MapActivity.this, R.string.high_energy_consumption, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(MapActivity.this, R.string.screen_toggle_off, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * This method will set a flag so that the screen will not turn off or reset it to the
     * default behaviour. In this particular method there are no warnings to the user
     * (unlike in the method switchKeepScreenOnOffVerbose(boolean))
     *
     * @param on = screen on / off
     *           / **
     *      *此方法将设置一个标志，以便屏幕不会关闭或重置为
     *      *默认行为。在这种特定方法中，没有对用户的警告
     *      *（与方法switchKeepScreenOnOffVerbose（boolean）不同）
     *      *
     *      * @param on =屏幕开/关
     *      * /
     */
    private void switchKeepScreenOnOffQuiet(boolean on) {
        if (on) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * Stops the navigation and clears the annotation list.
     * / **
     *      *停止导航并清除注释列表。
     *      * /
     */
    private void stopNavigation() {
        mApp.setmNavigationInProgress(false);
        SKNavigationManager.getInstance().stopNavigation();

        mAnnotationList.clear();
    }

//按钮点击跳转activity
  /*  public void searchButtonProcess(View v) {
        if (v== mDingwei) {
            startActivity(new Intent(getApplicationContext(), Notbeencompleted.class));
        }
    }
*/

    //搜索栏
    public void setmSearchView(View v) {

    }

//定位
/*     @override
    public void onClick(View v) {
        if (v.getId() == R.id.dingwei) {
            if (mCurrentPosition != null) {

                mMapView.centerMapOnCurrentPositionSmooth(17, 500);
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, R.string.unable_to_find_location, Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }
    }*/

    /**
     * This class is a item click listener for the Navigation Drawer.
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {

        private Context context;

        public SlideMenuClickListener(Context con) {
            context = con;
        }

        /**
         * Is executed when the user is tapping on one of the Navigation Drawer Items.
         *
         * @param parent   = The Adapter view where the click happened.
         * @param view     = The view within the AdapterView that was clicked (this will be a view
         *                 provided by the adapter)
         * @param position = The position of the view in the adapter.
         * @param id       = The row id of the item that was clicked.
         */


        /**
         * 当用户点击其中一个导航抽屉项时执行。
         *
         * @param parent=单击发生的适配器视图。
         * @param view=单击的adapterview中的视图（这将是一个视图 由适配器提供）
         * @param position=视图在适配器中的位置。
         * @param id=单击的项的行ID。
         */

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            changeView(position);
        }


        /**
         * This switch Statement represents the Navigation drawer with its indexes
         * <p>
         * index 0 = Centers the Map on the position of the device.
         * index 1 = Starts new MapPackagesListActivity ( Download Offline Map Packages )
         * index 2 = Toggles the On- or Offline Mode of the app
         * index 3 = Starts or stops the navigation a an annotation (of course, annotation mus exist
         * beforehand)
         * index 4 = Changes Map Style.
         * index 5 = Deletes all annotations and polylines
         * index 6 = Toggle compass on / off
         * index 7 = Toggle heading function on / off
         * index 8 = Switch Screen toggle on / off
         *
         * @param position = index.
         */


        /**
         * 此switch语句表示导航抽屉及其索引
         *
         * <p>
         * <p>
         * 索引0=将地图居中显示在设备的位置上。
         * <p>
         * index 1=启动新的mappackageslistactivity（下载脱机映射包）
         * <p>
         * index 2=切换应用程序的打开或脱机模式
         * <p>
         * index3=开始或停止导航注释（当然，注释mus存在
         * <p>
         * 预先）
         * <p>
         * index 4=更改地图样式。
         * <p>
         * index 5=删除所有注释和多段线
         * <p>
         * index6=打开/关闭罗盘
         * <p>
         * index7=打开/关闭航向功能
         * <p>
         * index8=打开/关闭屏幕切换
         *
         * @参数位置=索引。
         */
        private void changeView(int position) {

            switch (position) {
                case 0:
                    //个人中心
                   // startActivityForResult(new Intent(MapActivity.this, MineActivity.class), 1);
                    startActivity(new Intent(MapActivity.this, MineActivity.class));
                    break;
                case 1:
                    //社区论坛

                    startActivity(new Intent(MapActivity.this, ShequActivity.class));
                    break;
                case 2:
                    //图片技术
                    startActivity(new Intent(MapActivity.this, TpjsActivity.class));
                    break;
                case 3:
                    //车载导航
                    startActivity(new Intent(MapActivity.this, daohangActivity.class));
                    break;
                case 4:
                    //实时车况
                    // startActivity(new Intent(MapActivity.this, CreateNewEntry.class));
                    startActivity(new Intent(MapActivity.this, obdActivity.class));
                    break;
                case 5:
                    // Find Me定位 !

                    if (mCurrentPosition != null) {

                        mMapView.centerMapOnCurrentPositionSmooth(17, 500);
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, R.string.unable_to_find_location, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    break;

                case 6:
                    // Download Offline Maps下载离线地图

                    if (!mApp.ismOfflineMaps()) {

                        if (mApp.ismNavigationInProgress()) {
                            Toast.makeText(MapActivity.this, R.string.abort_navigation,
                                    Toast.LENGTH_LONG).show();
                            stopNavigation();
                            mMapView.deleteAllAnnotationsAndCustomPOIs();
                            mMapView.clearAllOverlays();
                            SKRouteManager.getInstance().clearCurrentRoute();
                        }
                        startActivity(new Intent(context, MapPackagesListActivity.class));
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, R.string.only_online_downloads,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    break;

                case 7:
                    // Toggle Maps off / online切换在线
                    if (!mApp.ismOfflineMaps()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, R.string.app_offline,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        mApp.setmOfflineMaps(true);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, R.string.app_online,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        mApp.setmOfflineMaps(false);
                    }
                    mDrawerList.setItemChecked(position, false);

                    String[] tempStyleArray = mApp.getMapStylesArray();
                    String tempStyle = tempStyleArray[mMapStyleCounter];
                    // Init the app library again
                    Utils.initAppLibrary(MapActivity.this, mApp.ismOfflineMaps(), tempStyle);
                    // Recreate the activity
                    recreate();
                    break;
                case 8:
                    //设置
                    startActivity(new Intent(MapActivity.this, ShezhiActivity.class));
                    break;


            /*    case 3:
                    //Start navigation开始/停止导航
                    if (!mApp.ismNavigationInProgress() && mNavDestination != null) {

                        calculateRouteToCoord(mNavDestination);
                        mMapView.deleteAllAnnotationsAndCustomPOIs();
                        mMapView.clearAllOverlays();
                        launchNavigation();

                    } else if (mApp.ismNavigationInProgress()) {

                        stopNavigation();
                        mMapView.deleteAllAnnotationsAndCustomPOIs();
                        mMapView.clearAllOverlays();
                        SKRouteManager.getInstance().clearCurrentRoute();
                        Toast.makeText(MapActivity.this, R.string.abort_navigation,
                                Toast.LENGTH_LONG).show();
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, R.string.pin_dest_for_nav, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;

                case 4:
                    // Switch Map Style更改地图样式
                    mMapStyleCounter++;
                    if (mMapStyleCounter >= mApp.getMapStylesArray().length) {
                        mMapStyleCounter = 0;
                    }

                    mApp.setChosenMapStyle(mMapStyleCounter);

                    String[] styleArray = mApp.getMapStylesArray();
                    String style = styleArray[mMapStyleCounter];

                    final SKMapViewStyle mapViewStyle = new SKMapViewStyle(mApp.getResourcePath() + style + "/", style + ".json");
                    mMapView.getMapSettings().setMapStyle(mapViewStyle);
                    mDrawerList.setItemChecked(position, false);
                    break;

                case 5:
                    // Delete Pins and Overlays清除标点
                    mMapView.deleteAllAnnotationsAndCustomPOIs();
                    mMapView.clearAllOverlays();
                    mAnnotationList.clear();

                    if (!mApp.ismNavigationInProgress()) {
                        mNavDestination = null;
                    }

                    mDrawerList.setItemChecked(position, false);
                    break;

                case 6:
                    // Toggle compass on / off开启/关闭指南针
                    if (!mApp.isCompassToggle()) {
                        mApp.setCompassToggle(true);
                    } else {
                        mApp.setCompassToggle(false);
                    }
                    mMapView.getMapSettings().setCompassShown(mApp.isCompassToggle());
                    mApp.setCompassToggle(mApp.isCompassToggle());

                    break;
                case 7:
                    // Toggle heading function on / off开启/关闭面向
                    if (!mHeadingOn) {
                        setHeading(true);
                    } else {

                        setHeading(false);
                    }

                    break;
                case 8:
                    // Keep screen on / off保持屏幕常亮
                    if (!mKeepScreenOn) {
                        switchKeepScreenOnOffVerbose(true);
                        mKeepScreenOn = true;
                    } else {
                        switchKeepScreenOnOffVerbose(false);
                        mKeepScreenOn = false;
                    }

                    break;*/
            }
            mDrawerLayout.closeDrawers();
        }
    }

    private String[] mStrs = {"kk", "kk", "wskx", "wksx"};
    private SearchView mSearchView;
    private ListView lListView;


}