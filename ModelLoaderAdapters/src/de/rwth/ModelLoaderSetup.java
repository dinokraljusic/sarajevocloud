package de.rwth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.opengles.GL10;

import actions.Action;
import actions.ActionPlaceObject;
import actions.ActionRotateCameraBuffered;
import commands.Command;
import commands.system.CommandDeviceVibrate;
import components.ViewPosCalcerComp;
import de.rwth.GuiElements.ClickableTextView;
import de.rwth.GuiElements.CommandBar;
import de.rwth.GuiElements.DialogBox;
import de.rwth.GuiElements.LoaderBar;
import de.rwth.GuiElements.MessageBox;
import de.rwth.GuiElements.TitleBar;
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
    public static final String CREDENTIALS = "credentials.sc";

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

    private TitleBar _titleBar;
    protected MessageBox _messageBox;
    protected LoaderBar _loader;
    private DialogBox _uploadPictureDialogBox,
            _albumDialog;
    private CommandBar _commandBar;

    private ClickableTextView _rightInfo, _rightFotografije, _rightAbout;

    private LinearLayout _rightMenu,
            _piktogramChooser_piktogramRows;
    RelativeLayout _popupWindow;
    ImageView _thumbnailImage;
    ProgressBar _piktogramChooser_loader;
    ScrollView _piktogramChooser;

    Typeface defaultFont;

    private boolean modeSarajevoCloud = true;
    private int _timesBackPressed = 0;

    ImageView _fullScreenImage;
    int _fullScreenImageIndex = 0;

    Obj _selectedLightObject;
    MeshComponent _selectedMesh;
    Command _openInfoView;

    AsyncTask<Void, Object, Void> _dobaviPiktogrameAsync;
    //endregion

    //region CONSTRUCTORS

    public ModelLoaderSetup(Command openInfoView) {
        _targetMoveWrapper = new Wrapper();

        //instantiated light here, since the method _a2_initLightning() is no longer overridden
        spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(0, 0, 0));
        _openInfoView = openInfoView;
        Spremnik.getInstance().setPreviousActivity("ModelLoadersSetup");
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

        _viewPosCalcer = new ViewPosCalcerComp(camera, 110, 0.1f) {
            @Override
            public void onPositionUpdate(worldData.Updateable parent,
                                         Vec targetVec) {
                targetVec.z = -45f;
                if (parent instanceof Obj) {
                    Obj obj = (Obj) parent;
                    MoveComp m = obj.getComp(MoveComp.class);
                    if (m != null) {
                        m.myTargetPos = targetVec;
                    }
                    Vec pos = camera.getPositionOnGroundWhereTheCameraIsLookingAt();
                    if (_selectedMesh != null) {
                        Vec rot = _selectedMesh.getRotation();
                        if (rot == null) return;
                        int stepeni = (int) Math.toDegrees(Math.atan2(pos.y, pos.x));
                        if (_previousRotation != stepeni) {
                            int dz = stepeni - _previousRotation;
                            rot.z += dz;
                            _selectedMesh.setRotation(rot);
                            _previousRotation = stepeni;
                            Log.i("rotation", Integer.toString(stepeni));
                        }
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

    int _previousRotation;

    @Override
    public void _c_addActionsToEvents(EventManager eventManager,
                                      CustomGLSurfaceView arView, SystemUpdater updater) {
        Log.i(LOG_TAG, "entering _c_addActionsToEvents");

        super.rotateGLCameraAction = new ActionRotateCameraBuffered(camera);
        Action rot2 = new ActionPlaceObject(super.camera, _targetMoveWrapper, 200);

        eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
        eventManager.addOnOrientationChangedAction(rot2);
        /*eventManager.addOnOrientationChangedAction(new Action() {

            @Override
            public boolean onLocationChanged(Location location) {
                Vec pos = camera.getPositionOnGroundWhereTheCameraIsLookingAt();
                if (_selectedMesh != null) {
                    Vec rot = _selectedMesh.getRotation();
                    if (rot == null) return true;
                    int stepeni = (int) Math.toDegrees(Math.atan2(pos.y, pos.x));
                    if (_previousRotation != stepeni) {
                        int dz = stepeni - _previousRotation;
                        rot.z += dz;
                        _selectedMesh.setPosition(rot);
                        _previousRotation = stepeni;
                        Log.i("rotation", Integer.toString(stepeni));
                    }
                }
                return true;
            }
        });*/
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

        guiSetup.getBottomView().setOrientation(LinearLayout.VERTICAL);

        _loader = new LoaderBar(getActivity(), defaultFont);
        guiSetup.getBottomView().addView(_loader);
        _loader.getLayoutParams().width = (int) getScreenHeigth();

        _uploadPictureDialogBox = new DialogBox(getActivity(), R.drawable.yes_first, R.drawable.yes_second, R.drawable.no_first, R.drawable.no_second);
        _uploadPictureDialogBox.registerOnShowCommand(new Command() {
            @Override
            public boolean execute() {
                _commandBar.hide();
                _titleBar.setEnabled(false);
                _piktogramChooser.setVisibility(View.GONE);

                return true;
            }
        });
        _uploadPictureDialogBox.registerOnHideCommand(new Command() {
            @Override
            public boolean execute() {
                _messageBox.showMessage("FOTOGRAFIJA SNIMLJENA NA VAŠ UREDJAJ");
                _titleBar.setEnabled(true);
                _thumbnailImage.setVisibility(View.GONE);
                return false;
            }
        });
        _uploadPictureDialogBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _uploadPictureDialogBox.getLayoutParams().width = (int) getScreenHeigth();
        guiSetup.getBottomView().addView(_uploadPictureDialogBox);

        _albumDialog = new DialogBox(getActivity(), R.drawable.yes_first, R.drawable.yes_second,
                R.drawable.no_first, R.drawable.no_second);
        _albumDialog.registerOnShowCommand(new Command() {
            @Override
            public boolean execute() {
                _commandBar.hide();
                return true;
            }
        });
        _albumDialog.registerOnHideCommand(new Command() {
            @Override
            public boolean execute() {
                _commandBar.show(modeSarajevoCloud);
                return true;
            }
        });
        _albumDialog.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _albumDialog.getLayoutParams().width = (int) getScreenHeigth();
        guiSetup.getBottomView().addView(_albumDialog);

        guiSetup.getTopView().setOrientation(LinearLayout.VERTICAL);
        _titleBar = new TitleBar(getActivity(), (int) getScreenHeigth());
        _titleBar.showBackground();
        _titleBar.setMinimumWidth((int) getScreenHeigth());
        _titleBar.setTop(0);
        _titleBar.setTitle("SARAJEVO CLOUD");
        guiSetup.getTopView().addView(_titleBar);

        _rightMenu = new LinearLayout(getActivity());
        getGuiSetup().addViewToRight(_rightMenu);
        _rightMenu.setVisibility(View.GONE);
        _rightMenu.setOrientation(LinearLayout.VERTICAL);
        _rightMenu.setBackgroundColor(Color.argb(128, 0, 0, 0));

        _commandBar = new CommandBar(getActivity(), (int) getScreenHeigth());
        guiSetup.addViewToBottom(_commandBar);
        _commandBar.show(true);

        _commandBar.setAlbumCommand(new Command() {
            @Override
            public boolean execute() {
                _albumDialog.showDialog("POSJETITE ALBUM FOTOGRAFIJA SARAJEVO CLOUDA NA FACEBOOKU ?",
                        new Command() {
                            @Override
                            public boolean execute() {
                                Uri uri = Uri.parse("https://www.facebook.com/ACTSarajevo/photos/?tab=album&album_id=280657718934966"); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                getActivity().startActivity(intent);
                                return true;
                            }
                        },
                        new Command() {
                            @Override
                            public boolean execute() {
                                return false;
                            }
                        });
                return false;
            }
        });
        _commandBar.setRestartCommand(new Command() {
            @Override
            public boolean execute() {
                try {
                    if (world != null) {
                        world.clear();
                    } else {
                        world = new World(camera);
                    }
                    _messageBox.showMessage("SVI OBJEKTI IZBRISANI !");
                    return true;
                } catch (Throwable t) {
                }
                return false;
            }
        });
        _commandBar.setNewPiktogramCommand(new Command() {
            @Override
            public boolean execute() {
                _commandBar.hide();
                modeSarajevoCloud = false;
                showPiktogramChooser();
                return true;
            }
        });
        _commandBar.setFotoCommand(new Command() {
            @Override
            public boolean execute() {
                try {
                    _titleBar.setTitle("FOTOGRAFISANJE");
                    getMyRenderer().takeScreenShot(myCameraView, Spremnik.getInstance().get_slikaPath());
                    //takeScreenshot();
                    pictureHandler.postDelayed(pictureRunnable, 100);
                    _commandBar.hide();
                } catch (Throwable t) {
                    t.printStackTrace();
                    return true;
                }
                return true;
            }
        });
        _commandBar.setRotirajCommand(new Command() {
            @Override
            public boolean execute() {
                if (_selectedMesh != null) {
                    Vec rot = _selectedMesh.getRotation();
                    if (rot == null)
                        rot = new Vec(0, 0, 0);
                    rot.z += 22.5;
                    _selectedMesh.setRotation(rot);
                    return true;
                }
                return true;
            }
        });
        _commandBar.setPostaviCommand(new Command() {
            @Override
            public boolean execute() {
                if (_selectedMesh != null && _selectedLightObject != null) {
                    Vec v_loc = _selectedLightObject.getPosition();
                    Log.d(LOG_TAG, "v_loc: " + v_loc.toString());
                    final GeoObj final3Dobj = new GeoObj(true);
                    final3Dobj.setVirtualPosition(v_loc);

                    final MeshComponent lightGroup = new Shape();
                    lightGroup.addChild(spotLight);
                    final3Dobj.setComp(lightGroup);
                    gl.Color tmpColor = _selectedMesh.getColor();
                    tmpColor.alpha = 1;
                    _selectedMesh.setColor(tmpColor);
                    final3Dobj.setComp(_selectedMesh);
                    world.add(final3Dobj);
                    world.remove(_selectedLightObject);

                    _targetMoveWrapper.setTo(final3Dobj);
                    //setStatic(lightObject);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _messageBox.showMessage("OBJEKAT POSTAVLJEN !");
                            modeSarajevoCloud = true;
                            //_titleBar.setTitle("SARAJEVO CLOUD");
                            //_commandBar.show(modeSarajevoCloud);
                        }
                    });
                    _selectedMesh = null;
                    _selectedLightObject = null;
                    return true;
                }
                return true;
            }
        });

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

        _titleBar.setRightButtonCommand(
                new Command() {
                    @Override
                    public boolean execute() {
                        if (_rightMenu.getVisibility() == View.VISIBLE) {
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0, 0, 0, 0));
                            _rightMenu.setVisibility(View.GONE);
                        } else {
                            guiSetup.getMainContainerView().setBackgroundColor(Color.argb(0, 0, 0, 0));
                            _rightMenu.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                });

        _popupWindow = new RelativeLayout(getActivity());
        _popupWindow.setGravity(Gravity.CENTER);

        _thumbnailImage = new ImageView(getActivity());
        _popupWindow.addView(_thumbnailImage);

        _messageBox = new MessageBox(getActivity());
        _popupWindow.addView(_messageBox);
        _messageBox.registerOnShowCommand(new Command() {
            @Override
            public boolean execute() {
                _commandBar.hide();
                return false;
            }
        });
        _messageBox.registerOnHideCommand(new Command() {
            @Override
            public boolean execute() {
                _titleBar.setTitle("SARAJEVO CLOUD");
                _commandBar.show(modeSarajevoCloud);
                return false;
            }
        });

        RelativeLayout.LayoutParams lp_txtMsg = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_txtMsg.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp_txtMsg.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp_txtMsg.addRule(RelativeLayout.CENTER_VERTICAL);
        _messageBox.setLayoutParams(lp_txtMsg);
        _messageBox.setGravity(Gravity.CENTER);
        _messageBox.setPadding(0, 19, 0, 19);

        RelativeLayout mainView = (RelativeLayout) guiSetup.getMainContainerView();
        RelativeLayout.LayoutParams lp_popup = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //lp_thumbnail.addRule(RelativeLayout.CENTER_VERTICAL);
        //lp_thumbnail.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int H = (int) getScreenWidth(),
                W = (int) getScreenHeigth();
        int h1 = (int) (0.13 * H),
                h2 = (int) (0.22 * H),
                dW = (int) (0.175 * W);
        mainView.addView(_popupWindow, lp_popup);
        _popupWindow.getLayoutParams().height = (int) (H * 0.65);
        _popupWindow.getLayoutParams().width = (int) (W * 0.65);
        lp_popup.setMargins(dW, h1, dW, h2);
        _popupWindow.requestLayout();
        hidePopup();
        mainView.requestLayout();

        _fullScreenImage = new ImageView(getActivity());
        _fullScreenImage.setBackgroundColor(Color.argb(128, 0, 0, 0));
        _fullScreenImage.setImageResource(R.drawable.screen_9);

        _fullScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_fullScreenImageIndex == 0) {
                    _fullScreenImage.setImageResource(R.drawable.screen_12);
                    _fullScreenImageIndex = 1;
                } else {
                    _fullScreenImage.setImageResource(R.drawable.screen_9);
                    _fullScreenImageIndex = 0;
                    _fullScreenImage.setVisibility(View.GONE);
                    _titleBar.showBar();
                    _titleBar.showBackground();
                    _commandBar.show(modeSarajevoCloud);
                }
            }
        });
        _fullScreenImage.setVisibility(View.GONE);

        RelativeLayout.LayoutParams lp_fsiw = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mainView.addView(_fullScreenImage, lp_fsiw);

        _rightFotografije = new ClickableTextView(
                getActivity()
                , "FOTOGRAFIJE"
                , new Command() {
            @Override
            public boolean execute() {

                _rightMenu.setVisibility(View.GONE);
                String secStore = System.getenv("SECONDARY_STORAGE");
                File file = new File(secStore + "/DCIM");
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i, 1);//android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                /*
                String secStore = System.getenv("SECONDARY_STORAGE");
                File file = new File(secStore + "/DCIM");
                 */
                return true;
            }
        }
                , defaultFont
                , R.color.zuta
                , R.color.zelena);
        _rightFotografije.setPadding(15, 15, 15, 10);
        _rightFotografije.setGravity(Gravity.LEFT);

        _rightInfo = new ClickableTextView(
                getActivity()
                , "UPUTE"
                , new Command() {
            @Override
            public boolean execute() {
                _titleBar.hideBar();
                _commandBar.hide();
                _rightMenu.setVisibility(View.GONE);
                _fullScreenImage.setVisibility(View.VISIBLE);
                _titleBar.showBackground();
                return false;
            }
        }
                , defaultFont
                , R.color.zuta
                , R.color.zelena
        );
        _rightInfo.setPadding(15, 15, 15, 10);
        _rightInfo.setGravity(Gravity.LEFT);

        _rightAbout = new ClickableTextView(
                getActivity()
                , "O PROJEKTU"
                , new Command() {
            @Override
            public boolean execute() {
                _rightMenu.setVisibility(View.GONE);
                if (_openInfoView != null) {
                    _openInfoView.execute();
                } else {
                    Log.e(LOG_TAG, "OpenInfoView not defined!");
                }
                return true;
            }
        }
                , defaultFont
                , R.color.zuta
                , R.color.zelena
        );
        _rightAbout.setPadding(15, 15, 15, 10);
        _rightAbout.setGravity(Gravity.LEFT);

        _rightMenu.addView(_rightInfo);
        _rightMenu.addView(_rightFotografije);
        _rightMenu.addView(_rightAbout);
        _rightMenu.setPadding(30, 0, 30, 20);

        initPiktogramChooser();

        final SharedPreferences settings = getActivity().getSharedPreferences(CREDENTIALS, 0);
        final Boolean firstTimeHere = settings.getBoolean("seenIntroInfo", false);

        if (firstTimeHere) {
            _titleBar.hideBar();
            _titleBar.hideBackground();
            _commandBar.hide();
            _fullScreenImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _fullScreenImage.setImageResource(R.drawable.screen_9);
                    _fullScreenImageIndex = 0;
                    _fullScreenImage.setVisibility(View.GONE);
                    _titleBar.showBar();
                    _titleBar.showBackground();
                    _commandBar.show(modeSarajevoCloud);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("seenIntroInfo", true);
                    editor.commit();
                }
            });
            _fullScreenImage.setVisibility(View.VISIBLE);
        } else {
            _fullScreenImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_fullScreenImageIndex == 0) {
                        _fullScreenImage.setImageResource(R.drawable.screen_12);
                        _fullScreenImageIndex = 1;
                    } else {
                        _fullScreenImage.setImageResource(R.drawable.screen_9);
                        _fullScreenImageIndex = 0;
                        _fullScreenImage.setVisibility(View.GONE);
                        _titleBar.showBar();
                        _titleBar.showBackground();
                        _commandBar.show(modeSarajevoCloud);
                    }
                }
            });
        }

        //TODO:back

        //showMessage("Dobro dosli " + Spremnik.getInstance().getUserName());
        //checkNewPiktogramHandler.postDelayed(checkNewPiktogramRunnable, 0);
    }

    private void initPiktogramChooser() {
        Context ctx = getActivity();
        _piktogramChooser = new ScrollView(ctx);
        _piktogramChooser_piktogramRows = new LinearLayout(ctx);
        _piktogramChooser_piktogramRows.setOrientation(LinearLayout.VERTICAL);
        _piktogramChooser_piktogramRows.setPadding(0, 15, 0, 10);

        _piktogramChooser_loader = new ProgressBar(ctx);

        _piktogramChooser.addView(_piktogramChooser_piktogramRows);
        _piktogramChooser_loader.setVisibility(View.GONE);

        //_messageBox.addView(_piktogramChooser);
        _piktogramChooser.setVisibility(View.GONE);
        getGuiSetup().addViewToBottom(_piktogramChooser);
    }

    private void showPiktogramChooser() {
        boolean thereAreVisibleSiblings = false;
        ViewGroup row = (ViewGroup) _piktogramChooser.getParent();
        for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
            if (row.getChildAt(itemPos).getVisibility() == View.VISIBLE) {
                thereAreVisibleSiblings = true;
                break;
            }
        }
        if (thereAreVisibleSiblings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPiktogramChooser();
                }
            }, 500);
            return;
        }
        //getGuiSetup().getMainContainerView().setBackgroundColor(Color.argb(128, 0, 0, 0));
        //if (_messageBox.getVisibility() != View.VISIBLE) _messageBox.setVisibility(View.VISIBLE);
        _commandBar.hide();

        _titleBar.showPiktogramChooserInfo(true);
        _titleBar.hideBackground();
        getGuiSetup().getMainContainerView().setBackgroundColor(Color.argb(128, 0, 0, 0));
        _piktogramChooser_piktogramRows.removeAllViews();
        _piktogramChooser_loader.setVisibility(View.VISIBLE);
        _piktogramChooser.getLayoutParams().height = (int) (getScreenWidth() * 0.7);
        _piktogramChooser.getLayoutParams().width = (int) (getScreenHeigth());
        _piktogramChooser.setLayoutParams(
                new LinearLayout.LayoutParams(
                        (int) (getScreenHeigth()),
                        (int) (getScreenWidth() * 0.6)
                )
        );
        _piktogramChooser.setVisibility(View.VISIBLE);
        _titleBar.setEnabled(false);

        final StopFlag stopFlag = new StopFlag();

        try {
                final Index j = new Index();
                final RunnableWithParams runnable_pokaziPiktogramGaleriju =
                        new RunnableWithParams() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray piktogrami = (JSONArray) m_args[0];
                                    JSONObject piktogram = piktogrami.getJSONObject(j.getIndex());
                                    final Piktogram newPiktogram = new Piktogram(piktogram.getInt("id"),
                                            piktogram.getString("naziv"),
                                            piktogram.getString("put_piktogram"),
                                            piktogram.getString("put_tekstura"),
                                            piktogram.getInt("color_red"),
                                            piktogram.getInt("color_green"),
                                            piktogram.getInt("color_blue"));
                                    String tmp = piktogram.getString("naziv") + "." + Utility.getEkstension(piktogram.getString("put_piktogram"));
                                    final String finalFileName = Utility.downloadAndSaveFile(getActivity(), piktogram.getInt("id"), 0, tmp, LOG_TAG);

                                    tmp = piktogram.getString("naziv") + "." + Utility.getEkstension(piktogram.getString("put_tekstura"));
                                    final String finalThumbnail_1 = Utility.downloadAndSaveFile(getActivity(), piktogram.getInt("id"), 1, tmp, LOG_TAG);

                                    final float W = getScreenHeigth();
                                    if (j.getIndex() % 5 == 0) {
                                        publishProgress();
                                    }
                                    final ImageView btnImage = createImageWithTransparentBackground(getActivity(), finalThumbnail_1,
                                            getActivity().getResources().getColor(R.color.zuta),
                                            getActivity().getResources().getColor(R.color.zelena),
                                            new Command() {
                                                @Override
                                                public boolean execute() {
                                                    stopFlag.podigni();
                                                    Log.i(LOG_TAG, "canceled: " + stopFlag.podignuta());

                                                    String defaultTextureName = Spremnik.getInstance().getUrl() + "/teksture/default.jpg";
                                                    _piktogramChooser.setVisibility(View.GONE);
                                                    _titleBar.showPiktogramChooserInfo(false);
                                                    getGuiSetup().getMainContainerView().setBackgroundColor(Color.argb(0, 0, 0, 0));
                                                    newObject(finalFileName, defaultTextureName, newPiktogram);
                                                    _titleBar.setTitle("POSTAVLJANJE OBJEKATA");
                                                    _commandBar.show(modeSarajevoCloud);
                                                    showPiktogramInfoIfNeeded();
                                                    return false;
                                                }

                                                private void showPiktogramInfoIfNeeded() {
                                                    final SharedPreferences settings = getActivity().getSharedPreferences(CREDENTIALS, 0);
                                                    final Boolean seenPiktogramInfo = settings.getBoolean("seenPiktogramInfo", false);
                                                    if (!seenPiktogramInfo) {
                                                        _fullScreenImage.setImageResource(R.drawable.screen_12);
                                                        _fullScreenImage.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                _fullScreenImage.setImageResource(R.drawable.screen_9);
                                                                _fullScreenImageIndex = 0;
                                                                _fullScreenImage.setVisibility(View.GONE);
                                                                _titleBar.showBar();
                                                                _titleBar.showBackground();
                                                                _commandBar.show(modeSarajevoCloud);
                                                                SharedPreferences.Editor editor = settings.edit();
                                                                editor.putBoolean("seenPiktogramInfo", true);
                                                                editor.commit();
                                                            }
                                                        });
                                                        _fullScreenImage.setVisibility(View.VISIBLE);
                                                        _titleBar.hideBar();
                                                        _titleBar.hideBackground();
                                                        _commandBar.hide();
                                                    }
                                                }
                                            });

                                    final TableRow.LayoutParams mlp = new TableRow.LayoutParams((int) (W * 0.18), (int) (W * 0.18));
                                    mlp.setMargins((int) (W * 0.01), (int) (W * 0.01), (int) (W * 0.01), (int) (W * 0.01));

                                    publishProgress(btnImage, mlp, (int) (W * 0.18));
                                    j.increment();
                                    if (j.getIndex() < piktogrami.length() && !stopFlag.podignuta())
                                        new Handler().postDelayed(this, 1);
                                    else
                                        j.reset();
                                } catch (Throwable t) {

                                }
                            }
                        };
            String json = new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    return Utility.GET(params[0]);
                }
            }.execute(Spremnik.getInstance().getSetServiceAddress()).get();

            android.util.Log.d(LOG_TAG, "json: " + json);
            final JSONArray setovi = new JSONArray(json);
            //for (int i = 0; i < setovi.length() && !stopFlag.podignuta(); i++) {
            final Index i = new Index();
            RunnableWithParams runnable_dobaviSetove = new RunnableWithParams() {
                @Override
                public void run() {
                    try {
                        if(j.getIndex() != 0) {
                            new Handler().postDelayed(this, 250);
                            return;
                        }
                    JSONObject set = setovi.getJSONObject(i.getIndex());
                    android.util.Log.d(LOG_TAG, "set(" + i.getIndex() + "): " + set.toString());
                    //if(isCancelled()) break;

                    publishProgress(set.getString("naziv"));
                        //String json_piktogrami = Utility.GET(Spremnik.getInstance().getPiktogramServiceAddress() + "?setid=" + Integer.toString(set.getInt("id")));
                        String json_piktogrami = new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                return Utility.GET(params[0]);
                            }
                        }.execute(Spremnik.getInstance().getPiktogramServiceAddress() + "?setid=" + Integer.toString(set.getInt("id")))
                                .get();
                        //android.util.Log.d(LOG_TAG, "json: " + json_piktogrami);
                        final JSONArray piktogrami = new JSONArray(json_piktogrami);
                        //for (int j = 0; j < piktogrami.length()&& !stopFlag.podigunta(); j++) {
                        RunnableWithParams copy = (RunnableWithParams) runnable_pokaziPiktogramGaleriju.clone();
                        copy.setArguments(piktogrami);
                        new Handler().postDelayed(copy, 0);
                    } catch (org.json.JSONException je) {
                        Log.e("PiktogramChooser", je.toString());
                    } catch (Throwable ex) {
                        android.util.Log.d("PiktogramChooser", ex.getMessage(), ex);
                    }
                    i.increment();
                    if(i.getIndex() < setovi.length() && !stopFlag.podignuta())
                        new Handler().postDelayed(this, 100);
                }
            };
            new Handler().postDelayed(runnable_dobaviSetove, 0);
        } catch (org.json.JSONException je) {
            Log.e("LOG_piktogramchooser", je.toString());
        } catch (Throwable ex) {
            android.util.Log.d(LOG_TAG, "error: " + ex.getMessage(), ex);
        }
    }

    //protected void onProgressUpdate(Object... values) {
    protected void publishProgress(final Object... values) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (values.length == 3) {
                    ImageView btnImage = (ImageView) values[0];
                    TableRow.LayoutParams mlp = (TableRow.LayoutParams) values[1];
                    int wid = (int) values[2];

                    btnImage.setMaxWidth(wid);
                    btnImage.setMaxHeight(wid);

                    if (_piktogramChooser_piktogramRows == null || _piktogramChooser_piktogramRows.getChildCount() == 0)
                        return;
                    ((LinearLayout) (_piktogramChooser_piktogramRows
                            .getChildAt(_piktogramChooser_piktogramRows.getChildCount() - 1)))
                            .addView(btnImage);
                    btnImage.setLayoutParams(mlp);
                    btnImage.requestLayout();
                    //}else if(values[0]!=null) {
                } else if (values.length == 1) {
                    String setName = (String) values[0];
                    TextView naslovSeta = new TextView(getActivity());
                    naslovSeta.setTypeface(defaultFont);
                    naslovSeta.setText(setName);
                    naslovSeta.setTextSize(19);
                    naslovSeta.setPadding(25, 7, 0, 7);
                    naslovSeta.setTextColor(Color.rgb(242, 229, 0));
                    _piktogramChooser_piktogramRows.addView(naslovSeta);
                } else {
                    LinearLayout noviRed = new LinearLayout(getActivity());
                    noviRed.setOrientation(LinearLayout.HORIZONTAL);
                    _piktogramChooser_piktogramRows.addView(noviRed);
                }
            }
        });
    }

    class StopFlag {
        AtomicReference<Boolean> _podigunta = new AtomicReference<>(false);

        public boolean podignuta() {
            return _podigunta.get();
        }

        public void podigni() {
            _podigunta.getAndSet(true);
        }
    }

    class Index {
        int i = 0;

        public int getIndex() {
            return i;
        }

        public int increment() {
            i++;
            return i;
        }

        public int decrement() {
            i--;
            return i;
        }

        public void reset() {
            i = 0;
        }
    }

    abstract class RunnableWithParams implements Runnable, Cloneable {

        Object[] m_args;

        public RunnableWithParams() {
        }

        public void run(Object... args) {
            m_args = args;
            run();
        }

        public void setArguments(Object... args) {
            m_args = args;
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }
    //};
    //_dobaviPiktogrameAsync.execute();
    //new Thread(runnable_dobaviPiktogrameAsync).run();

    //}

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
        if (lat.length() > 8)
            lat = lat.substring(0, 9);
        if (lon.length() > 8)
            lon = lon.substring(0, 9);
        return "{ " + lat + "; " + lon + "; " + Double.toString(l.getAltitude()) + " }";
    }

    /**
     * Snima novododani objekat
     *
     * @param newObjectFilename    - lokacija za .obj novog piktograma
     * @param newObjectTexturename - lokacija za teksturu novog piktograma
     * @param newPiktogram
     **/
    private Obj newObject(String newObjectFilename, String newObjectTexturename, final Piktogram newPiktogram) {
        final Obj lightObject = new Obj();
        lightObject.setPosition(new Vec(0, 0, -1));
        spotLight.setPosition(new Vec(0, 0, 2));
        final MeshComponent lightGroup = new Shape();
        lightGroup.addChild(spotLight);
        lightObject.setComp(lightGroup);
        lightObject.setComp(new MoveComp(1));
        //lightObject.setComp(_moveComp);

        final ModelLoader model = new ModelLoader(_localRenderer, newObjectFilename, newObjectTexturename) {
            @Override
            public void modelLoaded(final MeshComponent gdxMesh) {
                _selectedMesh = gdxMesh;
                _selectedMesh.setRotation(new Vec(0, 0, 0));
                _selectedLightObject = lightObject;
                Log.d(LOG_TAG, "Loaded mesh component from GDX");
                final gl.Color tmpColor = newPiktogram.get_color();
                tmpColor.alpha = 0.5f;
                gdxMesh.setColor(tmpColor);
                lightObject.setComp(gdxMesh);
                lightObject.setComp(_viewPosCalcer);
                Log.d(LOG_TAG, "CAMERA LOCATION: " + camera.getGPSLocation().toString());
                world.add(lightObject);
                _targetMoveWrapper.setTo(lightObject);
                /*lightObject.setPosition(new Vec(0, 0, -50));
                if (_targetMoveWrapper.getObject() instanceof Obj) {
                    ((Obj) _targetMoveWrapper.getObject())
                            .getComp(MoveComp.class).myTargetPos.z = -55f;
                }*/

            }
        };
        try {
            Utility.SaveThisPiktogram(camera.getGPSLocation());
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
     */
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

            if (noviPiktogrami != null && noviPiktogrami.size() > 0) {
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
            if (slikaPath != null && !slikaPath.equals("")) {
                Spremnik.getInstance().get_slikaPath().getAndSet("");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showScreenshotThumbnail(slikaPath);
                        _uploadPictureDialogBox.showDialog("POŠALJI FOTOGRAFIJU NA FACEBOOK STRANICU PROJEKTA ?",
                                new Command() {
                                    @Override
                                    public boolean execute() {
                                        try {
                                            new AsyncTask<Void, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(Void... params) {
                                                    try {
                                                        Utility.uploadScreenshoot(ModelLoaderSetup.this, slikaPath, camera.getGPSLocation());
                                                    } catch (Throwable t1) {
                                                        Log.e("uploadScreen", t1.toString());
                                                        t1.printStackTrace();
                                                    }
                                                    return null;
                                                }
                                            }.execute().get();
                                        } catch (Throwable t) {
                                        }
                                        hidePopup();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                _loader.showLoader("POSTING TO FACEBOOK");
                                            }
                                        });
                                        return false;
                                    }
                                },
                                new Command() {
                                    @Override
                                    public boolean execute() {
                                        try {
                                            new AsyncTask<String, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    try {
                                                        String str = Utility.POST(Spremnik.getInstance().getUploadPictureServiceAddress(), new ArrayList<NameValuePair>(0));
                                                    } catch (Throwable thr) {
                                                        thr.printStackTrace();
                                                    }
                                                    return null;
                                                }
                                            }.execute("");
                                        } catch (Throwable t) {
                                            Log.w("UploadPicture", t.toString());
                                        }
                                        hidePopup();
                                        return false;
                                    }
                                });
                    }
                });
            } else {
                pictureHandler.postDelayed(this, 100);
            }
        }
    };

    private void hidePopup() {
        //_popupWindow.setVisibility(View.GONE);
        _thumbnailImage.setVisibility(View.GONE);
        //_commandBar.show(modeSarajevoCloud);
    }

    private void showScreenshotThumbnail(String slikaPath) {
        Bitmap thumbnail = BitmapFactory.decodeFile(slikaPath);
        _thumbnailImage.setImageBitmap(thumbnail);
        _thumbnailImage.setVisibility(View.VISIBLE);
        //_popupWindow.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume(Activity a) {
        super.onResume(a);
        String objPut = Spremnik.getInstance().getObjekatPut(),
                tekPut = Spremnik.getInstance().getTeksturaPut();

        if (objPut != null && tekPut != null) {
            Spremnik.getInstance().setObjekatPut(null);
            Spremnik.getInstance().setTeksturaPut(null);

            //fileName = objPut;
            //textureName = tekPut;
            //newObject(objPut, tekPut, newPiktogram);
            //loadedObject(objPut, tekPut, camera.getGPSLocation());
        }
        /*String world_gson = "";
        SharedPreferences settings = getActivity().getSharedPreferences(CREDENTIALS, 0);
        world_gson = settings.getString("world", "");
        if(world_gson != null && !world_gson.equals("")){
            Gson gson = new Gson();
            world = gson.fromJson(world_gson, World.class);
        }*/
    }

    @Override
    public void onStart(Activity a) {
        super.onStart(a);
    }

    @Override
    public void onStop(Activity a) {
        super.onStop(a);
        if (pictureHandler != null && pictureRunnable != null)
            pictureHandler.removeCallbacks(pictureRunnable);
        if (checkNewPiktogramHandler != null && checkNewPiktogramRunnable != null)
            checkNewPiktogramHandler.removeCallbacks(checkNewPiktogramRunnable);
        /*Gson gson = new Gson();
        String world_gson = gson.toJson(world);
        SharedPreferences settings = getActivity().getSharedPreferences(CREDENTIALS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("world", world_gson);
        editor.commit();*/
    }

    @Override
    public boolean onKeyDown(Activity a, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_BACKSLASH:
                    _timesBackPressed++;

                    //if( getGuiSetup().getMainContainerView().getBack )
                    getGuiSetup().getMainContainerView().setBackgroundColor(Color.argb(0, 0, 0, 0));
                    _titleBar.showBackground();

                    if (!modeSarajevoCloud) {
                        if (_selectedMesh != null && _selectedLightObject != null) {
                            world.remove(_selectedLightObject);
                            _selectedMesh = null;
                            _selectedLightObject = null;
                            _titleBar.setTitle("SARAJEVO CLOUD");
                            modeSarajevoCloud = true;
                            _commandBar.show(modeSarajevoCloud);
                            _timesBackPressed = 0;
                        }
                    }

                    if (_fullScreenImage.getVisibility() == View.VISIBLE) {
                        _fullScreenImage.setImageResource(R.drawable.screen_9);
                        _fullScreenImageIndex = 0;
                        _fullScreenImage.setVisibility(View.GONE);
                        _titleBar.showBar();
                        _commandBar.show(modeSarajevoCloud);
                        _timesBackPressed = 0;
                    }

                    if (_rightMenu.getVisibility() == View.VISIBLE) {
                        _rightMenu.setVisibility(View.GONE);
                        _commandBar.show(modeSarajevoCloud);
                        _timesBackPressed = 0;
                    }

                    if (_piktogramChooser.isShown()) {
                        if (_dobaviPiktogrameAsync != null)
                            _dobaviPiktogrameAsync.cancel(true);
                        _titleBar.showPiktogramChooserInfo(false);
                        modeSarajevoCloud = true;
                        _commandBar.show(modeSarajevoCloud);
                        _piktogramChooser.setVisibility(View.GONE);
                        getGuiSetup().getMainContainerView().setBackgroundColor(Color.argb(0, 0, 0, 0));
                        _timesBackPressed = 0;
                    }

                    if (_thumbnailImage.getVisibility() == View.VISIBLE) {
                        //_popupWindow.setVisibility(View.GONE);
                        //_mojCloudKomande.show(modeSarajevoCloud);
                        _uploadPictureDialogBox.hideDialog();
                        _timesBackPressed = 0;
                        _messageBox.showMessage("FOTOGRAFIJA SNIMLJENA NA VAŠ UREDJAJ");
                    }

                    if (_timesBackPressed > 0) {
                        //_messageBox_TextView.setText("Press back once more to exit.");
                        //_messageBox.setVisibility(View.VISIBLE);
                        if (_timesBackPressed > 1)
                            getActivity().finish();// return super.onKeyDown(a, keyCode, event);
                        if (_timesBackPressed == 1)
                            Toast.makeText(getActivity(), "Press back once more to exit", Toast.LENGTH_SHORT).show();
                    }

                    //getGuiSetup().getRightView().setGravity(Gravity.CENTER_VERTICAL);

                    return true;
                default:
                    break;
            }
        }
        return false;

        //return super.onKeyDown(a, keyCode, event);
    }

    @Override
    public void onDestroy(Activity a) {
        super.onDestroy(a);
    }

    public View createButtonImageWithTransparentBackground(Context context, final int normalImageId, final int clickedImageId,
                                                           final Command command) {
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
                                                     final Command command) {
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

    public ImageView createImageWithTransparentBackground(Context context, String normalImagePath,
                                                          @ColorInt final int normalImageBackgroundColor,
                                                          @ColorInt final int clickedImageBackgroundColor,
                                                          final Command command) {
        final ImageView imgButton = new ImageView(context);
        try {
            imgButton.setBackgroundColor(0);
            System.gc();
            final Bitmap normalBmp = BitmapFactory.decodeFile(normalImagePath);
            System.gc();
            imgButton.setImageBitmap(normalBmp);
            imgButton.setBackgroundColor(normalImageBackgroundColor);
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imgButton.setBackgroundColor(clickedImageBackgroundColor);
                    if (vibrateCommand != null)
                        vibrateCommand.execute();
                    if (command != null) {
                        new AsyncTask<Command, Void, Void>() {
                            @Override
                            protected Void doInBackground(Command... params) {
                                //new Handler().postDelayed(
                                //        new Runnable() {
                                //            @Override
                                //            public void run() {
                                getActivity().runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                command.execute();
                                            }
                                        });
                                //            }
                                //        }, 50);
                                return null;
                            }
                        }.execute(command);
                    }
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    imgButton.setBackgroundColor(normalImageBackgroundColor);
                                                }
                                            });
                                }
                            }, 500);
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return imgButton;
    }
}