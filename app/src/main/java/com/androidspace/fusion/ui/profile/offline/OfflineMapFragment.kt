package com.androidspace.fusion.ui.profile.offline

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.data.model.PointData
import com.androidspace.fusion.databinding.FragmentOfflineMapBinding
import com.androidspace.fusion.databinding.OfflineMapDialogBinding
import com.androidspace.fusion.ui.common.*
import com.androidspace.fusion.ui.profile.data.OnOffline
import com.androidspace.fusion.ui.route.data.OnMap
import com.androidspace.fusion.utils.screenHeight
import com.androidspace.fusion.utils.screenWidth
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.security.AuthenticationChallengeHandler
import com.esri.arcgisruntime.security.AuthenticationManager
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import kotlin.math.roundToInt

@RuntimePermissions
@Layout(R.layout.fragment_offline_map)
class OfflineMapFragment : BaseFragment<FragmentOfflineMapBinding, OfflineMapViewModel>(), OnMap, OnProgress, OnAddress, OnMarker, OnOffline{
    private val TAG = OfflineMapFragment::class.java.simpleName
    private val grOverlay: GraphicsOverlay by lazy { GraphicsOverlay() }
    private var dataDialog: AlertDialog? = null
    private var dataBinding: OfflineMapDialogBinding? = null
    private var onBottomBarVisible: OnBottomBarVisible? = null
    private var screenTopLeft = android.graphics.Point()
    private var screenTopRight = android.graphics.Point()
    private var screenBottomLeft = android.graphics.Point()
    private var screenBottomRight = android.graphics.Point()
    private var onMapLongClick: OnMapLongClick? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBottomBarVisible = context as OnBottomBarVisible
    }

    override fun onDetach() {
        onBottomBarVisible = null
        super.onDetach()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler: AuthenticationChallengeHandler = DefaultAuthenticationChallengeHandler(requireActivity())
        AuthenticationManager.setAuthenticationChallengeHandler(handler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AuthenticationManager.setTrustAllSigners(true)
        onBottomBarVisible?.onBottomBarVisible(true)
        binding.mapView.graphicsOverlays.add(grOverlay)
        //binding.mapView.locationDisplay.locationDataSource = createMock()
        val screenLeftX = (resources.getDimension(R.dimen.big)).toInt()
        val screenTopY = (resources.getDimension(R.dimen.big)+resources.getDimension(R.dimen.add_top)).toInt()
        val screenRightX = (requireContext().screenWidth - resources.getDimension(R.dimen.big)).toInt()
        val screenBottomY = (requireContext().screenHeight - (resources.getDimension(R.dimen.big)+resources.getDimension(R.dimen.add_bottom))).toInt()
        screenTopLeft.x = screenLeftX
        screenTopLeft.y = screenTopY
        screenTopRight.x = screenRightX
        screenTopRight.y = screenTopY
        screenBottomLeft.x = screenLeftX
        screenBottomLeft.y = screenBottomY
        screenBottomRight.x = screenRightX
        screenBottomRight.y = screenBottomY
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onMap = this
        viewModel.onProgress = this
        viewModel.onMarker = this
        viewModel.onAddress = this
        viewModel.onOffline = this
        binding.model = viewModel
        arguments?.let {
            val address = it.getString("address")
            address?.let {
                viewModel.address = it
                binding.edAddress.setText(viewModel.address)
            }?: kotlin.run {
                viewModel.address = null
            }
            val point = it.getParcelable<PointData>("point")
            point?.let {
                viewModel.point = it
            }?: kotlin.run {
                viewModel.point = null
            }
        }?: kotlin.run {
            viewModel.point = null
            viewModel.address = null
        }
        onKeyboadVisibleChanged?.setKeyboarVisible(false)
        startLoadWithPermissionCheck()
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }
    override fun onPause() {
        binding.mapView.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.mapView.dispose()
        super.onDestroyView()
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun startLoad(){
        viewModel.loadMap()
    }
    @OnPermissionDenied(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onDenied(){
        binding.mapView.locationDisplay.stop()
        viewModel.loadMap()
    }
    @OnNeverAskAgain(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onNever(){
        binding.mapView.locationDisplay.stop()
        viewModel.loadMap()
    }
    override fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer?) {
        map?.let {
            binding.mapView.map = map
            for(l in binding.mapView.map.operationalLayers){
                Log.d(TAG, "Layer name:"+l.name+", id:"+l.id+" visible:"+l.isVisible+", item: "+l.item.itemId)
            }
            binding.mapView.post {
                binding.mapView.apply {
                    viewModel.point?.let {
                        onKeyboadVisibleChanged?.setKeyboarVisible(false)
                        val p: Point = Point(it.latitude, it.longitude, SpatialReferences.getWgs84())
                        setViewpointAsync(Viewpoint(p, 20000.0))
                        onMapLongClick?.onMapLongClick(p)
                    }?: kotlin.run {
                            locationDisplay.apply {
                                autoPanMode = LocationDisplay.AutoPanMode.RECENTER
                                initialZoomScale = 50000.0
                                addDataSourceStatusChangedListener {
                                    if(it.isStarted){
                                        location?.position?.let {
                                            val pp = GeometryEngine.normalizeCentralMeridian(it) as Point
                                            viewModel.getAddress(pp)
                                        }
                                    }
                                }
                            }.startAsync()
                    }
                    onTouchListener = object : DefaultMapViewOnTouchListener(requireContext(), this) {
                        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                            val tappedPoint = android.graphics.Point(event.x.roundToInt(), event.y.roundToInt())
                            val mapPoint = binding.mapView.screenToLocation(tappedPoint)
                            return true
                        }
                    }
                }
            }
        }
    }
    override fun onAddMaker(marker: Graphic) {
        grOverlay.graphics.clear()
        grOverlay.graphics.add(marker)
    }
    override fun onAddress(address: String) {
        binding.edAddress.setText(address)
    }
    override fun onProgerssVisible(visible: Boolean) {
        binding.prBar.isVisible = visible
    }

    override fun onOfflineShow(dirname: String?) {
        createData(dirname)
    }

    override fun onCancel() {
        dataDialog?.dismiss()
    }

    override fun onSuccess() {
        dataDialog?.dismiss()
        viewModel.onBackPressed()
    }

    override fun onFail(message: String) {
        dataDialog?.dismiss()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onProgress(progress: Int) {
        dataBinding?.prMap?.progress = progress
        dataBinding?.tvMap?.setText(progress.toString()+" %")
    }

    fun createData(folder: String?){
        viewModel.mapTopLeft = binding.mapView.screenToLocation(screenTopLeft)
        viewModel.mapTopRight = binding.mapView.screenToLocation(screenTopRight)
        viewModel.mapBottomLeft = binding.mapView.screenToLocation(screenBottomLeft)
        viewModel.mapBottomRight = binding.mapView.screenToLocation(screenBottomRight)
        showCreate(folder)
    }
    fun showCreate(folder: String?){
        dataDialog = AlertDialog.Builder(requireContext()).create()
        val inf = LayoutInflater.from(requireContext())
        dataBinding = DataBindingUtil.inflate<OfflineMapDialogBinding>(inf, R.layout.offline_map_dialog, null, false)
        dataBinding?.model = viewModel
        dataBinding?.edMapName?.setText(folder)
        dataDialog?.setView(dataBinding?.root)
        dataDialog?.setCancelable(false)
        dataDialog?.show()
    }
}