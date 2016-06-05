package de.rwth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import actions.Action;
import actions.ActionPlaceObject;
import actions.ActionRotateCameraBuffered;
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

    //private String textureName;
    //private String fileName;
    private ViewPosCalcerComp _viewPosCalcer;
    private Obj _selectedObj;
    private MoveComp _moveComp;

    private Button _rightInfo, _rightFotografije, _rightAbout, _leftMojCloud, _leftSarajevoCloud;
    private ImageView _ivPlus, _ivReload, _ivMojCloud, _ivSarajevoCloud;

    private LinearLayout  _rightMenu,_leftMenu;
    private LinearLayout _messageBox, _popupWindow, _loader;
    private LinearLayout _titleBar;
    private TextView _messageBox_TextView;
    private LinearLayout _messageBox_buttons,  _piktogramChooser_piktogramRows;
    ImageView _messageBox_yesButton,
            _messageBox_noButton;
    View _cameraButton, _lijeviMeni_btn, _desniMeni_btn;
    RelativeLayout _mojCloud_commands;
    View _about;
    ImageView _thumbnailImage;
    TextView _naslov_txt, _loader_text;
    ProgressBar _loader_loader, _piktogramChooser_loader;
    ScrollView _piktogramChooser;

    Typeface defaultFont;

    private boolean modeSarajevoCloud=true;

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
    public void _b_addWorldsToRenderer(GL1Renderer renderer,
                                       GLFactory objectFactory, GeoObj currentPosition) {
        super._b_addWorldsToRenderer(renderer, objectFactory, currentPosition);
        Log.i(LOG_TAG, "entering _b_addWorldsToRenderer");
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
        Action rot2 = new ActionPlaceObject(super.camera, _targetMoveWrapper, 300);

        eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
        eventManager.addOnOrientationChangedAction(rot2);
        //eventManager.addOnLocationChangedAction(
        //        new ActionCalcRelativePos(world, camera));

        updater.addObjectToUpdateCycle(rotateGLCameraAction);
        updater.addObjectToUpdateCycle(rot2);

        //eventManager.addOnLocationChangedAction(new ActionMoveCameraBuffered(camera, 25, 5));
        //eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(super.camera, 5, 25));
        /*eventManager.addOnTrackballAction(new TrackBallEventListener() {
            @Override
            public boolean onTrackballEvent(float x, float y, MotionEvent event) {
                Log.d(LOG_TAG, "TRACKBALL x: " + x + ", y=" + y);
                if (_targetMoveWrapper.getObject() instanceof Obj) {
                    Vec rotation = _selectedObj.getMeshComp().getRotation();
                    if (x > 0)
                        rotation.rotateAroundXAxis(15);
                    else if (x < 0)
                        rotation.rotateAroundXAxis(-15);
                    Log.d(LOG_TAG, "rotation: " + rotation);
                    ((Obj) _targetMoveWrapper.getObject())
                            .getComp(MeshComponent.class).setRotation(rotation);
                    return true;
                }
                return true;
            }
        });*/

        /*ActionWaitForAccuracy _minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 20.0f, 10) {
            @Override
            public void minAccuracyReachedFirstTime(Location l,
                                                    ActionWaitForAccuracy a) {
                callAddObjectsToWorldIfNotCalledAlready();
                if (!EventManager.getInstance().getOnLocationChangedAction().remove(a)) {
                    Log.e(LOG_TAG,
                            "Could not remove minAccuracyAction from the onLocationChangedAction list");
                }
            }
        };*/
        callAddObjectsToWorldIfNotCalledAlready();
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

        initMessageBox(guiSetup);

        _titleBar = guiSetup.getTopView();
        _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
        _titleBar.setMinimumWidth((int) getScreenHeigth());
        _titleBar.setTop(0);

        _rightMenu =  new LinearLayout(getActivity());
        getGuiSetup().addViewToRight(_rightMenu);
        _rightMenu.setVisibility(View.GONE);
        _rightMenu.setOrientation(LinearLayout.VERTICAL);
        _rightMenu.setBackgroundColor(Color.argb(128, 0, 0, 0));

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
        }, "Obj up");*/
        /*guiSetup.addButtonToBottomView(new Command() {

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

        /*guiSetup.addButtonToLeftView(new Command() {

            @Override
            public boolean execute() {
                Intent intent = new Intent(getActivity().getApplicationContext(), chooser.class);
                getActivity().startActivityForResult(intent, 0);
                return true;
            }

        }, " + ");
*/
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
                        try {
                            getMyRenderer().takeScreenShot(myCameraView, Spremnik.getInstance().get_slikaPath());
                            //takeScreenshot();
                            pictureHandler.postDelayed(pictureRunnable, 100);
                        } catch (Throwable t) {
                            t.printStackTrace();
                            return true;
                        }
                        return  true;
                    }
                });


        _lijeviMeni_btn = createButtonImageWithTransparentBackground(getActivity(),
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

                            _rightMenu.setVisibility(View.GONE);
                            //_ivReload.setVisibility(View.INVISIBLE);
                            //_ivPlus.setVisibility(View.INVISIBLE);
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
        _lijeviMeni_btn.setPadding(1, 5, 75, 45);
        _naslov_txt = new TextView(getActivity());
        _naslov_txt.setPadding(0, 15, 0, 15);
        _naslov_txt.setTypeface(defaultFont);
        _naslov_txt.setTextColor(Color.rgb(242, 229, 0));
        _naslov_txt.setTextSize(19);
        _naslov_txt.setText("SARAJEVO CLOUD");
        _desniMeni_btn = createImageWithTransparentBackground(getActivity(),
                R.drawable.gornji_desni_meni_zuto, R.drawable.gornji_desni_meni_zelen,
                new Command() {
                    boolean visible = true;
                    @Override
                    public boolean execute() {
                        if(visible){
                            visible = false;
                            //guiSetup.getMainContainerView().setBackgroundColor(Color.argb(128,0,0,0));
                            _titleBar.setBackgroundColor(Color.argb(0, 0, 0, 0));
                            _cameraButton.setVisibility(View.GONE);
                            _messageBox.setVisibility(View.VISIBLE);
                            _rightMenu.setVisibility(View.VISIBLE);

                            _ivReload.setVisibility(View.GONE);
                            _ivPlus.setVisibility(View.GONE);
                            getGuiSetup().getRightView().setGravity(Gravity.TOP);

                        }else {
                            visible = true;
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0,0,0,0));
                            _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
                            _cameraButton.setVisibility(View.VISIBLE);
                            _rightMenu.setVisibility(View.GONE);

                            if(modeSarajevoCloud){

                            }
                            else{
                                _ivReload.setVisibility(View.VISIBLE);
                                _ivPlus.setVisibility(View.VISIBLE);
                            }

                            getGuiSetup().getRightView().setGravity(Gravity.CENTER_VERTICAL);
                        }

                        return true;
                    }
                });
        _desniMeni_btn.setRight((int) getScreenHeigth() - 15);
        _desniMeni_btn.setPadding(55, 15, 15, 15);

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
        _lijeviMeni_btn.setLayoutParams(lp_l);
        _naslov_txt.setLayoutParams(lp_c);
        _desniMeni_btn.setLayoutParams(lp_d);

        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(_lijeviMeni_btn);
        relativeLayout.addView(_naslov_txt);
        relativeLayout.addView(_desniMeni_btn);
        relativeLayout.setMinimumWidth((int) getScreenHeigth());

        _titleBar.addView(relativeLayout, rlp);

        _popupWindow = new LinearLayout(getActivity());
        _thumbnailImage = new ImageView(getActivity());

        _popupWindow.addView(_thumbnailImage);

        RelativeLayout mainView = (RelativeLayout)guiSetup.getMainContainerView();
        RelativeLayout.LayoutParams lp_popup = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //lp_thumbnail.addRule(RelativeLayout.CENTER_VERTICAL);
        //lp_thumbnail.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int H = (int)getScreenWidth(),
                W=(int)getScreenHeigth();
        int h1 = (int)(0.13*H),
                h2 = (int)(0.22*H),
                dW=(int)(0.175*W);
        //mainView.addView(_thumbnailImage, lp_popup);
        mainView.addView(_popupWindow, lp_popup);
        _popupWindow.getLayoutParams().height = (int)(H*0.65);
        _popupWindow.getLayoutParams().width = (int) (W*0.65);
        lp_popup.setMargins(dW, h1, dW, h2);
        _popupWindow.requestLayout();
        hidePopup();
        mainView.requestLayout();

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
        _rightMenu.setPadding(80, 0, 50, 50);

        guiSetup.setRightViewCentered();
        _ivPlus= (ImageView)createImageWithTransparentBackground(getActivity(),
                R.drawable.plus_zuto, R.drawable.plus_zeleno, new Command() {
                    @Override
                    public boolean execute() {
                        //Intent intent = new Intent(getActivity().getApplicationContext(), chooser.class);
                        //getActivity().startActivityForResult(intent, 0);
                        showPiktogramChooser();
                        return true;
                    }
                });
        _ivPlus.setPadding(0, 120, 0, 0);
        getGuiSetup().addViewToRight(_ivPlus);

        //getGuiSetup().getRightView().setPadding(30,30,30,30);

        guiSetup.addViewToRight(_cameraButton);///

        _ivReload = (ImageView)createImageWithTransparentBackground(getActivity(),
                R.drawable.reload_zuto, R.drawable.reload_zeleno,
                new Command() {
                    @Override
                    public boolean execute() {
                        world.clear();
                        return true;
                    }
                });
        //_ivReload.setPadding(0,120,0,0);
        getGuiSetup().addViewToRight(_ivReload);////
        getGuiSetup().getRightView().setWeightSum(100);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
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
        /*_ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), chooser.class);
                getActivity().startActivityForResult(intent, 0);
            }
        });*/

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
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        _leftMojCloud.setTextSize(26);

        _leftSarajevoCloud = new Button(getActivity());
        _leftSarajevoCloud.setText("SARAJEVO CLOUD");
        _leftSarajevoCloud.setBackgroundColor(0);
        _leftSarajevoCloud.setTypeface(defaultFont);
        _leftSarajevoCloud.setTextColor(Color.rgb(242, 229, 0));
        _leftSarajevoCloud.setTextSize(26);

        LinearLayout _llMojCloud = new LinearLayout(getActivity());
        _llMojCloud.setOrientation(LinearLayout.HORIZONTAL);
        _llMojCloud.setWeightSum(100);
        _ivMojCloud = new ImageView(getActivity());
        _ivMojCloud.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.moj_cloud_icon_zuto));
        _llMojCloud.addView(_ivMojCloud);
        _llMojCloud.addView(_leftMojCloud);


        LinearLayout _llSarajevoCloud = new LinearLayout(getActivity());
        _llSarajevoCloud.setOrientation(LinearLayout.HORIZONTAL);
        _llSarajevoCloud.setWeightSum(100);
        _ivSarajevoCloud = new ImageView(getActivity());
        _ivSarajevoCloud.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.sarajevo_cloud_icon_zuto));
        _llSarajevoCloud.addView(_ivSarajevoCloud);
        _llSarajevoCloud.addView(_leftSarajevoCloud);

        LinearLayout.LayoutParams paramsSC = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //paramsSC.weight = 30.0f;
        paramsSC.gravity = Gravity.CENTER_VERTICAL;
        _ivSarajevoCloud.setLayoutParams(paramsSC);

        TextView _tvModovi = new TextView(getActivity());
        _tvModovi.setText("MODOVI");
        _tvModovi.setTypeface(defaultFont);
        _tvModovi.setTextColor(Color.rgb(242, 229, 0));
        _tvModovi.setTextSize(26);

        _leftMenu.addView(_tvModovi);
        //_leftMenu.getChildAt(1).setPadding(60,70,80,90);
        _leftMenu.addView(_llMojCloud);
        _leftMenu.addView(_llSarajevoCloud);

        _leftMenu.getChildAt(0).setPadding(60,40,0,0);//ltrb
        _leftMenu.getChildAt(1).setPadding(80,50,0,0);
        _leftMenu.getChildAt(2).setPadding(80,40,0,0);

        getGuiSetup().getRightView().getChildAt(1).setVisibility(View.GONE);
        getGuiSetup().getRightView().getChildAt(3).setVisibility(View.GONE);


        _leftMojCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeSarajevoCloud = false;
                _naslov_txt.setText("MOJ CLOUD");
                getGuiSetup().getRightView().getChildAt(1).setVisibility(View.VISIBLE);
                getGuiSetup().getRightView().getChildAt(3).setVisibility(View.VISIBLE);
                //_leftMenu.setVisibility(View.GONE);
                guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0,0,0,0));
                _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
                _cameraButton.setVisibility(View.VISIBLE);
                _messageBox_TextView.setVisibility(View.GONE);
                _messageBox.setVisibility(View.GONE);
                _leftMenu.setVisibility(View.GONE); //TODO: when mycloud clicked
            }
        });

        _leftSarajevoCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeSarajevoCloud = true;
                _naslov_txt.setText("SARAJEVO CLOUD");
                getGuiSetup().getRightView().getChildAt(1).setVisibility(View.GONE);
                getGuiSetup().getRightView().getChildAt(3).setVisibility(View.GONE);
                //_leftMenu.setVisibility(View.GONE);
                guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0,0,0,0));
                _titleBar.setBackgroundColor(Color.argb(128, 0, 0, 0));
                _cameraButton.setVisibility(View.VISIBLE);
                _messageBox_TextView.setVisibility(View.GONE);
                _messageBox.setVisibility(View.GONE);
                _leftMenu.setVisibility(View.GONE);//TODO: when changed from my to sarajevo
            }
        });


        initPiktogramChooser();

        showMessage("Dobro dosli " + Spremnik.getInstance().getUserName());
        //checkNewPiktogramHandler.postDelayed(checkNewPiktogramRunnable, 0);
    }

    private void initPiktogramChooser(){
        Context ctx = getActivity();
        _piktogramChooser = new ScrollView(ctx);
        _piktogramChooser_piktogramRows = new LinearLayout(ctx);
        _piktogramChooser_piktogramRows.setOrientation(LinearLayout.VERTICAL);
        _piktogramChooser_piktogramRows.setPadding(0,15,0,10);

        _piktogramChooser_loader = new ProgressBar(ctx);

        _piktogramChooser.addView(_piktogramChooser_piktogramRows);
        _piktogramChooser_loader.setVisibility(View.GONE);

        _messageBox.addView(_piktogramChooser);
    }

    private void showPiktogramChooser(){
        if(_messageBox.getVisibility() == View.VISIBLE){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPiktogramChooser();
                }
            }, 500);
            return;
        }
        final List<Set> _setovi_lista = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                _piktogramChooser_piktogramRows.removeAllViews();
                _piktogramChooser_loader.setVisibility(View.VISIBLE);
                _piktogramChooser.getLayoutParams().height = (int)(getScreenWidth()*0.6);
                _piktogramChooser.getLayoutParams().width = (int)(getScreenHeigth());
                _piktogramChooser.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                (int)(getScreenHeigth()),
                                (int)(getScreenWidth()*0.6)
                        )
                );
                _piktogramChooser.setVisibility(View.VISIBLE);
                _messageBox.setVisibility(View.VISIBLE);
                _messageBox_TextView.setVisibility(View.GONE);
                _messageBox_buttons.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String json = Utility.GET(Spremnik.getInstance().getSetServiceAddress());
                    android.util.Log.d(LOG_TAG, "json: " + json);
                    JSONArray setovi = new JSONArray(json);
                    for (int i = 0; i < setovi.length(); i++) {
                        JSONObject set = setovi.getJSONObject(i);
                        android.util.Log.d(LOG_TAG, "set(" + i + "): " + set.toString());
                        _setovi_lista.add(new Set(set.getInt("id"), set.getString("naziv")));
                        dobaviPiktograme(getActivity(), set.getString("naziv"), Integer.toString(set.getInt("id")));
                    }
                } catch (org.json.JSONException je) {
                    Log.e("LOG_piktogramchooser", je.toString());
                } catch (Throwable ex) {
                    android.util.Log.d(LOG_TAG, "error: " + ex.getMessage(), ex);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }

            void dobaviPiktograme(Context ctx, final String setName, String setId){
                List<Piktogram> _piktogram_lista = new ArrayList<Piktogram>();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView naslovSeta = new TextView(getActivity());
                            naslovSeta.setTypeface(defaultFont);
                            naslovSeta.setText(setName);
                            naslovSeta.setTextSize(19);
                            naslovSeta.setPadding(25, 7, 0, 7);
                            naslovSeta.setTextColor(Color.rgb(242, 229, 0));
                            _piktogramChooser_piktogramRows.addView(naslovSeta);
                        }
                    });
                    String json = Utility.GET(Spremnik.getInstance().getPiktogramServiceAddress() + "?setid=" + setId);
                    android.util.Log.d(LOG_TAG, "json: " + json);
                    JSONArray setovi = new JSONArray(json);
                    for (int i = 0; i < setovi.length(); i++) {

                        JSONObject set = setovi.getJSONObject(i);
                        android.util.Log.d(LOG_TAG, "set(" + i + "): " + set.toString());
                        Piktogram newPiktogram = new Piktogram(set.getInt("id"),
                                set.getString("naziv"),
                                set.getString("put_piktogram"),
                                set.getString("put_tekstura"));
                        _piktogram_lista.add(newPiktogram);
                        String tmp = set.getString("naziv") + "." + Utility.getEkstension(set.getString("put_piktogram"));
                        final String finalFileName = Utility.downloadAndSaveFile(ctx, set.getInt("id"), 0, tmp, LOG_TAG);

                        tmp = set.getString("naziv") + "." + Utility.getEkstension(set.getString("put_tekstura"));
                        final String finalThumbnail_1 = Utility.downloadAndSaveFile(ctx, set.getInt("id"), 1, tmp, LOG_TAG);
                        final String finalThumbnail_2 = Utility.downloadAndSaveFile(ctx, set.getInt("id"), 2, tmp, LOG_TAG);

                        final float W = getScreenHeigth();
                        if (i % 5 != 0) {
                            final ImageView btnImage = (ImageView)createImageWithTransparentBackground(getActivity(),
                                    finalThumbnail_1, finalThumbnail_2,
                                    new Command() {
                                        @Override
                                        public boolean execute() {
                                            newObject(finalFileName, finalThumbnail_1);
                                            _piktogramChooser.setVisibility(View.GONE);
                                            showRightBar();
                                            return false;
                                        }
                                    });
                            btnImage.setMaxWidth((int) (W * 0.18));
                            btnImage.setMaxHeight((int) (W * 0.18));
                            btnImage.setPadding((int) (W * 0.01), (int) (W * 0.01), (int) (W * 0.01), (int) (W * 0.01));

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((LinearLayout) (_piktogramChooser_piktogramRows
                                            .getChildAt(_piktogramChooser_piktogramRows.getChildCount() - 1)))
                                            .addView(btnImage);
                                    btnImage.getLayoutParams().height = (int) (W * 0.18);
                                    btnImage.getLayoutParams().width = (int) (W * 0.18);
                                    btnImage.requestLayout();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout noviRed = new LinearLayout(getActivity());
                                    noviRed.setOrientation(LinearLayout.HORIZONTAL);
                                    _piktogramChooser_piktogramRows.addView(noviRed);
                                }
                            });
                        }
                    }
                } catch (org.json.JSONException je) {
                    Log.e("PiktogramChooser", je.toString());
                } catch (Throwable ex) {
                    android.util.Log.d("PiktogramChooser", ex.getMessage(), ex);
                }
            }
        }.execute();

    }

    protected void showRightBar(){
        getGuiSetup().getRightView().setVisibility(View.VISIBLE);
    }

    protected void hideRightBar(){
        getGuiSetup().getRightView().setVisibility(View.GONE);
    }

    private void initMessageBox(GuiSetup guiSetup) {
        _loader_loader = new ProgressBar(getActivity());
        _loader_text = new TextView(getActivity());

        _loader = new LinearLayout(getActivity());
        _loader.setOrientation(LinearLayout.HORIZONTAL);
        _loader.addView(_loader_loader);
        _loader.addView(_loader_text);
        _loader.setVisibility(View.GONE);

        _messageBox_TextView = new TextView(getActivity());
        _messageBox_buttons = new LinearLayout(getActivity());

        _messageBox = guiSetup.getBottomView();
        _messageBox.setVisibility(View.GONE);
        _messageBox.setOrientation(LinearLayout.VERTICAL);
        _messageBox.setBackgroundColor(android.graphics.Color.argb(128, 0, 0, 0));
        _messageBox.addView(_messageBox_TextView);
        _messageBox.addView(_messageBox_buttons);
        _messageBox.addView(_loader);

        _messageBox_TextView.setPadding(0, 13, 0, 17);
        _messageBox_TextView.setTypeface(defaultFont);
        _messageBox_TextView.setWidth((int) getScreenHeigth());

        _messageBox_yesButton = new ImageView(getActivity());
        _messageBox_yesButton.setImageResource(R.drawable.yes_first);
        _messageBox_yesButton.setPadding(25, 25, 35, 30);
        _messageBox_noButton = new ImageView(getActivity());
        _messageBox_noButton.setImageResource(R.drawable.no_first);
        _messageBox_noButton.setPadding(35, 25, 25, 30);

        _messageBox_buttons.addView(_messageBox_yesButton);
        _messageBox_buttons.addView(_messageBox_noButton);
    }

    public void showMessage(final String text) {
        if (_messageBox.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMessage(text);
                }
            }, 500);
        } else {
            _messageBox_TextView.setText(text);
            _messageBox_TextView.setVisibility(View.VISIBLE);
            _loader.setVisibility(View.GONE);
            _messageBox.setVisibility(View.VISIBLE);
            _messageBox_buttons.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    _messageBox.setVisibility(View.GONE);
                }
            }, 5000);
        }
    }

    public void showDialog(final String text, final Command yesCallback, final Command noCallback){
        if(_messageBox.getVisibility() == View.VISIBLE){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDialog(text, yesCallback, noCallback);
                }
            }, 500);
        }else {
            _messageBox_TextView.setText(text);
            _messageBox.setVisibility(View.VISIBLE);
            _messageBox_TextView.setVisibility(View.VISIBLE);
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

    /** Snima novododani objekat
     *              @param newObjectFilename - lokacija za .obj novog piktograma
     *              @param newObjectTexturename - lokacija za teksturu novog piktograma
     **/
    private Obj newObject(String newObjectFilename, String newObjectTexturename) {
        final Obj lightObject = new Obj();
        lightObject.setPosition(new Vec(0,0,-1));
        spotLight.setPosition(new Vec(0, 0, 2));
        final MeshComponent lightGroup = new Shape();
        lightGroup.addChild(spotLight);
        lightObject.setComp(lightGroup);
        //lightObject.setComp(new MoveComp(1));
        lightObject.setComp(_moveComp);

        final ModelLoader model = new ModelLoader(_localRenderer, newObjectFilename, newObjectTexturename) {
            @Override
            public void modelLoaded(final MeshComponent gdxMesh) {
                Log.d(LOG_TAG, "Loaded mesh component from GDX");
                gdxMesh.setPosition(new Vec(-2, -2, -2));
                gdxMesh.setColor(gl.Color.greenTransparent());
                lightObject.setComp(gdxMesh);
                Log.d(LOG_TAG, "CAMERA LOCATION: " + camera.getGPSLocation().toString());
                world.add(lightObject);
                _targetMoveWrapper.setTo(lightObject);
                if (_targetMoveWrapper.getObject() instanceof Obj) {
                    ((Obj) _targetMoveWrapper.getObject())
                            .getComp(MoveComp.class).myTargetPos.z = -30f;
                }

                final MeshComponent finalGdxMesh = gdxMesh;
                gdxMesh.setOnClickCommand(new Command() {
                    @Override
                    public boolean execute() {
                        Vec v_loc = lightObject.getPosition();
                        //Location loc = camera.getGPSLocation();//TODO:check
                        Log.d(LOG_TAG, "v_loc: " + v_loc.toString());
                        //Log.d(LOG_TAG, "loc: " + loc.toString());
                        final GeoObj final3Dobj = new GeoObj(true);
                        //final3Dobj.setLocation(loc);
                        final3Dobj.setVirtualPosition(v_loc);

                        final MeshComponent lightGroup = new Shape();
                        lightGroup.addChild(spotLight);
                        final3Dobj.setComp(lightGroup);
                        finalGdxMesh.setColor(gl.Color.green());
                        final3Dobj.setComp(finalGdxMesh);
                        //final3Dobj.setComp(new MoveComp(1));
                        //final3Dobj.setVirtualPosition(new Vec(30,0,-2));
                        world.add(final3Dobj);
                        world.remove(lightObject);

                        _targetMoveWrapper.setTo(final3Dobj);
                        //if (_targetMoveWrapper.getObject() instanceof Obj) {
                        //    ((Obj) _targetMoveWrapper.getObject())
                        //            .getComp(MoveComp.class).myTargetPos.z -= 14f;
                        //}
                        setStatic(lightObject);
                        //Log.d(LOG_TAG, final3Dobj.getVirtualPosition().toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showMessage("OBJEKAT POSTAVLjEN I MEMORISAN");
                            }
                        });
                        return true;
                    }
                });
            }
        };
        //Utility.SaveThisPiktogram(loc);
        return lightObject;
    }
    //region -- old code --
        /*
        try {
            Location loc = camera.getGPSLocation();
            loc.setAltitude(0);

            Utility.SaveThisPiktogram(loc);
            final Obj newObject = new GeoObj(loc);

            new ModelLoader(_localRenderer, newObjectFilename, newObjectTexturename) {
                @Override
                public void modelLoaded(MeshComponent gdxMesh) {
                    gdxMesh.setColor(gl.Color.blueTransparent());
                    newObject.setComp(gdxMesh);
                }
            };
            world.add(newObject);
            return newObject;
        }catch (Throwable t){
            t.printStackTrace();
        }
        return  null;*/
    //endregion

    /**
     * Dodavanje postojecih objekata iz baze
     * */
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

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            myCameraView.takePhoto(bitmap, Spremnik.getInstance().get_slikaPath());

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
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
    Runnable checkNewPiktogramRunnable = new Runnable() {

        @Override
        public void run() {
            List<Piktogram> noviPiktogrami = Utility.GetNewPiktograms(getActivity());

            if(noviPiktogrami!=null && noviPiktogrami.size() > 0){
                for (Piktogram p : noviPiktogrami) {
                    Location location = new Location("GPS");
                    location.setLatitude(p.get_latitude());
                    location.setLongitude(p.get_longitude());
                    loadedObject(p.getPutPiktogram(), p.getPutTekstura(), location);
                }
            }

            checkNewPiktogramHandler.postDelayed(this, 2000);
        }
    };

    Handler pictureHandler = new Handler();
    Runnable pictureRunnable = new Runnable() {
        @Override
        public void run() {
            final String slikaPath = Spremnik.getInstance().get_slikaPath().get();
            if(slikaPath!= null && !slikaPath.equals("")){
                Spremnik.getInstance().get_slikaPath().getAndSet("");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showScreenshotThumbnail(slikaPath);
                        showDialog("POSALJI FOTOGRAFIJU NA SARAJEVO CLOUD FACEBOOK?",
                                new Command() {
                                    @Override
                                    public boolean execute() {
                                        try {
                                            new AsyncTask<Void, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(Void... params) {
                                                    try {
                                                        Utility.uploadScreenshoot(ModelLoaderSetup.this, slikaPath, camera.getGPSLocation());
                                                    }catch (Throwable t1){
                                                        Log.e("uploadScreen", t1.toString());
                                                        t1.printStackTrace();
                                                    }
                                                    return null;
                                                }
                                            }.execute().get();
                                        }catch (Throwable t) {
                                        }
                                        hidePopup();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showLoader("POSTING TO FACEBOOK");
                                            }
                                        });
                                        return false;
                                    }
                                },
                                new Command() {
                                    @Override
                                    public boolean execute() {
                                        hidePopup();
                                        return false;
                                    }
                                });
                    }
                });
            }else{
                pictureHandler.postDelayed(this, 100);
            }
        }
    };

    private void hidePopup(){
        _popupWindow.setVisibility(View.GONE);
        _thumbnailImage.setVisibility(View.GONE);
    }

    protected void showLoader(String text){
        _messageBox.setVisibility(View.VISIBLE);
        _messageBox_TextView.setVisibility(View.GONE);
        _messageBox_buttons.setVisibility(View.GONE);
        _loader.setVisibility(View.VISIBLE);
        _loader_text.setText(text);
    }

    protected void hideLoader(){
        _messageBox.setVisibility(View.GONE);
        _messageBox_TextView.setVisibility(View.GONE);
        _messageBox_buttons.setVisibility(View.GONE);
        _loader.setVisibility(View.GONE);
    }

    private void showScreenshotThumbnail(String slikaPath) {
        Bitmap thumbnail = BitmapFactory.decodeFile(slikaPath);
        _thumbnailImage.setImageBitmap(thumbnail);
        _thumbnailImage.setVisibility(View.VISIBLE);
        _popupWindow.setVisibility(View.VISIBLE);
    }


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
            newObject(objPut, tekPut);
            //loadedObject(objPut, tekPut, camera.getGPSLocation());
        }
    }

    @Override
    public void onStart(Activity a) {
        super.onStart(a);}

    @Override
    public void onStop(Activity a) {
        super.onStop(a);
        if(pictureHandler!=null && pictureRunnable!=null)
            pictureHandler.removeCallbacks(pictureRunnable);
        if(checkNewPiktogramHandler!=null && checkNewPiktogramRunnable!=null)
            checkNewPiktogramHandler.removeCallbacks(checkNewPiktogramRunnable);
    }

    @Override
    public void onDestroy(Activity a) {
        super.onDestroy(a);
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
                        }, 150);
            }
        });
        return imgButton;
    }
    public View createImageWithTransparentBackground(Context context, final int normalImageId, final int clickedImageId,
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
                        }, 150);
            }
        });
        return imgButton;
    }

    public ImageView createImageWithTransparentBackground(Context context, String normalImagePath, String clickedImagePath,
                                                      final Command command){
        final ImageView imgButton = new ImageView(context);
        try {
            imgButton.setBackgroundColor(0);
            final Bitmap normalBmp = BitmapFactory.decodeFile(normalImagePath);
            final Bitmap clickedBitmap = BitmapFactory.decodeFile(clickedImagePath);
            imgButton.setImageBitmap(normalBmp);
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imgButton.setImageBitmap(clickedBitmap);
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
                                                    imgButton.setImageBitmap(normalBmp);
                                                }
                                            });
                                }
                            }, 500);
                }
            });
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return imgButton;
    }
}

