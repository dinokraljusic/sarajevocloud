package de.rwth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionPlaceObject;
import actions.ActionRotateCameraBuffered;
import actions.ActionWaitForAccuracy;
import commands.Command;
import commands.system.CommandDeviceVibrate;
import components.ViewPosCalcerComp;
import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLFactory;
import gl.LightSource;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;
import gui.InfoScreenSettings;
import listeners.eventManagerListeners.LocationEventListener;
import listeners.eventManagerListeners.TrackBallEventListener;
import system.DefaultARSetup;
import system.EventManager;
import util.Log;
import util.Vec;
import util.Wrapper;
import v2.simpleUi.util.ErrorHandler;
import worldData.MoveComp;
import worldData.Obj;
import worldData.RenderableEntity;
import worldData.SystemUpdater;
import worldData.World;

public class ModelLoaderSetup extends DefaultARSetup {


    //region CONSTANTS

    private String LOG_TAG = "ModelLoader";
    private static final long VIBRATION_DURATION_IN_MS = 20;

    //endregion

    //region FIELDS

    private boolean lightsOnOff = true;
    protected static final float zMoveFactor = 1.4f;
    private LightSource spotLight;
    GL1Renderer _localRenderer;
    private CommandDeviceVibrate vibrateCommand;

    private Wrapper _targetMoveWrapper;

    //private String textureName;
    //private String fileName;
    private ViewPosCalcerComp _viewPosCalcer;
    private Obj _selectedObj;
    private MoveComp _moveComp;

    private Button _lokacijaLabel;
    private Button _rightInfo, _rightFotografije, _rightAbout, _leftMojCloud, _leftSarajevoCloud;
    private ImageView _ivPlus, _ivReload, _ivMojCloud, _ivSarajevoCloud;

    private LinearLayout  _rightMenu,_leftMenu;
    private LinearLayout _messageBox;
    private LinearLayout _titleBar;
    private TextView _messageBox_TextView;
    private LinearLayout _messageBox_buttons;
    ImageView _messageBox_yesButton,
            _messageBox_noButton;
    View _cameraButton;
    View _about;

    Typeface defaultFont;

    //endregion

    //region CONSTRUCTORS

    public ModelLoaderSetup() {
        _targetMoveWrapper = new Wrapper();

        //instantiated light here, since the method _a2_initLightning() is no longer overridden
        spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(0, 0, 0));
    }

    //endregion

    //region METHODS

    //region OVERRIDES

    @Override
    public void _a_initFieldsIfNecessary() {
        super._a_initFieldsIfNecessary();
        Log.i(LOG_TAG, "entering _a_initFieldsIfNecessary");

        _targetMoveWrapper = new Wrapper();
        // allow the user to send error reports to the developer:
        ErrorHandler.enableEmailReports("miniprogrammerme@gmail.com", "Error in SarajevoCloud App");
        EventManager.getInstance().setMaxNrOfBufferedLocations(30);
    }

    @Override
    public void _b_addWorldsToRenderer(GL1Renderer renderer,
                                       GLFactory objectFactory, GeoObj currentPosition) {
        super._b_addWorldsToRenderer(renderer, objectFactory, currentPosition);
        Log.i(LOG_TAG, "entering _b_addWorldsToRenderer");

        _viewPosCalcer = new ViewPosCalcerComp(camera, 150, 0.1f) {
            @Override
            public void onPositionUpdate(worldData.Updateable parent,
                                         Vec targetVec) {
                if (parent instanceof Obj) {
                    Obj obj = (Obj) parent;
                    MoveComp m = obj.getComp(MoveComp.class);
                    if (m != null) {
                        m.myTargetPos = targetVec;
                    }
                }
            }
        };
        _moveComp = new MoveComp(4);
    }

    @Override
    public void addObjectsTo(GL1Renderer renderer, final World world,
                             GLFactory objectFactory) {
        Log.i(LOG_TAG, "entering addObjectsTo");
/*		final Obj lightObject = new Obj();

		spotLight.setPosition(new Vec(1, 1, 1));
		MeshComponent circle = objectFactory.newCircle(null);
		circle.setRotation(new Vec(0.2f, 0.2f, 0.2f));
		MeshComponent lightGroup = new Shape();
		lightGroup.addChild(spotLight);
		lightGroup.addChild(circle);
		lightObject.setComp(lightGroup);
		lightObject.setComp(new MoveComp(1));

		lightObject.setOnClickCommand(new Command() {
			@Override
			public boolean execute() {
				_targetMoveWrapper.setTo(lightObject);
				return true;
			}
		});
		world.add(lightObject);

		_targetMoveWrapper.setTo(lightObject);*/

        _localRenderer = renderer;
        GDXConnection.init(myTargetActivity, renderer);

/*		new ModelLoader(renderer, fileName, textureName) {
			@Override
			public void modelLoaded(MeshComponent gdxMesh) {
				Log.d(LOG_TAG, "Mesh loaded.");
				final Obj obj3D = new Obj();
				obj3D.setComp(gdxMesh);
				world.add(obj3D);
				setComps(obj3D);
				obj3D.setOnClickCommand(new Command() {
					@Override
					public boolean execute() {
						setComps(obj3D);
						Log.d(LOG_TAG, "Mesh uloaded.");
						return true;
					}
				});
			}
		};*/
    }

    @Override
    public void _c_addActionsToEvents(EventManager eventManager,
                                      CustomGLSurfaceView arView, SystemUpdater updater) {
        Log.i(LOG_TAG, "entering _c_addActionsToEvents");

        super.rotateGLCameraAction = new ActionRotateCameraBuffered(camera);
        Action rot2 = new ActionPlaceObject(super.camera, _targetMoveWrapper, 50);

        eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
        eventManager.addOnLocationChangedAction(
                new ActionCalcRelativePos(world, camera));

        updater.addObjectToUpdateCycle(rotateGLCameraAction);
        updater.addObjectToUpdateCycle(rot2);
        eventManager.addOnOrientationChangedAction(rot2);

        //eventManager.addOnLocationChangedAction(new ActionMoveCameraBuffered(camera, 25, 5));
        //eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(super.camera, 5, 25));
        eventManager.addOnTrackballAction(new TrackBallEventListener() {
            @Override
            public boolean onTrackballEvent(float x, float y, MotionEvent event) {
                Log.d(LOG_TAG, "TRACKBALL x: " + x + ", y=" + y);
                if (_selectedObj != null && _selectedObj.getMeshComp() != null) {
                    Vec rotation = _selectedObj.getMeshComp().getRotation();
                    if (x > 0)
                        rotation.rotateAroundXAxis(15);
                    else if (x < 0)
                        rotation.rotateAroundXAxis(-15);
                    Log.d(LOG_TAG, "rotation: " + rotation);
                    _selectedObj.getMeshComp().setRotation(rotation);
                    //return  true;
                }
                return true;
            }
        });

        ActionWaitForAccuracy _minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 20.0f, 10) {
            @Override
            public void minAccuracyReachedFirstTime(Location l,
                                                    ActionWaitForAccuracy a) {
                callAddObjectsToWorldIfNotCalledAlready();
                if (!EventManager.getInstance().getOnLocationChangedAction().remove(a)) {
                    Log.e(LOG_TAG,
                            "Could not remove minAccuracyAction from the onLocationChangedAction list");
                }
            }
        };
        eventManager.addOnLocationChangedAction(_minAccuracyAction);
        eventManager.addOnLocationChangedAction(new LocationEventListener() {
            @Override
            public boolean onLocationChanged(Location location) {
                String locationParams = "{ " + location.getAccuracy() + " } " + IspisLokacije(location) + " VS " +
                        IspisLokacije(camera.getGPSLocation());
                _lokacijaLabel.setText(locationParams);

                //new Refresh().execute(locationParams);
                for (int i = 0; i< world.length(); i++) {
                    RenderableEntity go = world.getAllItems().get(i);
                    if(go instanceof  GeoObj) {
                        ((GeoObj) go).refreshVirtualPosition();
                        Log.i("MoldelLoaders", "geoObject refreshed");
                    }
                }

                return true;
            }
        });
        //super._c_addActionsToEvents(eventManager, arView, updater);

        //eventManager.addOnLocationChangedAction(new ActionMoveCameraBuffered(camera, 25, 5));
        //eventManager.addOnOrientationChangedAction(new ActionRotateCameraBuffered(camera));
        //eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera, 25, 0));

        // clear some inputs set in default methods
        //eventManager.getOnLocationChangedAction().clear();
        //eventManager.getOnTrackballEventAction().clear();

        //eventManager.addOnTrackballAction(new ActionMoveObject(
        //			_targetMoveWrapper, getCamera(), 10, 200));

        eventManager.registerLocationUpdates();
        callAddObjectsToWorldIfNotCalledAlready();
    }

    private class Refresh extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String val = params[0];
            try{
                Utility.SaveLog(Spremnik.getInstance().getUrl() + "/logujLokaciju.php", val);
            }catch (Exception ex) {
                Log.d("URL", ex.toString());
            }
            return null;
        }
    }

    @Override
    public void _e2_addElementsToGuiSetup(final GuiSetup guiSetup, Activity activity) {
        //super._e2_addElementsToGuiSetup(guiSetup, activity);
        Log.i(LOG_TAG, "entering _e2_addElementsToGuiSetup");
        try {
            Log.d(LOG_TAG,
                    "Trying to enable vibration feedback for UI actions");
            vibrateCommand = new CommandDeviceVibrate(getActivity(), VIBRATION_DURATION_IN_MS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        defaultFont = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/ACTOPOLIS.otf");

        _lokacijaLabel = new Button(getActivity().getApplicationContext());
        _messageBox_TextView = new TextView(getActivity());
        _messageBox_buttons = new LinearLayout(getActivity());

        _messageBox = guiSetup.getBottomView();
        _messageBox.setVisibility(View.GONE);
        _messageBox.setOrientation(LinearLayout.VERTICAL);
        _messageBox.setBackgroundColor(android.graphics.Color.argb(128, 0, 0, 0));
        _messageBox.addView(_messageBox_TextView);
        _messageBox.addView(_messageBox_buttons);

        _titleBar = guiSetup.getTopView();
        _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
        _titleBar.setMinimumWidth((int) getScreenHeigth());
        _titleBar.setTop(0);

        _rightMenu =  new LinearLayout(getActivity());
        getGuiSetup().addViewToRight(_rightMenu);
        _rightMenu.setVisibility(View.GONE);
        _rightMenu.setOrientation(LinearLayout.VERTICAL);
        _rightMenu.setBackgroundColor(android.graphics.Color.argb(128, 0, 0, 0));

        _leftMenu = new LinearLayout(getActivity());
        getGuiSetup().addViewToLeft(_leftMenu);
        _leftMenu.setVisibility(View.GONE);
        _leftMenu.setOrientation(LinearLayout.VERTICAL);
        _leftMenu.setWeightSum(99);



        //guiSetup.addViewToTop(_lokacijaLabel);

        //region --- old code ---
        /*guiSetup.addButtonToBottomView(new Command() {
            @Override
            public boolean execute() {
                if (_targetMoveWrapper.getObject() instanceof Obj) {
                    ((Obj) _targetMoveWrapper.getObject())
                            .getComp(MoveComp.class).myTargetPos.z -= zMoveFactor;
                    return true;
                }
                return false;
            }
        }, "Obj Down");
        guiSetup.addButtonToBottomView(new Command() {

            @Override
            public boolean execute() {
                if (_targetMoveWrapper.getObject() instanceof Obj) {
                    ((Obj) _targetMoveWrapper.getObject())
                            .getComp(MoveComp.class).myTargetPos.z += zMoveFactor;
                    return true;
                }
                return false;
            }
        }, "Obj up");
        guiSetup.addButtonToBottomView(new Command() {

            @Override
            public boolean execute() {
                if (getMyRenderer() != null) {
                    lightsOnOff = !lightsOnOff;
                    getMyRenderer().setUseLightning(lightsOnOff);
                    return true;
                }
                return false;
            }
        }, "Lights on/of");

        guiSetup.addButtonToBottomView(new Command() {
            @Override
            public boolean execute() {
                Log.d(LOG_TAG, "<");
                if (_selectedObj != null && _selectedObj.getMeshComp() != null) {
                    Vec rotation = _selectedObj.getMeshComp().getRotation();
                    rotation.rotateAroundXAxis(-15);
                    Log.d(LOG_TAG, "rotation: " + rotation);
                    _selectedObj.getMeshComp().setRotation(rotation);
                    //return  true;
                }
                return true;
            }
        }, " < ");

        guiSetup.addButtonToBottomView(new Command() {
            @Override
            public boolean execute() {
                Log.d(LOG_TAG, ">");
                if (_selectedObj != null) {
                    MeshComponent m1 = _selectedObj.getMeshComp();
                    MeshComponent m2 = _selectedObj.getGraphicsComponent();
                    Vec rotation = m1.getRotation();
                    if (rotation == null)
                        rotation = new Vec(0, 0, 0);
                    rotation.rotateAroundXAxis(15);
                    Log.d(LOG_TAG, "rotation: " + rotation);
                    m1.setRotation(rotation);
                    return true;
                }
                return false;
            }
        }, " > ");
        */
        //endregion


        guiSetup.addButtonToLeftView(new Command() {

            @Override
            public boolean execute() {
                Intent intent = new Intent(getActivity().getApplicationContext(), chooser.class);
                getActivity().startActivityForResult(intent, 0);
                return true;
            }

        }, " + ");

        //region --- old code ---
        /*guiSetup.addButtonToBottomView(new Command() {

               @Override
               public boolean execute() {
                   Intent intent = new Intent(getActivity().getApplicationContext(), MapView.class);
                   getActivity().startActivityForResult(intent, 0);
                   return  true;
               }
           }, "-"
        );*/

/*        guiSetup.addImangeButtonToRightView(R.drawable.cam_yellow, new Command() {
            @Override
            public boolean execute() {
                try {
                    //myCameraView.takePhoto();
                    getMyRenderer().takeScreenShot(myCameraView);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return false;
                }
                return true;
            }
        });*/
        //endregion


        /*_about = create*/

        _cameraButton = createButtonImageWithTransparentBackground(getActivity().getApplicationContext(),
                R.drawable.cam_yellow, R.drawable.cam_green, new Command() {
                    @Override
                    public boolean execute() {
                        showDialog("POSALJI FOTOGRAFIJU NA SARAJEVO CLOUD FACEBOOK?",
                                new Command() {
                                    @Override
                                    public boolean execute() {
                                        try {
                                            getMyRenderer().takeScreenShot(myCameraView, Spremnik.getInstance().get_slikaPath());
                                            showMessage("POSTAVLjANjE NA FACEBOOK JOS NIJE IMPLEMENTIRANO.");
                                            pictureHandler.postDelayed(pictureRunnable, 100);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                            return true;
                                        }
                                        return false;
                                    }
                                },
                                new Command() {
                                    @Override
                                    public boolean execute() {
                                        try {
                                            getMyRenderer().takeScreenShot(myCameraView, Spremnik.getInstance().get_slikaPath());
                                            pictureHandler.postDelayed(pictureRunnable, 100);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                        return  true;
                    }
                });


        View lijeviMeni = createButtonImageWithTransparentBackground(getActivity(),
                R.drawable.gornji_lijevi_button_zuto, R.drawable.gornji_lijevi_button_zeleno,
                new Command() {
                    boolean visible = true;
                    @Override
                    public boolean execute() {
                        if(visible){
                            visible = false;
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(128,0,0,0));
                            _titleBar.setBackgroundColor(Color.argb(0, 0, 0, 0));
                            _cameraButton.setVisibility(View.GONE);
                            _messageBox.setVisibility(View.VISIBLE);
                            _messageBox_TextView.setVisibility(View.VISIBLE);
                            //_messageBox_TextView.setText("LIJEVI TEXT");
                            _leftMenu.setVisibility(View.VISIBLE);
                        }else {
                            visible = true;
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0,0,0,0));
                            _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
                            _cameraButton.setVisibility(View.VISIBLE);
                            _messageBox_TextView.setVisibility(View.GONE);
                            _messageBox.setVisibility(View.GONE);
                            _leftMenu.setVisibility(View.GONE);
                        }
                        return true;
                    }
                });
        lijeviMeni.setPadding(15, 15, 45, 15);
        TextView naslov = new TextView(getActivity());
        naslov.setPadding(0, 15, 0, 15);
        naslov.setTypeface(defaultFont);
        naslov.setTextColor(Color.rgb(242, 229, 0));
        naslov.setTextSize(19);
        naslov.setText("SARAJEVO CLOUD");
        View desniMeni = createImageWithTransparentBackground(getActivity(),
                R.drawable.gornji_desni_meni_zuto, R.drawable.gornji_desni_meni_zelen,
                new Command() {
                    boolean visible = true;
                    @Override
                    public boolean execute() {
                        if(visible){
                            visible = false;
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(128,0,0,0));
                            _titleBar.setBackgroundColor(Color.argb(0, 0, 0, 0));
                            _cameraButton.setVisibility(View.GONE);
                            _messageBox.setVisibility(View.VISIBLE);
                            _rightMenu.setVisibility(View.VISIBLE);

                        }else {
                            visible = true;
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0,0,0,0));
                            _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
                            _cameraButton.setVisibility(View.VISIBLE);
                            _rightMenu.setVisibility(View.GONE);
                        }

                        return true;
                    }
                });
        desniMeni.setRight((int) getScreenHeigth() - 15);
        desniMeni.setPadding(55, 15, 15, 15);

        // Creating a new RelativeLayout
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        // Defining the layout parameters of the TextView
        RelativeLayout.LayoutParams lp_l = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp_c = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp_d = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_c.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp_d.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_d.addRule(RelativeLayout.CENTER_VERTICAL);

        // Setting the parameters on the TextView
        lijeviMeni.setLayoutParams(lp_l);
        naslov.setLayoutParams(lp_c);
        desniMeni.setLayoutParams(lp_d);

        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(lijeviMeni);
        relativeLayout.addView(naslov);
        relativeLayout.addView(desniMeni);
        relativeLayout.setMinimumWidth((int) getScreenHeigth());

        _titleBar.addView(relativeLayout, rlp);

        _messageBox_TextView.setPadding(0, 13, 0, 17);
        _messageBox_TextView.setTypeface(defaultFont);
        _messageBox_TextView.setWidth((int) getScreenHeigth());

        _messageBox_yesButton = new ImageView(getActivity());
        _messageBox_yesButton.setImageResource(R.drawable.yes_first);
        _messageBox_yesButton.setPadding(25, 25, 35, 30);
        _messageBox_noButton = new ImageView(getActivity());
        _messageBox_noButton.setImageResource(R.drawable.no_first);
        _messageBox_noButton.setPadding(35,25,25,30);

        _messageBox_buttons.addView(_messageBox_yesButton);
        _messageBox_buttons.addView(_messageBox_noButton);

        _rightFotografije = new Button(getActivity());
        _rightFotografije.setPadding(25, 25, 35, 30);
        _rightFotografije.setText("FOTOGRAFIJE");
        _rightFotografije.setBackgroundColor(0);
        _rightFotografije.setTypeface(defaultFont);
        _rightFotografije.setTextColor(Color.rgb(242, 229, 0));

        _rightInfo = new Button(getActivity());
        _rightInfo.setPadding(25, 25, 35, 30);
        _rightInfo.setText("INFO");
        _rightInfo.setBackgroundColor(0);
        _rightInfo.setTypeface(defaultFont);
        _rightInfo.setTextColor(Color.rgb(242, 229, 0));

        _rightAbout = new Button(getActivity());
        _rightAbout.setPadding(25, 25, 35, 30);
        _rightAbout.setBackgroundColor(0);
        _rightAbout.setText("ABOUT");
        _rightAbout.setTypeface(defaultFont);
        _rightAbout.setTextColor(Color.rgb(242, 229, 0));

        _rightMenu.addView(_rightInfo);
        _rightMenu.addView(_rightFotografije);
        _rightMenu.addView(_rightAbout);
        _rightMenu.setPadding(80,0,50,50);

        _ivPlus= new ImageView(getActivity());
        _ivPlus.setImageResource(R.drawable.plus_zuto);
        //_ivPlus.setPadding(0,120,0,0);
        getGuiSetup().addViewToRight(_ivPlus);///

        //getGuiSetup().getRightView().setPadding(30,30,30,30);

        guiSetup.addViewToRight(_cameraButton);///

        _ivReload = new ImageView(getActivity());
        _ivReload.setImageResource(R.drawable.reload_zuto);
        //_ivReload.setPadding(0,120,0,0);
        getGuiSetup().addViewToRight(_ivReload);////
        getGuiSetup().getRightView().setWeightSum(100);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 24f);
        getGuiSetup().getRightView().getChildAt(1).setLayoutParams(params);
        getGuiSetup().getRightView().getChildAt(1).setPadding(0,0,0,60);
        //getGuiSetup().getRightView().getChildAt(2).setLayoutParams(params);
        getGuiSetup().getRightView().getChildAt(3).setLayoutParams(params);
        getGuiSetup().getRightView().getChildAt(3).setPadding(0,60,0,0);
        getGuiSetup().getRightView().setGravity(Gravity.CENTER_VERTICAL);

        /*_ivReload.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.f));
        _ivPlus.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.f));
        _cameraButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.f));*/
        RelativeLayout _relative_right = new RelativeLayout(getActivity());
        //_relative_right.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );


       /* _relative_right.addView(_ivPlus, lp);
        //relative_right.getChildAt(0).setLayoutParams(new RelativeLayout.LayoutParams(100,250));
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        _relative_right.addView(_cameraButton, lp);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM);
        _relative_right.addView(_ivReload, lp);
        getGuiSetup().addViewToRight(_relative_right);*/
        //getGuiSetup().getRightView().getChildAt(1);

        _rightInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Swipes.class);
                getActivity().startActivity(i);
                getActivity().finish();
            }
        });

        _rightFotografije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String secStore = System.getenv("SECONDARY_STORAGE");
                File file = new File(secStore + "/DCIM");
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i, 1);//android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                /*
                String secStore = System.getenv("SECONDARY_STORAGE");
                File file = new File(secStore + "/DCIM");
                 */
            }
        });

        _leftMojCloud = new Button(getActivity());
        _leftMojCloud.setText("MOJ CLOUD");
        _leftMojCloud.setBackgroundColor(0);
        _leftMojCloud.setTypeface(defaultFont);
        _leftMojCloud.setTextColor(Color.rgb(242, 229, 0));

        _leftSarajevoCloud = new Button(getActivity());
        _leftSarajevoCloud.setText("SARAJEVO CLOUD");
        _leftSarajevoCloud.setBackgroundColor(0);
        _leftSarajevoCloud.setTypeface(defaultFont);
        _leftSarajevoCloud.setTextColor(Color.rgb(242, 229, 0));

        LinearLayout _llMojCloud = new LinearLayout(getActivity());
        _llMojCloud.setOrientation(LinearLayout.HORIZONTAL);
        _llMojCloud.setWeightSum(100);
        _ivMojCloud = new ImageView(getActivity());
        _ivMojCloud.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.gornji_desni_meni_zuto));
        _llMojCloud.addView(_ivMojCloud);
        _llMojCloud.addView(_leftMojCloud);


        LinearLayout _llSarajevoCloud = new LinearLayout(getActivity());
        _llSarajevoCloud.setOrientation(LinearLayout.HORIZONTAL);
        _llSarajevoCloud.setWeightSum(100);
        _ivSarajevoCloud = new ImageView(getActivity());
        _ivSarajevoCloud.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.gornji_lijevi_button_zuto));
        _llSarajevoCloud.addView(_ivSarajevoCloud);
        _llSarajevoCloud.addView(_leftSarajevoCloud);

        TextView _tvModovi = new TextView(getActivity());
        _tvModovi.setText("MODOVI");
        _tvModovi.setTypeface(defaultFont);
        _tvModovi.setTextColor(Color.rgb(242, 229, 0));

        _leftMenu.addView(_tvModovi);
        _leftMenu.addView(_llMojCloud);
        _leftMenu.addView(_llSarajevoCloud);

        showMessage("Dobro dosli " + Spremnik.getInstance().getUserName());
    }

    public void showMessage(String text) {
        _messageBox_TextView.setText(text);
        _messageBox.setVisibility(View.VISIBLE);
        _messageBox_buttons.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                _messageBox.setVisibility(View.GONE);
            }
        }, 5000);
    }

    public void showDialog(String text, final Command yesCallback, final Command noCallback){
        _messageBox_TextView.setText(text);
        _messageBox.setVisibility(View.VISIBLE);
        _messageBox_buttons.setVisibility(View.VISIBLE);
        _messageBox_yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _messageBox_yesButton.setImageResource(R.drawable.yes_second);
                if (vibrateCommand != null)
                    vibrateCommand.execute();
                if (yesCallback != null)
                    yesCallback.execute();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                _messageBox_yesButton.setImageResource(R.drawable.yes_first);
                                                _messageBox.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }, 500);
            }
        });
        _messageBox_noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _messageBox_noButton.setImageResource(R.drawable.no_second);
                if (vibrateCommand != null)
                    vibrateCommand.execute();
                if (noCallback != null)
                    noCallback.execute();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                _messageBox_noButton.setImageResource(R.drawable.no_first);
                                                _messageBox.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }, 500);
            }
        });
    }

    @Override
    public void _d_addElementsToUpdateThread(SystemUpdater updater) {
        super._d_addElementsToUpdateThread(updater);
        Log.i(LOG_TAG, "entering _d_addElementsToUpdateThread");

    }

    @Override
    public void _f_addInfoScreen(InfoScreenSettings infoScreenData) {
        super._f_addInfoScreen(infoScreenData);
        Log.i(LOG_TAG, "entering _f_addInfoScreen");

        //infoScreenData.addText("Loading...");
    }

    //endregion

    private String IspisLokacije(Location l) {
        String lat = Double.toString(l.getLatitude()), lon = Double.toString(l.getLongitude());
        if(lat.length() > 8)
            lat  = lat.substring(0,9);
        if(lon.length() > 8)
            lon = lon.substring(0,9);
        return "{ " + lat + "; " + lon + "; " + Double.toString(l.getAltitude()) + " }";
    }

    private Obj newObject(String newObjectFilename, String newObjectTexturename) {
        Location loc = camera.getGPSLocation();
        loc.setAltitude(0);
        final Obj newObject = new GeoObj(loc);

        new ModelLoader(_localRenderer, newObjectFilename, newObjectTexturename) {
            @Override
            public void modelLoaded(MeshComponent gdxMesh) {
                gdxMesh.setColor(gl.Color.blueTransparent());
                newObject.setComp(gdxMesh);
            }
        };
        Utility.SaveThisPiktogram(loc);
        return newObject;
    }

    private Obj loadedObject(String newObjectFilename, String newObjectTexturename, final Location location) {
        final GeoObj x = new GeoObj(location);

        final ModelLoader model = new ModelLoader(_localRenderer, newObjectFilename, newObjectTexturename) {
            @Override
            public void modelLoaded(MeshComponent gdxMesh) {
                gdxMesh.setColor(gl.Color.blueTransparent());
                x.setComp(gdxMesh);
            }
        };
        world.add(x);
        return x;
    }

    private boolean savePiktogramToDb(){
        return  false;
    }

    private void setComps(Obj obj) {
        if (_selectedObj != null) {
            _selectedObj.remove(_viewPosCalcer);
            _selectedObj.remove(_moveComp);
            _selectedObj.getPosition();
            obj.getPosition();
        }
        if (obj != null) {
            obj.setComp(_viewPosCalcer);
            obj.setComp(_moveComp);
        }
        _selectedObj = obj;
    }

    private void setStatic(Obj obj) {
        if (_selectedObj != null) {
            _selectedObj.remove(_viewPosCalcer);
            _selectedObj.remove(_moveComp);
        }
        if (obj != null) {
            obj.remove(_viewPosCalcer);
            obj.remove(_moveComp);
        }
        _targetMoveWrapper.setTo(false);
        _selectedObj = null;
    }

    //endregion

    Handler checkNewPiktogramHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            List<Piktogram> noviPiktogrami = Utility.GetNewPiktograms(getActivity());

            if(noviPiktogrami!=null && noviPiktogrami.size() > 0){
                for (Piktogram p : noviPiktogrami) {
                    //loadedObject(p.getPutPiktogram(), p.getPutTekstura(), p.);
                }
            }

            checkNewPiktogramHandler.postDelayed(this, 1000);
        }
    };

    Handler pictureHandler = new Handler();
    Runnable pictureRunnable = new Runnable() {

        @Override
        public void run() {
            String slikaPath = Spremnik.getInstance().get_slikaPath().get();
            if(slikaPath!= null && !slikaPath.equals("")){
                Spremnik.getInstance().get_slikaPath().getAndSet("");
                Utility.uploadScreenshoot(ModelLoaderSetup.this, slikaPath, camera.getGPSLocation());
            }else{
                pictureHandler.postDelayed(this, 100);
            }
        }
    };


    @Override
    public void onResume(Activity a) {
        super.onResume(a);
        String objPut = Spremnik.getInstance().getObjekatPut(),
                tekPut = Spremnik.getInstance().getTeksturaPut();

        if(objPut!=null && tekPut!=null) {
            Spremnik.getInstance().setObjekatPut(null);
            Spremnik.getInstance().setTeksturaPut(null);

            //fileName = objPut;
            //textureName = tekPut;
            //newObject(objPut, tekPut);
            loadedObject(objPut, tekPut, camera.getGPSLocation());
            showMessage("OBJEKAT POSTAVLjEN I MEMORISAN");
        }
    }

    @Override
    public void onStart(Activity a) {
        super.onStart(a);}

    public View createButtonImageWithTransparentBackground(Context context, final int normalImageId, final int clickedImageId,
                                                           final Command command){
        final View imgButton = new ImageButton(context);
        imgButton.setBackgroundColor(0);
        imgButton.setBackgroundResource(normalImageId);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgButton.setBackgroundResource(clickedImageId);
                if (vibrateCommand != null)
                    vibrateCommand.execute();
                if (command != null)
                    command.execute();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                imgButton.setBackgroundResource(normalImageId);
                                            }
                                        });
                            }
                        }, 500);
            }
        });
        return imgButton;
    }public View createImageWithTransparentBackground(Context context, final int normalImageId, final int clickedImageId,
                                                      final Command command){
        final ImageView imgButton = new ImageView(context);
        imgButton.setBackgroundColor(0);
        imgButton.setImageResource(normalImageId);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgButton.setImageResource(clickedImageId);
                if (vibrateCommand != null)
                    vibrateCommand.execute();
                if (command != null)
                    command.execute();
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                imgButton.setImageResource(normalImageId);
                                            }
                                        });
                            }
                        }, 500);
            }
        });
        return imgButton;
    }
}
