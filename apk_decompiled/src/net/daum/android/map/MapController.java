package net.daum.android.map;

import net.daum.android.map.coord.MapCoord;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.p000n.api.NativeMapCoord;
import net.daum.mf.map.p000n.api.internal.NativeMapController;
import net.daum.mf.map.task.MainQueueManager;
import net.daum.mf.map.task.MapTaskManager;

public final class MapController {
    public static final int MAP_LAYER_TYPE_MAP = 100;
    public static final int MAP_LAYER_TYPE_ROAD_VIEW = 300;
    public static final int MAP_LAYER_TYPE_TRAFFIC = 200;
    public static final int MAP_LAYER_TYPE_UNDEFINED = 0;
    public static final int MAP_TILE_MODE_HD = 100;
    public static final int MAP_TILE_MODE_HD_2X = 200;
    public static final int MAP_TILE_MODE_NORMAL = 0;
    public static final int MAP_VIEW_TYPE_HYBRID = 3;
    public static final int MAP_VIEW_TYPE_IMAGE = 1;
    public static final int MAP_VIEW_TYPE_SKY = 2;
    public static final int MAP_VIEW_TYPE_UNDEFINED = 0;
    private static MapController instance = new MapController();
    private NativeMapController nativeMapController = new NativeMapController();

    public static MapController getInstance() {
        return instance;
    }

    private MapController() {
    }

    public boolean isMapEnable() {
        return this.nativeMapController.isMapEnable();
    }

    public void setMapEnable(boolean mapEnable) {
        this.nativeMapController.setMapEnable(mapEnable);
    }

    public void move(final MapCoord coord) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.move(new NativeMapCoord(coord));
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void move2(MapCoord coord) {
        this.nativeMapController.move(new NativeMapCoord(coord));
    }

    public void move(final MapCoord coord, final boolean animated) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setMapCenterPoint(new NativeMapCoord(coord), animated);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void move(final MapCoord coord, final int level) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.move(new NativeMapCoord(coord), level);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void move(final MapCoord coord, final float level, final boolean animated) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setMapCenterPointAndZoomLevel(new NativeMapCoord(coord), level, animated);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void moveToViewMarker(final MapCoord coord) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.moveToViewMarker(new NativeMapCoord(coord));
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setViewType(final int viewType) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setViewType(viewType);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public boolean isHDScreen() {
        return this.nativeMapController.isHDScreen();
    }

    public boolean isFullHDScreen() {
        return this.nativeMapController.isFullHDScreen();
    }

    public boolean isHDMapTileEnabled() {
        return this.nativeMapController.isHDMapTileEnabled();
    }

    public void setHDMapTileEnabled(final boolean enabled, boolean forceMapEngineMainThread) {
        if (forceMapEngineMainThread) {
            MapTaskManager.getInstance().queueTask(new Runnable() {
                public void run() {
                    MapController.this.nativeMapController.setHDMapTileEnabled(enabled);
                }
            }, MapEngineManager.getInstance().getStopGlSwap());
            return;
        }
        this.nativeMapController.setHDMapTileEnabled(enabled);
    }

    public int getMapTileMode() {
        return this.nativeMapController.getMapTileMode();
    }

    public void setMapTileMode(final int tileMode, boolean forceMapEngineMainThread) {
        if (forceMapEngineMainThread) {
            MapTaskManager.getInstance().queueTask(new Runnable() {
                public void run() {
                    MapController.this.nativeMapController.setMapTileMode(tileMode);
                }
            }, MapEngineManager.getInstance().getStopGlSwap());
            return;
        }
        this.nativeMapController.setMapTileMode(tileMode);
    }

    public int getViewType() {
        return this.nativeMapController.getViewType();
    }

    public void setUseLayer(final int layerType, final boolean use) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setUseLayer(layerType, use);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public boolean isUseLayer(int layerType) {
        return this.nativeMapController.isUseLayer(layerType);
    }

    public void changeGroundScaleWithAnimation(final float scale, final boolean animation) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.changeGroundScaleWithAnimation(scale, animation);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setNeedsRefreshTiles() {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setNeedsRefreshTiles();
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void clearTiles() {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.clearTiles();
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void releaseUnusedMapTileImageResources() {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.releaseUnusedMapTileImageResources();
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MapCoord getCurrentMapViewpoint() {
        NativeMapCoord mapCoord = this.nativeMapController.getCurrentMapViewpoint();
        if (mapCoord != null) {
            return mapCoord.toMapCoord();
        }
        return MapCoord.UNDEFINED;
    }

    public MapPointBounds getCurrentMapBounds() {
        NativeMapCoord beginCoord = this.nativeMapController.getCurrentMapBoundsBeginPoint();
        NativeMapCoord endCoord = this.nativeMapController.getCurrentMapBoundsEndPoint();
        if (beginCoord == null || endCoord == null) {
            return null;
        }
        MapCoord begin = beginCoord.toMapCoord();
        MapCoord end = endCoord.toMapCoord();
        return new MapPointBounds(MapPoint.mapPointWithWCONGCoord(begin.getX(), begin.getY()), MapPoint.mapPointWithWCONGCoord(end.getX(), end.getY()));
    }

    public MapCoord getDestinationMapViewpoint() {
        NativeMapCoord mapCoord = this.nativeMapController.getDestinationMapViewpoint();
        if (mapCoord != null) {
            return mapCoord.toMapCoord();
        }
        return MapCoord.UNDEFINED;
    }

    public float getZoom() {
        return this.nativeMapController.getZoom();
    }

    public float getZoomLevelFloat() {
        return this.nativeMapController.getZoomLevel();
    }

    public int getZoomLevelInt() {
        return Math.round(this.nativeMapController.getZoomLevel());
    }

    public void setZoom(final float zoom) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setZoom(zoom);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setZoomLevel(final float zoomLevel, final boolean animated) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setZoomLevel(zoomLevel, animated);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void zoomIn(final boolean animated) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.zoomIn(animated);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void zoomOut(final boolean animated) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.zoomOut(animated);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public float getMapRotationAngle() {
        return this.nativeMapController.getMapRotationAngle();
    }

    public void setMapRotationAngle(final float angle, final boolean animated) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setMapRotationAngle(angle, animated);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MapCoord getCurrentPointingCoord() {
        return this.nativeMapController.getCurrentPointingCoord().toMapCoord();
    }

    public void fitMapViewAreaToShowAllMapPoints(final NativeMapCoord[] mapCoords) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.fitMapViewAreaToShowAllMapPoints(mapCoords);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void updateCameraWithMapPointAndDiameter(final MapCoord mapCoord, final float diameter) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.updateCameraWithMapPointAndDiameter(new NativeMapCoord(mapCoord), diameter);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void updateCameraWithMapPointAndDiameterAndPadding(final MapCoord mapCoord, final float diameter, final int padding) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.updateCameraWithMapPointAndDiameterAndPadding(new NativeMapCoord(mapCoord), diameter, padding);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void updateCameraWithMapPoints(final NativeMapCoord[] mapCoords) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.updateCameraWithMapPoints(mapCoords);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void updateCameraWithMapPointsAndPadding(final NativeMapCoord[] mapCoords, final int padding) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.updateCameraWithMapPointsAndPadding(mapCoords, padding);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    /* renamed from: updateCameraWithMapPointsAndPaddingAndMinZoomLevelAndMaxZoomLevel */
    public void m3x6d3014d3(NativeMapCoord[] mapCoords, int padding, float minZoomLevel, float maxZoomLevel) {
        final NativeMapCoord[] nativeMapCoordArr = mapCoords;
        final int i = padding;
        final float f = minZoomLevel;
        final float f2 = maxZoomLevel;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.m4x6d3014d3(nativeMapCoordArr, i, f, f2);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setUseHeading(final boolean use) {
        MainQueueManager.getInstance().queueToMainQueue(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setUseHeading(use);
            }
        });
    }

    public void setMapGroundAngleWithAnimation(final float angle, final boolean animation) {
        MainQueueManager.getInstance().queueToMainQueue(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.setMapGroundAngleWithAnimation(angle, animation);
            }
        });
    }

    public float getBestZoom(MapCoord origin, MapCoord coordFromDistances) {
        return this.nativeMapController.getBestZoom(new NativeMapCoord(origin), new NativeMapCoord(coordFromDistances));
    }

    public void resetMapTileCache() {
        MainQueueManager.getInstance().queueToMainQueue(new Runnable() {
            public void run() {
                MapController.this.nativeMapController.resetMapTileCache();
            }
        });
    }

    public void resetMapTileCacheRunOnCurrentThread() {
        this.nativeMapController.resetMapTileCache();
    }

    public MapCoord convertMapCoordToGraphicPixelCoord(MapCoord mapCoord) {
        return this.nativeMapController.convertMapCoordToGraphicPixelCoord(new NativeMapCoord(mapCoord)).toMapCoord();
    }

    public MapCoord convertGraphicPixelCoordToMapCoord(MapCoord mapCoord) {
        return this.nativeMapController.convertGraphicPixelCoordToMapCoord(new NativeMapCoord(mapCoord)).toMapCoord();
    }

    public void startReceivingTileCommand(MapCoord mapCoord, float level) {
        this.nativeMapController.startReceivingTileCommand(new NativeMapCoord(mapCoord), level);
    }
}
