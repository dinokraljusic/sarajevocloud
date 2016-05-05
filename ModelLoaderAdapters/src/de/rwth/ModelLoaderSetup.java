package de.rwth;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import gl.scenegraph.Shape;
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

    private String textureName;
    private String fileName;
    private ViewPosCalcerComp _viewPosCalcer;
    private Obj _selectedObj;
    private MoveComp _moveComp;

    private Button _lokacijaLabel;

    //endregion

    //region CONSTRUCTORS

    public ModelLoaderSetup(String fileName, String textureName) {
        this.fileName = fileName;
        this.textureName = textureName;
        _targetMoveWrapper = new Wrapper();

        //instantiated light here, since the method _a2_initLightning() is no longer overridden
        spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(0, 0, 0));

        try {
            Log.d(LOG_TAG,
                    "Trying to enable vibration feedback for UI actions");
            vibrateCommand = new CommandDeviceVibrate(getActivity().getApplicationContext(), VIBRATION_DURATION_IN_MS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                new ActionCalcRelativePos(super.world, camera));

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

        eventManager.registerLocationUpdates();

        ActionWaitForAccuracy _minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 25.0f, 25) {
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
                _lokacijaLabel.setText(IspisLokacije(location) + " VS " +
                        IspisLokacije(camera.getGPSLocation()));
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

        callAddObjectsToWorldIfNotCalledAlready();
    }

    @Override
    public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
        super._e2_addElementsToGuiSetup(guiSetup, activity);
        Log.i(LOG_TAG, "entering _e2_addElementsToGuiSetup");

        _lokacijaLabel = new Button(getActivity().getApplicationContext());
        guiSetup.addViewToTop(_lokacijaLabel);

        guiSetup.addButtonToBottomView(new Command() {
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

        guiSetup.addButtonToLeftView(new Command() {

            @Override
            public boolean execute() {
                //world.add(newObject());
                Intent intent = new Intent(getActivity().getApplicationContext(), chooser.class);
                //startActivity(getActivity().getApplicationContext(), intent);
                getActivity().startActivityForResult(intent, 0);
                /*String _url = "http://192.168.0.105";

                ArrayList<View> _listSetovi = new ArrayList<View>();
                ArrayAdapter<View> _adapterSetovi;
                ListView _listViewSetovi= new ListView(getActivity().getApplicationContext());

                _adapterSetovi = new ArrayAdapter<View>(getActivity().getApplicationContext(),
                        android.R.layout.simple_list_item_single_choice,
                        _listSetovi);
                _listViewSetovi.setAdapter(_adapterSetovi);

                new DobavljacSetova( getActivity().getApplicationContext(), _adapterSetovi).execute(_url, "1", "2");

                getActivity().setContentView(_listViewSetovi);
*/
                return true;
            }

        }, " + ");

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

        guiSetup.addViewToRight(createButtonImageWithTransparentBackground(getActivity().getApplicationContext(),
                R.drawable.cam_yellow, R.drawable.cam_green, new Command() {
                    @Override
                    public boolean execute() {
                        try {
                            //myCameraView.takePhoto();
                            getMyRenderer().takeScreenShot(myCameraView);
                        } catch (Throwable t) {
                            t.printStackTrace();
                            return  true;
                        }
                        return  false;
                    }
                }));
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

        infoScreenData.addText("Loading...");
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

    private Obj newObject() {
        final Obj lightObject = new Obj();
        spotLight.setPosition(new Vec(1, 1, 1));
        final MeshComponent lightGroup = new Shape();
        lightGroup.addChild(spotLight);
        lightObject.setComp(lightGroup);
        lightObject.setComp(new MoveComp(1));

        final ModelLoader model = new ModelLoader(_localRenderer, fileName, textureName) {
            @Override
            public void modelLoaded(MeshComponent gdxMesh) {
                Log.d(LOG_TAG, "Loaded mesh component from GDX");
                lightObject.setComp(gdxMesh);
                Log.d(LOG_TAG, "CAMERA LOCATION: " + camera.getGPSLocation().toString());
                world.add(lightObject);
                _targetMoveWrapper.setTo(lightObject);

                final MeshComponent finalGdxMesh = gdxMesh;
				gdxMesh.setOnClickCommand(new Command() {
					@Override
					public boolean execute() {
                        Vec v_loc = lightObject.getPosition();
                        Location loc = camera.getGPSLocation();//TODO:check
                        Log.d(LOG_TAG, "v_loc: " + v_loc.toString());
                        Log.d(LOG_TAG, "loc: " + loc.toString());
                        final GeoObj final3Dobj = new GeoObj(true);
                        final3Dobj.setLocation(loc);
                        final3Dobj.setVirtualPosition(v_loc);

                        final MeshComponent lightGroup = new Shape();
                        lightGroup.addChild(spotLight);
                        final3Dobj.setComp(lightGroup);
                        final3Dobj.setComp(finalGdxMesh);
                        //final3Dobj.setComp(new MoveComp(1));
                        world.add(final3Dobj);
                        world.remove(lightObject);

                        //_targetMoveWrapper.setTo(final3Dobj);
						//setStatic(lightObject);
                        Log.d(LOG_TAG, final3Dobj.getVirtualPosition().toString());
						return true;
					}
				});
            }
        };
        return lightObject;
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


    @Override
    public void onResume(Activity a) {
        super.onResume(a);
        String objPut = Spremnik.getInstance().getObjekatPut(),
                tekPut = Spremnik.getInstance().getTeksturaPut();

        if(objPut!=null && tekPut!=null) {
            Spremnik.getInstance().setObjekatPut(null);
            Spremnik.getInstance().setTeksturaPut(null);

            fileName = objPut;
            textureName = tekPut;
            newObject();
        }
    }

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
                if(command!=null)
                    command.execute();
                imgButton.setBackgroundResource(normalImageId);
            }
        });
        return imgButton;
    }
}